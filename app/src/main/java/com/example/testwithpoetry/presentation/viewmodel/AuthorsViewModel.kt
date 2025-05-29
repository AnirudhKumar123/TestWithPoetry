package com.example.testwithpoetry.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testwithpoetry.presentation.state.NetworkResource
import com.example.testwithpoetry.data.local.model.Author
import com.example.testwithpoetry.domain.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthorsViewModel @Inject constructor(
    private val poetryRepository: PoetryRepository,
) : ViewModel() {

    private val _authors = MutableLiveData<NetworkResource<List<Author>>>()
    val authors: LiveData<NetworkResource<List<Author>>> = _authors

    private val _showSnackbar = MutableLiveData(false)
    val showSnackbar: LiveData<Boolean> = _showSnackbar



    init {
        loadAuthors()
    }

    private fun loadAuthors() {
        viewModelScope.launch {
            val authorList = poetryRepository.getAuthors()
            _authors.value = authorList
        }
    }

    fun toggleFavorite(author: Author) {
        viewModelScope.launch {
            poetryRepository.toggleFavourite(author)
            _showSnackbar.value = true
            loadAuthors()
        }
    }

    fun snackbarShown() {
        _showSnackbar.value = false
    }
}
