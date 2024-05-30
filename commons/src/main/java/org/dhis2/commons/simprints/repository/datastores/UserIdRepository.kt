package org.dhis2.commons.simprints.repository.datastores

import org.hisp.dhis.android.core.D2
import javax.inject.Inject

/**
 * Simprints ID's user ID input param value
 * is the name of the user signed into the current DHIS2 instance, as obtained by DHIS2 SDK.
 */
class UserIdRepository @Inject constructor(
    private val d2: D2,
) {

    fun getUserId(): String? =
        d2.userModule().authenticatedUser().blockingGet()?.user()
}
