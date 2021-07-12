package com.rasel.demoapplication.utils

import android.widget.ImageView
import android.widget.SearchView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("photoUrl")
fun showPhoto(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView)
            .load(it)
            .into(imageView)
    }
}

@BindingAdapter("setOnQueryTextListener")
fun setOnQueryTextListener(view: SearchView, search: (String?)-> Unit) {
    view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            search(query)
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    })
}