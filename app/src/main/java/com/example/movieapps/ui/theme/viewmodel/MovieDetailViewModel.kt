package com.example.movieapps.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapps.data.DataSource
import com.example.movieapps.model.Movie
import kotlinx.coroutines.launch

sealed interface MovieDetailUiState {
    data class Success(val data: Movie):MovieDetailUiState
    object Error: MovieDetailUiState
    object Loading: MovieDetailUiState
}

class MovieDetailViewModel(): ViewModel() {
    private lateinit var data: Movie
    var movieDetailUiState: MovieDetailUiState by mutableStateOf(MovieDetailUiState.Loading)
        private set

    fun getMovieById(id: Int) {
        viewModelScope.launch {
            for (movie in DataSource().loadMovie()) {
                if (movie.id == id) {
                    data = movie
                    break
                }
            }
            movieDetailUiState = MovieDetailUiState.Success(data)
        }
    }
}