package org.dhis2.data.simprintsId.remote

import android.annotation.SuppressLint
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

class RemoteSimprintsModuleIdMappingRepository @Inject constructor(
    private val d2: D2,
) {

    @SuppressLint("VisibleForTests")
    fun getOrgUnitUidToModuleIdMap(): Result<Map<String, String>> {
        val service = d2.retrofit().create(SimprintsDataMappingApiService::class.java)

        val moduleIdAttributeCall = service.getAttributes(
            paging = false,
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
            fields = "id,displayName,$moduleIdAttributeUid~rename(moduleId)",
            filter = "attributeValues.attribute.id:eq:$moduleIdAttributeUid",
        )
        val orgUnitUidToModuleIdMap = with(orgUnitsCall.execute()) {
            takeIf { isSuccessful }?.body()
                ?.organisationUnits?.associate { it.id to it.moduleId }
                ?: return Result.failure(
                    SimprintsDataMappingApiException("OrgUnits API: ${code()} (${message()})"),
                )
        }

        return Result.success(orgUnitUidToModuleIdMap)
    }
}
