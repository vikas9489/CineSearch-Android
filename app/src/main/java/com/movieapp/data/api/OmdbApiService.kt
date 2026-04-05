package com.movieapp.data.api

import com.movieapp.data.model.MovieDetail
import com.movieapp.data.model.MovieSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {

    @GET(".")
    suspend fun searchMovies(
        @Query("s") searchQuery: String,
        @Query("page") page: Int
    ): MovieSearchResponse

    @GET(".")
    suspend fun getMovieDetail(
        @Query("i") imdbId: String
    ): MovieDetail

    companion object {
        const val BASE_URL = "https://www.omdbapi.com/"
        const val API_KEY = "c66fd0f9"
    }
}
