package com.example.testwithpoetry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testwithpoetry.data.local.model.Author
import com.example.testwithpoetry.domain.repository.PoetryRepository
import com.example.testwithpoetry.presentation.state.NetworkResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthorsUiState {
    data class Success(val authors: List<Author>) : AuthorsUiState()
    data class Error(val message: String) : AuthorsUiState()
    object Loading : AuthorsUiState()
}

sealed class AuthorsEvent {
    data class ToggleFavorite(val author: Author) : AuthorsEvent()
}

@HiltViewModel
class AuthorsViewModel @Inject constructor(
    private val poetryRepository: PoetryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthorsUiState>(AuthorsUiState.Loading)
    val uiState: StateFlow<AuthorsUiState> = _uiState.asStateFlow()

    private val _snackbarChannel = MutableSharedFlow<String>()
    val snackbarEvents = _snackbarChannel.asSharedFlow()

    init {
        loadAuthors()
    }

    private fun loadAuthors() {
        poetryRepository.getAuthors()
            .onEach { resource ->
                _uiState.value = when (resource) {
                    is NetworkResource.Success -> AuthorsUiState.Success(resource.data)
                    is NetworkResource.Fail -> AuthorsUiState.Error(resource.error ?: "Unknown error")
                    is NetworkResource.Loading -> AuthorsUiState.Loading
                }
            }
            .catch { e ->
                _uiState.value = AuthorsUiState.Error(e.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    fun handleEvent(event: AuthorsEvent) {
        when (event) {
            is AuthorsEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    poetryRepository.toggleFavourite(event.author)
                    _snackbarChannel.emit(
                        if (!event.author.isFavourite) "Author added to favorites"
                        else "Author removed from favorites"
                    )
                    loadAuthors()
                }
            }
        }
    }
}
