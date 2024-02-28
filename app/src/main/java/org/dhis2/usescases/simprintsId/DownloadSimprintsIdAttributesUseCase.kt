package org.dhis2.usescases.simprintsId

import org.dhis2.data.simprintsId.local.LocalSimprintsModuleIdMappingRepository
import org.dhis2.data.simprintsId.local.LocalSimprintsProjectIdMappingRepository
import org.dhis2.data.simprintsId.remote.RemoteSimprintsModuleIdMappingRepository
import org.dhis2.data.simprintsId.remote.RemoteSimprintsProjectIdMappingRepository
import javax.inject.Inject

class DownloadSimprintsIdAttributesUseCase @Inject constructor(
    private val remoteProjectIdMappingRepository: RemoteSimprintsProjectIdMappingRepository,
    private val remoteModuleIdMappingRepository: RemoteSimprintsModuleIdMappingRepository,
    private val localProjectIdMappingRepository: LocalSimprintsProjectIdMappingRepository,
    private val localModuleIdMappingRepository: LocalSimprintsModuleIdMappingRepository,
) {

    fun execute(): Result<Unit> = runCatching {
        getAndProcessData(
            getter = remoteProjectIdMappingRepository::getProgramUidToProjectIdMap,
            processor = localProjectIdMappingRepository::updateProgramUidToProjectIdMap,
        )
        getAndProcessData(
            getter = remoteModuleIdMappingRepository::getOrgUnitUidToModuleIdMap,
            processor = localModuleIdMappingRepository::updateOrgUnitUidToModuleIdMap,
        )
    }

    private fun <T> getAndProcessData(
        getter: () -> Result<T>,
        processor: (T) -> Unit,
    ) {
        val data = getter()
        data.getOrNull()
            ?.let { processor(it) }
            ?: throw (
                data.exceptionOrNull() ?: IllegalStateException(
                    "Simprints attributes mapping retrieval result error: neither value, nor exception",
                )
                )
    }
}
