package com.example.movieapps.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapps.data.DataSource
import com.example.movieapps.model.Movie
import kotlinx.coroutines.launch

sealed interface ListMovieUIState {
    data class Success(val data: List<Movie>):ListMovieUIState
    object Error: ListMovieUIState //error dan loading itu default stuff
    object Loading: ListMovieUIState
}

class ListMovieViewModel: ViewModel() {
    var listMovieUIState: ListMovieUIState by mutableStateOf(ListMovieUIState.Loading) //manggil sealed interface
        private set

    private lateinit var data: List<Movie> // lateinit berarti isi variabelnya masih kosong

    init{ // Initialize() di JavaFX, tanpa button, pokoknya dari awal ketrigger
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                data = DataSource().loadMovie()

                listMovieUIState = ListMovieUIState.Success(data)
            } catch (e: Exception) {
                listMovieUIState = ListMovieUIState.Error
            }
        }
    }

    fun onFavClicked(movie: Movie) {
        movie.isLiked = !movie.isLiked

        // send to server, jangan logikanya di view meskipun simpel!!
        // update data dari server, ngambil data dari server
    }
}