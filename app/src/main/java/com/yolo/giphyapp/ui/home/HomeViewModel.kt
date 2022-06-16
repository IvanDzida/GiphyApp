package com.yolo.giphyapp.ui.home

import androidx.lifecycle.*
import com.yolo.data.GiphyItem
import com.yolo.giphyapp.util.State
import com.yolo.repository.GiphyRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: GiphyRepository) : ViewModel() {

    val gifs = MutableLiveData<List<GiphyItem>>()
    val state = MutableLiveData(State.INITIAL)

    private val limit = 10
    private var offset = 0

    init {
        trending(isInitial = true)
    }

    fun trending(isInitial: Boolean = false) {
        viewModelScope.launch {
            offset = if(isInitial) 0 else offset + PAGE_SIZE
            gifs.value = repository.getTrending(offset, limit)
            state.value = if(gifs.value!!.isNotEmpty()) State.READY else State.ERROR
        }
    }

    fun updateState(newState: State) {
        viewModelScope.launch {
            state.value = newState
        }
    }

    private companion object {
        const val PAGE_SIZE = 10
    }
}