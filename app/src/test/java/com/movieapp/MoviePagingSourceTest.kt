package com.movieapp

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.movieapp.data.api.OmdbApiService
import com.movieapp.data.model.Movie
import com.movieapp.data.model.MovieSearchResponse
import com.movieapp.data.paging.MoviePagingSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MoviePagingSourceTest {

    private val apiService: OmdbApiService = mockk()

    private val fakeMovies = (1..10).map { index ->
        Movie(
            title = "Movie $index",
            year = "2020",
            imdbID = "tt000000$index",
            type = "movie",
            poster = "N/A"
        )
    }

    @Test
    fun `load returns page with movies on success`() = runTest {
        val successResponse = MovieSearchResponse(
            search = fakeMovies,
            totalResults = "25",
            response = "True",
            error = null
        )
        coEvery { apiService.searchMovies("batman", 1) } returns successResponse

        val pagingSource = MoviePagingSource(apiService, "batman")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(10, page.data.size)
        assertEquals("Movie 1", page.data.first().title)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `load returns empty page when response is False`() = runTest {
        val emptyResponse = MovieSearchResponse(
            search = null,
            totalResults = null,
            response = "False",
            error = "Movie not found!"
        )
        coEvery { apiService.searchMovies("xyz123notfound", 1) } returns emptyResponse

        val pagingSource = MoviePagingSource(apiService, "xyz123notfound")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.nextKey)
    }

    @Test
    fun `load returns error on exception`() = runTest {
        coEvery { apiService.searchMovies("batman", 1) } throws Exception("Network failure")

        val pagingSource = MoviePagingSource(apiService, "batman")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals("Network failure", (result as PagingSource.LoadResult.Error).throwable.message)
    }

    @Test
    fun `last page has null nextKey`() = runTest {
        val lastPageResponse = MovieSearchResponse(
            search = fakeMovies.take(5),
            totalResults = "15",
            response = "True",
            error = null
        )
        coEvery { apiService.searchMovies("batman", 2) } returns lastPageResponse

        val pagingSource = MoviePagingSource(apiService, "batman")
        val result = pagingSource.load(
            PagingSource.LoadParams.Append(key = 2, loadSize = 10, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertNull(page.nextKey)
        assertEquals(1, page.prevKey)
    }
}
