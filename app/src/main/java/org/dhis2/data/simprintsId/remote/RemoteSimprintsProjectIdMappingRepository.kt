package org.dhis2.data.simprintsId.remote

import android.annotation.SuppressLint
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

class RemoteSimprintsProjectIdMappingRepository @Inject constructor(
    private val d2: D2,
) {

    @SuppressLint("VisibleForTests")
    fun getProgramUidToProjectIdMap(): Result<Map<String, String>> {
        val service = d2.retrofit().create(SimprintsDataMappingApiService::class.java)

        val projectIdAttributeCall = service.getAttributes(
            paging = false,
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
            fields = "id,displayName,$projectIdAttributeUid~rename(projectId)",
            filter = "attributeValues.attribute.id:eq:$projectIdAttributeUid",
        )
        val programUidToProjectIdMap = with(programsCall.execute()) {
            takeIf { isSuccessful }?.body()
                ?.programs?.associate { it.id to it.projectId }
                ?: return Result.failure(
                    SimprintsDataMappingApiException("Programs API: ${code()} (${message()})"),
                )
        }

        return Result.success(programUidToProjectIdMap)
    }
}
