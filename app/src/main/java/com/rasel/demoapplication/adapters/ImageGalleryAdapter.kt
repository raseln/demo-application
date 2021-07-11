package com.rasel.demoapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rasel.demoapplication.R
import com.rasel.demoapplication.data.models.Photo
import com.rasel.demoapplication.databinding.LayoutImageCellBinding
import com.rasel.demoapplication.viewholders.ImageGalleryViewHolder

class ImageGalleryAdapter :
    RecyclerView.Adapter<ImageGalleryViewHolder>() {

    var list: List<Photo> = listOf()
    var onPhotoClick: ((Photo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<LayoutImageCellBinding>(
            inflater, R.layout.layout_image_cell, parent, false
        )

        return ImageGalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageGalleryViewHolder, position: Int) {
        val photo = list[position]
        holder.bind(photo, onPhotoClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}