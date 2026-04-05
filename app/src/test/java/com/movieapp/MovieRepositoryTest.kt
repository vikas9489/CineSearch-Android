package com.movieapp

import com.movieapp.data.api.OmdbApiService
import com.movieapp.data.model.MovieDetail
import com.movieapp.data.model.MovieSearchResponse
import com.movieapp.data.model.Movie
import com.movieapp.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieRepositoryTest {

    private lateinit var repository: MovieRepository
    private val apiService: OmdbApiService = mockk()

    private val fakeMovieDetail = MovieDetail(
        title = "Batman Begins",
        year = "2005",
        rated = "PG-13",
        released = "15 Jun 2005",
        runtime = "140 min",
        genre = "Action, Drama",
        director = "Christopher Nolan",
        writer = "Bob Kane, David S. Goyer",
        actors = "Christian Bale, Michael Caine",
        plot = "Bruce Wayne becomes Batman.",
        language = "English",
        country = "USA",
        awards = "Nominated for 1 Oscar",
        poster = "https://example.com/poster.jpg",
        imdbRating = "8.2",
        imdbVotes = "2,500,000",
        imdbID = "tt0372784",
        type = "movie",
        response = "True",
        error = null
    )

    @Before
    fun setup() {
        repository = MovieRepository(apiService)
    }

    @Test
    fun `getMovieDetail returns success when response is True`() = runTest {
        coEvery { apiService.getMovieDetail("tt0372784") } returns fakeMovieDetail

        val result = repository.getMovieDetail("tt0372784")

        assertTrue(result.isSuccess)
        assertEquals("Batman Begins", result.getOrNull()?.title)
        assertEquals("8.2", result.getOrNull()?.imdbRating)
    }

    @Test
    fun `getMovieDetail returns failure when response is False`() = runTest {
        val errorDetail = fakeMovieDetail.copy(response = "False", error = "Movie not found!")
        coEvery { apiService.getMovieDetail("invalid_id") } returns errorDetail

        val result = repository.getMovieDetail("invalid_id")

        assertTrue(result.isFailure)
        assertEquals("Movie not found!", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getMovieDetail returns failure with default message when error is null`() = runTest {
        val errorDetail = fakeMovieDetail.copy(response = "False", error = null)
        coEvery { apiService.getMovieDetail("tt9999999") } returns errorDetail

        val result = repository.getMovieDetail("tt9999999")

        assertTrue(result.isFailure)
        assertEquals("Unknown error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getMovieDetail returns failure on network exception`() = runTest {
        coEvery { apiService.getMovieDetail(any()) } throws Exception("Network error")

        val result = repository.getMovieDetail("tt0372784")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getMovieDetail calls api with correct imdbId`() = runTest {
        coEvery { apiService.getMovieDetail("tt0372784") } returns fakeMovieDetail

        repository.getMovieDetail("tt0372784")

        coVerify(exactly = 1) { apiService.getMovieDetail("tt0372784") }
    }

    @Test
    fun `searchMovies returns non-null paging flow`() {
        val flow = repository.searchMovies("batman")
        assertTrue(flow != null)
    }
}
