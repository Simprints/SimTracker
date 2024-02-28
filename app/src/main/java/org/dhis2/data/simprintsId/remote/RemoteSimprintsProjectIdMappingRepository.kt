package org.dhis2.data.simprintsId.remote

import android.annotation.SuppressLint
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

class RemoteSimprintsProjectIdMappingRepository @Inject constructor(
    private val d2: D2,
) {

    /*
    Gets a map of DHIS2 Program UIDs to Simprints Project IDs from DHIS2 API
    with a D2-provided Retrofit HTTP client already configured with the current auth session,
    given that a dynamic attribute named ProjectID is defined,
    in 2 steps - example:
    from GET /api/attributes.json?paging=false&fields=id,displayName&filter=displayName:eq:ProjectID
    response
    {
        "attributes": [
            {
                "id": "ProjectIdAttributeUid",
                "displayName": "ProjectID"
            }
        ]
    }
    gets "ProjectIdAttributeUid",
    and then, using ProjectIdAttributeUid,
    from GET /api/programs.json?paging=false&fields=id,attributeValues[attribute[id],value]&filter=attributeValues.attribute.id:eq:ProjectIdAttributeUid
    response
    {
        "programs": [
            {
                "id": "ProgramUid",
                "attributeValues": [
                    {
                        "attribute": {
                            "id": "ProjectIdAttributeUid"
                        },
                        "value": "ProjectIdAttributeValue"
                    }
                ]
            }
        ]
    }
    gets mapOf("ProgramUid" to "ProjectIdAttributeValue")
     */
    @SuppressLint("VisibleForTests")
    fun getProgramUidToProjectIdMap(): Result<Map<String, String>> {
        val service = d2.retrofit().create(SimprintsDataMappingApiService::class.java)

        val projectIdAttributeCall = service.getAttributes(
            paging = false,
            fields = "id,displayName",
            filter = "displayName:eq:ProjectID",
        )
        val projectIdAttributeUid = with(projectIdAttributeCall.execute()) {
            takeIf { isSuccessful }?.body()
                ?.attributes?.first()?.id
                ?: return Result.failure(
                    SimprintsDataMappingApiException("Attribute API: ${code()} (${message()})"),
                )
        }

        val programsCall = service.getPrograms(
            paging = false,
            fields = "id,attributeValues[attribute[id],value]",
            filter = "attributeValues.attribute.id:eq:$projectIdAttributeUid",
        )
        val programUidToProjectIdPairs = with(programsCall.execute()) {
            takeIf { isSuccessful }?.body()?.programs?.mapNotNull { program ->
                program.attributeValues.find { it.attribute.id == projectIdAttributeUid }
                    ?.run { program.id to value }
            } ?: return Result.failure(
                SimprintsDataMappingApiException("Programs API: ${code()} (${message()})"),
            )
        }

        return Result.success(programUidToProjectIdPairs.toMap())
    }
}
