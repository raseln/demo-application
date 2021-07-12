package com.rasel.demoapplication.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.rasel.demoapplication.adapters.ImageGalleryAdapter
import com.rasel.demoapplication.data.api.ApiClientManager
import com.rasel.demoapplication.data.repositories.PexelImageRepository
import com.rasel.demoapplication.databinding.ActivityMainBinding
import com.rasel.demoapplication.utils.EndlessScrollListener
import com.rasel.demoapplication.utils.Toaster.showToast
import com.rasel.demoapplication.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageGalleryAdapter
    private lateinit var scrollListener: EndlessScrollListener
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
        viewModel.currentPage = 1
        viewModel.isLastPage = false

        //Initialize recyclerView with empty data
        setRecyclerview()
        setupObservers()

        //Get default images on init
        viewModel.getSearchedPhotos("nature")

        binding.swipeRefresh.setOnRefreshListener {
            scrollListener.resetValues()
            val query: String = binding.searchView.query.toString()
            viewModel.getSearchedPhotos(query, true)
        }
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

        scrollListener = object : EndlessScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (viewModel.photoList.isNotEmpty() && !viewModel.isLastPage) {
                    viewModel.currentPage = currentPage
                    binding.swipeRefresh.isRefreshing = true
                    viewModel.getSearchedPhotos(viewModel.currentQuery)
                }
            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = imageAdapter
    }

    private fun setupObservers() {
        viewModel.error.observe(this, { error ->
            binding.swipeRefresh.isRefreshing = false
            if (!error.isNullOrEmpty()) {
                showToast(this, error)
            }
        })

        viewModel.photoListLiveData.observe(this, { response ->
            binding.swipeRefresh.isRefreshing = false
            if (viewModel.currentPage == 1) {
                viewModel.photoList.clear()
                response?.photos?.let { viewModel.photoList.addAll(it) }
            } else {
                response?.photos?.let { viewModel.photoList.addAll(it) }
            }
            imageAdapter.list = viewModel.photoList
            imageAdapter.notifyDataSetChanged()
            if (response?.nextPage.isNullOrEmpty()) {
                viewModel.isLastPage = true
            } else {
                viewModel.currentPage++
            }
        })
    }
}