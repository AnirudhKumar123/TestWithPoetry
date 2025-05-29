package com.example.testwithpoetry.presentation.viewmodel

import com.example.testwithpoetry.presentation.state.NetworkResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testwithpoetry.data.local.model.Poem
import com.example.testwithpoetry.domain.repository.PoetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoetryViewModel @Inject constructor(
    private val poetryRepository: PoetryRepository
) : ViewModel() {

    private val _poemTitles = MutableLiveData<NetworkResource<List<String>>>()
    val poemTitles: LiveData<NetworkResource<List<String>>> = _poemTitles

    private val _selectedPoem = MutableLiveData<NetworkResource<Poem>?>()
    val selectedPoem: LiveData<NetworkResource<Poem>?> = _selectedPoem


    fun loadPoemTitles(author: String) {

        viewModelScope.launch {
            val titles = poetryRepository.getTitlesByAuthor(author)
            _poemTitles.postValue(titles)
        }
    }

    fun loadPoem(author: String, title: String) {
        viewModelScope.launch {
            val poem = poetryRepository.getPoem(author, title)
            _selectedPoem.postValue(poem)

        }
    }

    fun clearSelectedPoem() {
        _selectedPoem.value = null
    }
}
