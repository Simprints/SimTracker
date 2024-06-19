package com.simprints.simprints

// Whether the biometrics action button passes command to open
// Simprints ID for enrollment, verification or identification,
// can be defined just by knowing if it's identification (one-to-many) or not.
// If not, the presence of the enrollment GUID will define between the remaining two options.
data class SimprintsBiometricsAction(
    val isOneToMany: Boolean,
)
