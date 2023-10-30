package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nework.dto.Post

interface WallApiService {

    @GET("{authorId}/wall/{id}/before")
    suspend fun getWallBefore(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("{authorId}/wall/{id}/after")
    suspend fun getWallAfter(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("{authorId}/wall/latest")
    suspend fun getWallLatest(
        @Path("authorId") authorId: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>
}
