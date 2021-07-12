package com.rasel.demoapplication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasel.demoapplication.data.models.Photo
import com.rasel.demoapplication.data.models.PhotoResponse
import com.rasel.demoapplication.data.repositories.PexelImageRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PexelImageRepository): ViewModel() {

    val photoListLiveData: LiveData<PhotoResponse?> = repository.photoListLiveData
    val error: LiveData<String?> = repository.error
    var isLastPage: Boolean = false
    var currentPage: Int = 1
    var currentQuery: String = "nature"
    val photoList: MutableList<Photo> = mutableListOf()

    val searchPhotos = { query: String? ->
        if (!query.isNullOrEmpty()) {
            currentPage = 1
            isLastPage = false
            currentQuery = query
            getSearchedPhotos(query)
        }
    }

    fun getSearchedPhotos(query: String, isFirstPage: Boolean = false) {
        if (isFirstPage) {
            isLastPage = false
            currentPage = 1
            currentQuery = if (query.isNotEmpty()) {
                query
            } else {
                "nature"
            }
        }
        viewModelScope.launch {
            repository.getSearchPhotos(query, currentPage)
        }
    }
}