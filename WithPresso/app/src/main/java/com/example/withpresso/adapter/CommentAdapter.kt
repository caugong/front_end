package com.example.withpresso.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.R
import com.example.withpresso.Review
import com.example.withpresso.service.Comment
import kotlinx.android.synthetic.main.activity_review.view.*
import kotlinx.android.synthetic.main.review_layout.view.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentHolder(comment: View): RecyclerView.ViewHolder(comment) {
    var profile_photo = comment.profile_photo
    var user_name = comment.user_name
    var comment = comment.review_content
}

class CommentAdapter(
    val context: Context,
    val commentList: List<Comment>
): RecyclerView.Adapter<CommentHolder>() {
    private val pref = context.getSharedPreferences("user_info", 0)
    private val BASE_URL = "https://withpresso.gq"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val comment = LayoutInflater.from(context).inflate(R.layout.review_layout, parent, false)

        return CommentHolder(comment)
    }

    override fun getItemCount(): Int = commentList.size

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        val curr_comment = commentList[position]

        holder.user_name.text = curr_comment.user_name
        holder.comment.text = curr_comment.review
        drawCafePhoto(
            "${BASE_URL}/profiles/${curr_comment.user_asin}/1.jpg",
            holder.profile_photo
        )
    }

    private fun drawCafePhoto(photoUrl: String?, imageView: ImageView) {
        if(photoUrl.isNullOrBlank()) {
            Glide.with(context)
                .load(R.drawable.ic_baseline_person_24)
                .override(300)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }
        else{
            Glide.with(context)
                .load(photoUrl)
                .override(300)
                .centerCrop()
                .error(R.drawable.ic_baseline_person_24)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }
    }
}