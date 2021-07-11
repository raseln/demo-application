package com.rasel.demoapplication.viewholders

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rasel.demoapplication.data.models.Photo
import com.rasel.demoapplication.databinding.LayoutImageCellBinding

class ImageGalleryViewHolder(private val binding: LayoutImageCellBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(photo: Photo, onImageClick: ((Photo) -> Unit)?) {
        binding.photo = photo
        binding.root.setOnClickListener {
            onImageClick?.invoke(photo)
        }
        binding.executePendingBindings()
    }
}

@BindingAdapter("photoUrl")
fun showPhoto(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView)
            .load(it)
            .into(imageView)
    }
}