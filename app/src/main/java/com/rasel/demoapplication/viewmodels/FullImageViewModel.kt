package com.rasel.demoapplication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasel.demoapplication.data.models.Photo
import com.rasel.demoapplication.data.repositories.PexelImageRepository
import kotlinx.coroutines.launch

class FullImageViewModel(private val repository: PexelImageRepository): ViewModel() {

    val photoLiveData: LiveData<Photo?> = repository.photoLiveData
    val error: LiveData<String?> = repository.error

    fun getPhoto(photoId: Int) {
        viewModelScope.launch {
            repository.getPhoto(photoId)
        }
    }
}