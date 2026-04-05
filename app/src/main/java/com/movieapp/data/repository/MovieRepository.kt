package com.movieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.movieapp.data.api.OmdbApiService
import com.movieapp.data.model.Movie
import com.movieapp.data.model.MovieDetail
import com.movieapp.data.paging.MoviePagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: OmdbApiService
) {
    fun searchMovies(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = { MoviePagingSource(apiService, query) }
        ).flow
    }

    suspend fun getMovieDetail(imdbId: String): Result<MovieDetail> {
        return try {
            val detail = apiService.getMovieDetail(imdbId)
            if (detail.response == "True") {
                Result.success(detail)
            } else {
                Result.failure(Exception(detail.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
