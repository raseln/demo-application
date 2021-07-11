package com.rasel.demoapplication.data.api

import com.rasel.demoapplication.data.models.Photo
import com.rasel.demoapplication.data.models.PhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelApiService {

    @GET("search")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("per_page") itemNum: Int = 20,
        @Query("page") numPage: Int = 1,
        @Query("size") size: String = "small"
    ) : Response<PhotoResponse>

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: Int
    ) : Response<Photo>
}