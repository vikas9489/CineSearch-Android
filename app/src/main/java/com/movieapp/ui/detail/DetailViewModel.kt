package com.movieapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieapp.data.model.MovieDetail
import com.movieapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val movie: MovieDetail) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadMovieDetail(imdbId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getMovieDetail(imdbId)
                .onSuccess { detail ->
                    _uiState.value = DetailUiState.Success(detail)
                }
                .onFailure { error ->
                    _uiState.value = DetailUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
