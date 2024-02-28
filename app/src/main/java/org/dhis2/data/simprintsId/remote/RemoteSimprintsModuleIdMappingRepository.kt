package org.dhis2.data.simprintsId.remote

import android.annotation.SuppressLint
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

class RemoteSimprintsModuleIdMappingRepository @Inject constructor(
    private val d2: D2,
) {

    /*
    Gets a map of DHIS2 Organisation Unit UIDs to Simprints Module IDs from DHIS2 API
    with a D2-provided Retrofit HTTP client already configured with the current auth session,
    given that a dynamic attribute named ModuleID is defined,
    in 2 steps - example:
    from GET /api/attributes.json?paging=false&fields=id,displayName&filter=displayName:eq:ModuleID
    response
    {
        "attributes": [
            {
                "id": "ModuleIdAttributeUid",
                "displayName": "ModuleID"
            }
        ]
    }
    gets "ModuleIdAttributeUid",
    and then, using ModuleIdAttributeUid,
    from GET /api/organisationUnits.json?paging=false&fields=id,attributeValues[attribute[id],value]&filter=attributeValues.attribute.id:eq:ModuleIdAttributeUid
    response
    {
        "organisationUnits": [
            {
                "id": "OrganisationUnitUid",
                "attributeValues": [
                    {
                        "attribute": {
                            "id": "ModuleIdAttributeUid"
                        },
                        "value": "ModuleIdAttributeValue"
                    }
                ]
            }
        ]
    }
    gets mapOf("OrganisationUnitUid" to "ModuleIdAttributeValue")
     */
    @SuppressLint("VisibleForTests")
    fun getOrgUnitUidToModuleIdMap(): Result<Map<String, String>> {
        val service = d2.retrofit().create(SimprintsDataMappingApiService::class.java)

        val moduleIdAttributeCall = service.getAttributes(
            paging = false,
            fields = "id,displayName",
            filter = "displayName:eq:ModuleID",
        )
        val moduleIdAttributeUid = with(moduleIdAttributeCall.execute()) {
            takeIf { isSuccessful }?.body()
                ?.attributes?.first()?.id
                ?: return Result.failure(
                    SimprintsDataMappingApiException("Attribute API: ${code()} (${message()})"),
                )
        }

        val orgUnitsCall = service.getOrganisationUnits(
            paging = false,
            fields = "id,attributeValues[attribute[id],value]",
            filter = "attributeValues.attribute.id:eq:$moduleIdAttributeUid",
        )
        val orgUnitUidToModuleIdPairs = with(orgUnitsCall.execute()) {
            takeIf { isSuccessful }?.body()?.organisationUnits?.mapNotNull { orgUnit ->
                orgUnit.attributeValues.find { it.attribute.id == moduleIdAttributeUid }
                    ?.run { orgUnit.id to value }
            } ?: return Result.failure(
                SimprintsDataMappingApiException("OrgUnits API: ${code()} (${message()})"),
            )
        }

        return Result.success(orgUnitUidToModuleIdPairs.toMap())
    }
}
