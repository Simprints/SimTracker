package org.dhis2.data.simprintsId.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject

class LocalSimprintsProjectIdMappingRepository @Inject constructor(
    context: Context,
) {
    private val securePreferences = EncryptedSharedPreferences.create(
        context,
        "ProjectIdMappings",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun updateProgramUidToProjectIdMap(newMap: Map<String, String>) {
        with(securePreferences.edit()) {
            clear()
            newMap.entries.forEach { (key, value) ->
                putString(key, value)
            }
            apply()
        }
    }

    fun getProjectIdOrNull(programUid: String): String? =
        securePreferences.getString(programUid, null)
}
