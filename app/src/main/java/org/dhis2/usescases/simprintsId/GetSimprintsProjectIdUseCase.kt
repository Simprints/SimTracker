package org.dhis2.usescases.simprintsId

import org.dhis2.data.simprintsId.local.LocalSimprintsProjectIdMappingRepository
import javax.inject.Inject

class GetSimprintsProjectIdUseCase @Inject constructor(
    private val localProjectIdMappingRepository: LocalSimprintsProjectIdMappingRepository,
) {

    fun execute(programUid: String): String? =
        localProjectIdMappingRepository.getProjectIdOrNull(programUid)
}
