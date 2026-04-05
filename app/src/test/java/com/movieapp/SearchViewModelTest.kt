package com.movieapp

import androidx.paging.PagingData
import com.movieapp.data.model.Movie
import com.movieapp.data.repository.MovieRepository
import com.movieapp.ui.search.SearchViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val repository: MovieRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.searchMovies(any()) } returns flowOf(PagingData.empty())
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial search query is empty`() {
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `onQueryChange updates the search query state`() {
        viewModel.onQueryChange("batman")
        assertEquals("batman", viewModel.searchQuery.value)
    }

    @Test
    fun `onQueryChange with empty string sets query to empty`() {
        viewModel.onQueryChange("batman")
        viewModel.onQueryChange("")
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `onQueryChange with multiple updates keeps the latest value`() {
        viewModel.onQueryChange("b")
        viewModel.onQueryChange("ba")
        viewModel.onQueryChange("bat")
        assertEquals("bat", viewModel.searchQuery.value)
    }

    @Test
    fun `search query with spaces is preserved in state`() {
        viewModel.onQueryChange("batman begins")
        assertEquals("batman begins", viewModel.searchQuery.value)
    }
}
