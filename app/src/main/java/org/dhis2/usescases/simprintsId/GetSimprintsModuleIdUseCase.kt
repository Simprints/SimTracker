package org.dhis2.usescases.simprintsId

import org.dhis2.data.simprintsId.local.LocalSimprintsModuleIdMappingRepository
import org.hisp.dhis.android.core.D2
import javax.inject.Inject

class GetSimprintsModuleIdUseCase @Inject constructor(
    private val localModuleIdMappingRepository: LocalSimprintsModuleIdMappingRepository,
    private val d2: D2,
) {

    fun execute(orgUnitUid: String): String? {
        // First, looking for this org unit
        localModuleIdMappingRepository.getModuleIdOrNull(orgUnitUid)
            ?.let { return it }
        // If not found, looking for higher level org units
        var higherLevelOrgUnitUid: String? = orgUnitUid
        do {
            higherLevelOrgUnitUid = getHigherLevelOrgUnitUidOrNull(higherLevelOrgUnitUid)
            higherLevelOrgUnitUid?.run(localModuleIdMappingRepository::getModuleIdOrNull)
                ?.let { return it }
        } while (higherLevelOrgUnitUid != null)
        // If none found
        return null
    }

    private fun getHigherLevelOrgUnitUidOrNull(orgUnitUid: String?): String? =
        d2.organisationUnitModule().organisationUnits().byUid().eq(orgUnitUid).blockingGet()
            .map { it.parent()?.uid() }.firstOrNull()
}
