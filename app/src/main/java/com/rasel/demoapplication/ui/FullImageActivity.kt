package com.rasel.demoapplication.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rasel.demoapplication.data.api.ApiClientManager
import com.rasel.demoapplication.data.repositories.PexelImageRepository
import com.rasel.demoapplication.databinding.ActivityFullImageBinding
import com.rasel.demoapplication.viewmodels.FullImageViewModel
import kotlinx.coroutines.Dispatchers

class FullImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullImageBinding
    private val viewModel: FullImageViewModel by lazy {
        val repository = PexelImageRepository(
            ApiClientManager.pexelApiService, Dispatchers.IO
        )
        FullImageViewModel(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        setupObservers()


        //Get photo id from intent and display full image by retrieving
        val photoId: Int = intent?.getIntExtra(PHOTO_ID, 0) ?: 0
        if (photoId > 0) {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getPhoto(photoId)
        }
    }

    private fun setupObservers() {
        viewModel.error.observe(this, { error ->
            binding.progressBar.visibility = View.GONE
            if (!error.isNullOrEmpty()) {
                binding.error.text = error
            }
        })

        viewModel.photoLiveData.observe(this, {
            binding.progressBar.visibility = View.GONE
            if (it != null) {
                binding.photo = it
                binding.executePendingBindings()
            }
        })
    }

    companion object {
        const val PHOTO_ID: String = "photo_id"
        const val PHOTO_URL: String = "photo_url"
    }
}