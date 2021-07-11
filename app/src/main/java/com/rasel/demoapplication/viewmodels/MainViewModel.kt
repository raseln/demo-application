package com.rasel.demoapplication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasel.demoapplication.data.models.PhotoResponse
import com.rasel.demoapplication.data.repositories.PexelImageRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PexelImageRepository): ViewModel() {

    val photoListLiveData: LiveData<PhotoResponse?> = repository.photoListLiveData
    val error: LiveData<String?> = repository.error

    fun getSearchedPhotos(query: String) {
        viewModelScope.launch {
            repository.getSearchPhotos(query)
        }
    }
}