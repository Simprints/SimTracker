package org.dhis2.data.simprintsId.remote

data class SimprintsDataMappingApiException(override val message: String) :
    RuntimeException(message)
