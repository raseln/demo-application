package com.rasel.demoapplication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.rasel.demoapplication.adapters.ImageGalleryAdapter
import com.rasel.demoapplication.data.api.ApiClientManager
import com.rasel.demoapplication.data.repositories.PexelImageRepository
import com.rasel.demoapplication.databinding.ActivityMainBinding
import com.rasel.demoapplication.utils.Toaster.showToast
import com.rasel.demoapplication.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageGalleryAdapter
    private val viewModel: MainViewModel by lazy {
        val repository = PexelImageRepository(
            ApiClientManager.pexelApiService, Dispatchers.IO
        )
        MainViewModel(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //Initialize recyclerView with empty data
        setRecyclerview()
        setupObservers()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    getSearchedImages(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        //Get default images on init
        viewModel.getSearchedPhotos("nature")
    }

    private fun setRecyclerview() {
        //To display image in Gallery style with two image per row
        val layoutManager = GridLayoutManager(this, 2)
        imageAdapter = ImageGalleryAdapter().apply {
            onPhotoClick = { photo ->
                run {
                    val intent = Intent(this@MainActivity, FullImageActivity::class.java)
                    intent.putExtra(FullImageActivity.PHOTO_ID, photo.id)
                    intent.putExtra(FullImageActivity.PHOTO_URL, photo.src.original)
                    startActivity(intent)
                }
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = imageAdapter
    }

    private fun getSearchedImages(query: String) {
        viewModel.getSearchedPhotos(query)
    }

    private fun setupObservers() {
        viewModel.error.observe(this, { error ->
            if (!error.isNullOrEmpty()) {
                showToast(this, error)
            }
        })

        viewModel.photoListLiveData.observe(this, { response ->
            imageAdapter.list = response?.photos ?: listOf()
            imageAdapter.notifyDataSetChanged()
        })
    }
}