package com.example.al_quran
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApi {
    @GET("surah")
    fun getSurahList(): Call<SurahListResponse>

    @GET("surah/{id}/{edition}")
    suspend fun getSurahDetails(
        @Path("id") id: Int,
        @Path("edition") edition: String
    ): SurahDetailsResponse


}
