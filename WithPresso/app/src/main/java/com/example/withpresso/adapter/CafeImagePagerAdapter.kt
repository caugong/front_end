package com.example.withpresso.adapter

import android.content.Context
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.R
import kotlinx.android.synthetic.main.cafe_info_photo.view.*

class CafeImagePagerHolder(photo: View): RecyclerView.ViewHolder(photo){
    val photo = photo.info_photo_image
}

class CafeImagePagerAdapter(
    val context: Context,
    val photoList: ArrayList<String>?
    ): RecyclerView.Adapter<CafeImagePagerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeImagePagerHolder {
        val cafePhotoFrame = LayoutInflater.from(context).inflate(
            R.layout.cafe_info_photo,
            parent,
            false
        )

        return CafeImagePagerHolder(cafePhotoFrame)
    }

    override fun getItemCount(): Int = photoList?.size ?: 1

    override fun onBindViewHolder(holder: CafeImagePagerHolder, position: Int) {
        Glide.with(context)
            .load(photoList?.get(position)?:R.drawable.coffee)
            .error(R.drawable.coffee)
            .override(700)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.photo)
    }
}