package com.rasel.demoapplication.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rasel.demoapplication.data.api.PexelApiService
import com.rasel.demoapplication.data.models.Photo
import com.rasel.demoapplication.data.models.PhotoResponse
import com.rasel.demoapplication.data.models.Src
import com.rasel.demoapplication.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class PexelImageRepositoryTest {
    @Mock
    lateinit var apiService: PexelApiService

    private val dispatcher = TestCoroutineDispatcher()

    private lateinit var sut: PexelImageRepository

    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = PexelImageRepository(apiService, dispatcher)
    }

    @Test
    fun when_search_api_call_is_successful() = runBlockingTest {
        // Arrange
        val src = Src("", "", "", "", "", "", "", "")
        val photo = Photo(1, 200, 300, "https://abc.com/1", "ABC", "https://abc.com", 1, "Red", src)
        val photoResponse = PhotoResponse(listOf(photo), "next", 1, 1, 10)
        Mockito.`when`(
            apiService.searchImages(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString()
            )
        )
            .thenReturn(Response.success(photoResponse))

        // Act
        sut.getSearchPhotos("nature", 1)

        // Assert
        assertEquals(sut.photoListLiveData.getOrAwaitValue(), photoResponse)
        assertEquals(sut.error.getOrAwaitValue(), null)
        Mockito.verify(apiService, Mockito.times(1)).searchImages(
            Mockito.anyString(),
            Mockito.anyInt(),
            Mockito.anyInt(),
            Mockito.anyString()
        )
        Mockito.verifyNoMoreInteractions(apiService)
    }

    @Test
    fun when_search_api_call_is_not_successful() = runBlockingTest {
        // Arrange
        val errorBody = ByteArray(0).toResponseBody(null)
        Mockito.`when`(
            apiService.searchImages(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString()
            )
        )
            .thenReturn(Response.error(400, errorBody))

        // Act
        sut.getSearchPhotos("nature", 1)

        // Assert
        assertEquals(sut.photoListLiveData.getOrAwaitValue(), null)
        assertEquals(sut.error.getOrAwaitValue(), errorBody.toString())
        Mockito.verify(apiService, Mockito.times(1)).searchImages(
            Mockito.anyString(),
            Mockito.anyInt(),
            Mockito.anyInt(),
            Mockito.anyString()
        )
        Mockito.verifyNoMoreInteractions(apiService)
    }

    @Test
    fun when_search_api_call_fails_with_exception() = runBlockingTest {
        // Arrange
        Mockito.`when`(
            apiService.searchImages(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString()
            )
        )
            .thenThrow(RuntimeException("Invalid operation"))

        // Act
        sut.getSearchPhotos("nature", 1)

        //Assert
        assertEquals(sut.photoListLiveData.getOrAwaitValue(), null)
        assertEquals(sut.error.getOrAwaitValue(), "Invalid operation")
        Mockito.verify(apiService, Mockito.times(1)).searchImages(
            Mockito.anyString(),
            Mockito.anyInt(),
            Mockito.anyInt(),
            Mockito.anyString()
        )
        Mockito.verifyNoMoreInteractions(apiService)
    }

    @Test
    fun when_photo_api_call_is_successful() = runBlockingTest {
        // Arrange
        val src = Src("", "", "", "", "", "", "", "")
        val photo = Photo(1, 200, 300, "https://abc.com/1", "ABC", "https://abc.com", 1, "Red", src)
        Mockito.`when`(apiService.getPhoto(Mockito.anyInt()))
            .thenReturn(Response.success(photo))

        // Act
        sut.getPhoto(1)

        // Assert
        assertEquals(sut.photoLiveData.getOrAwaitValue(), photo)
        assertEquals(sut.error.getOrAwaitValue(), null)
        Mockito.verify(apiService, Mockito.times(1)).getPhoto(Mockito.anyInt())
        Mockito.verifyNoMoreInteractions(apiService)
    }

    @Test
    fun when_photo_api_call_is_failed() = runBlockingTest {
        // Arrange
        val errorBody = ByteArray(0).toResponseBody(null)
        Mockito.`when`(apiService.getPhoto(Mockito.anyInt()))
            .thenReturn(Response.error(400, errorBody))

        // Act
        sut.getPhoto(1)

        // Assert
        assertEquals(sut.photoLiveData.getOrAwaitValue(), null)
        assertEquals(sut.error.getOrAwaitValue(), errorBody.toString())
        Mockito.verify(apiService, Mockito.times(1)).getPhoto(Mockito.anyInt())
        Mockito.verifyNoMoreInteractions(apiService)
    }

    @Test
    fun when_photo_api_call_fails_with_exception() = runBlockingTest {
        // Arrange
        Mockito.`when`(apiService.getPhoto(Mockito.anyInt()))
            .thenThrow(RuntimeException("Invalid"))

        // Act
        sut.getPhoto(1)

        // Assert
        assertEquals(sut.photoLiveData.getOrAwaitValue(), null)
        assertEquals(sut.error.getOrAwaitValue(), "Invalid")
        Mockito.verify(apiService, Mockito.times(1)).getPhoto(Mockito.anyInt())
        Mockito.verifyNoMoreInteractions(apiService)
    }
}