package org.dhis2.commons.simprints

// The final matches by the Simprints biometric identification-based (one-to-many) search
data class SimprintsBiometricSearchResult(
    val timestamp: Long,
    val teiUids: List<String>,
)
