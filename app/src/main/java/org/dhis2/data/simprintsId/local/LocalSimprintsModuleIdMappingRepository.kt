package org.dhis2.data.simprintsId.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject

class LocalSimprintsModuleIdMappingRepository @Inject constructor(
    context: Context,
) {
    private val securePreferences = EncryptedSharedPreferences.create(
        context,
        "ModuleIdMappings",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun updateOrgUnitUidToModuleIdMap(newMap: Map<String, String>) {
        with(securePreferences.edit()) {
            clear()
            newMap.entries.forEach { (key, value) ->
                putString(key, value)
            }
            apply()
        }
    }

    fun getModuleIdOrNull(orgUnitUid: String): String? =
        securePreferences.getString(orgUnitUid, null)
}
