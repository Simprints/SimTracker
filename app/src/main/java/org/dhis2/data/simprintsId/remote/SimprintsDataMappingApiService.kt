package org.dhis2.data.simprintsId.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

internal interface SimprintsDataMappingApiService {

    data class Attributes(val attributes: List<Attribute>)

    data class Attribute(val displayName: String, val id: String)

    data class Programs(val programs: List<Program>)

    data class Program(
        val displayName: String,
        val id: String,
        val projectId: String,
    )

    data class OrganisationUnits(val organisationUnits: List<OrganisationUnit>)

    data class OrganisationUnit(
        val displayName: String,
        val id: String,
        val moduleId: String,
    )

    @GET("/api/attributes")
    fun getAttributes(
        @Query("paging") paging: Boolean,
        @Query("filter") filter: String,
    ): Call<Attributes>

    @GET("/api/programs")
    fun getPrograms(
        @Query("paging") paging: Boolean,
        @Query("fields") fields: String,
        @Query("filter") filter: String,
    ): Call<Programs>

    @GET("/api/organisationUnits")
    fun getOrganisationUnits(
        @Query("paging") paging: Boolean,
        @Query("fields") fields: String,
        @Query("filter") filter: String,
    ): Call<OrganisationUnits>
}
