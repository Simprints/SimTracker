package org.dhis2.data.simprintsId.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

internal interface SimprintsDataMappingApiService {

    data class Attributes(val attributes: List<Attribute>)

    data class Attribute(val displayName: String, val id: String)

    data class Programs(val programs: List<Program>)

    data class Program(val id: String, val attributeValues: List<AttributeValue>)

    data class OrganisationUnits(val organisationUnits: List<OrganisationUnit>)

    data class OrganisationUnit(val id: String, val attributeValues: List<AttributeValue>)

    data class AttributeValue(val attribute: AttributeID, val value: String)

    data class AttributeID(val id: String)

    @GET("/api/attributes.json")
    fun getAttributes(
        @Query("paging") paging: Boolean,
        @Query("fields") fields: String,
        @Query("filter") filter: String,
    ): Call<Attributes>

    @GET("/api/programs.json")
    fun getPrograms(
        @Query("paging") paging: Boolean,
        @Query("fields") fields: String,
        @Query("filter") filter: String,
    ): Call<Programs>

    @GET("/api/organisationUnits.json")
    fun getOrganisationUnits(
        @Query("paging") paging: Boolean,
        @Query("fields") fields: String,
        @Query("filter") filter: String,
    ): Call<OrganisationUnits>
}
