package com.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.movieapp.data.api.OmdbApiService
import com.movieapp.data.model.Movie

class MoviePagingSource(
    private val apiService: OmdbApiService,
    private val query: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = apiService.searchMovies(query, page)
            if (response.response == "True" && response.search != null) {
                val totalResults = response.totalResults?.toIntOrNull() ?: 0
                val totalPages = (totalResults + RESULTS_PER_PAGE - 1) / RESULTS_PER_PAGE
                LoadResult.Page(
                    data = response.search,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page >= totalPages) null else page + 1
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val RESULTS_PER_PAGE = 10
    }
}
