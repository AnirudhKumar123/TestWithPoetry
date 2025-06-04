package com.example.testwithpoetry.presentation.viewmodel

import com.example.testwithpoetry.presentation.state.NetworkResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testwithpoetry.data.local.model.Poem
import com.example.testwithpoetry.domain.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoetryViewModel @Inject constructor(
    private val poetryRepository: PoetryRepository
) : ViewModel() {

    private val _poemTitles = MutableStateFlow<NetworkResource<List<String>>>(NetworkResource.Loading)
    val poemTitles: StateFlow<NetworkResource<List<String>>> = _poemTitles.asStateFlow()

    private val _selectedPoem = MutableStateFlow<NetworkResource<Poem>?>(null)
    val selectedPoem: StateFlow<NetworkResource<Poem>?> = _selectedPoem.asStateFlow()

    fun loadPoemTitles(author: String) {
        viewModelScope.launch {
            val titles = poetryRepository.getTitlesByAuthor(author)
            _poemTitles.value = titles
        }
    }

    fun loadPoem(author: String, title: String) {
        viewModelScope.launch {
            val poem = poetryRepository.getPoem(author, title)
            _selectedPoem.value = poem
        }
    }

    fun clearSelectedPoem() {
        _selectedPoem.value = null
    }
}
