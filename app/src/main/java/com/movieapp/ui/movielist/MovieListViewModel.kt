package com.movieapp.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.movieapp.data.model.Movie
import com.movieapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    // Default query loads movies on first launch; user can filter via search bar
    private val _searchQuery = MutableStateFlow(DEFAULT_QUERY)
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val movies: Flow<PagingData<Movie>> = _searchQuery
        .debounce(400)
        .flatMapLatest { query -> repository.searchMovies(query.ifBlank { DEFAULT_QUERY }) }
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    companion object {
        const val DEFAULT_QUERY = "action"
    }
}
