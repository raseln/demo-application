package com.rasel.demoapplication.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rasel.demoapplication.data.api.PexelApiService
import com.rasel.demoapplication.data.models.Photo
import com.rasel.demoapplication.data.models.PhotoResponse
import com.rasel.demoapplication.utils.debugLogInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PexelImageRepository(
    private val apiService: PexelApiService,
    private val dispatcher: CoroutineDispatcher
) {

    private val _photoListLiveData = MutableLiveData<PhotoResponse?>()
    val photoListLiveData: LiveData<PhotoResponse?> = _photoListLiveData

    private val _photoLiveData = MutableLiveData<Photo>()
    val photoLiveData: LiveData<Photo?> = _photoLiveData

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    suspend fun getSearchPhotos(query: String) =
        withContext(dispatcher) {
            try {
                val response = apiService.searchImages(query)
                if (response.isSuccessful) {
                    _photoListLiveData.postValue(response.body())
                    _error.postValue(null)
                } else {
                    _photoListLiveData.postValue(null)
                    _error.postValue(response.errorBody()?.toString() ?: "Something went wrong! Try again!")
                    debugLogInfo(error)
                }
            } catch (ex: Exception) {
                debugLogInfo(ex)
                _photoListLiveData.postValue(null)
                _error.postValue(ex.message ?: "Something went wrong! Try again!")
            }
        }

    suspend fun getPhoto(photoId: Int) =
        withContext(dispatcher) {
            try {
                val response = apiService.getPhoto(photoId)
                if (response.isSuccessful) {
                    _photoLiveData.postValue(response.body())
                    _error.postValue(null)
                } else {
                    _photoLiveData.postValue(null)
                    _error.postValue(response.errorBody()?.toString() ?: "Something went wrong! Try again!")
                }
            } catch (ex: Exception) {
                _photoLiveData.postValue(null)
                _error.postValue(ex.message ?: "Something went wrong! Try again!")
            }
        }
}