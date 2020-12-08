package com.example.withpressoowner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpressoowner.R
import com.example.withpressoowner.service.Review
import kotlinx.android.synthetic.main.review_layout.view.*

class ReviewHolder(review: View): RecyclerView.ViewHolder(review) {
    var profile_photo = review.profile_photo
    var user_name = review.user_name
    var review = review.review_content
}

class ReviewAdapter(
    val context: Context,
    val commentList: List<Review>
): RecyclerView.Adapter<ReviewHolder>() {
    private val pref = context.getSharedPreferences("user_info", 0)
    private val BASE_URL = "https://withpresso.gq"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        val comment = LayoutInflater.from(context).inflate(R.layout.review_layout, parent, false)

        return ReviewHolder(comment)
    }

    override fun getItemCount(): Int = commentList.size

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        val curr_comment = commentList[position]

        holder.user_name.text = curr_comment.user_name
        holder.review.text = curr_comment.review
        drawProfile(
            "${BASE_URL}/profiles/${curr_comment.user_asin}/1.jpg",
            holder.profile_photo
        )
    }

    private fun drawProfile(photoUrl: String?, imageView: ImageView) {
        if(photoUrl.isNullOrBlank()) {
            Glide.with(context)
                .load(R.drawable.ic_baseline_person_24)
                .override(300)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }
        else{
            Glide.with(context)
                .load(photoUrl)
                .override(300)
                .centerCrop()
                .error(R.drawable.ic_baseline_person_24)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }
    }
}