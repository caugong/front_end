package com.example.withpresso.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.R
import com.example.withpresso.service.Coupon
import kotlinx.android.synthetic.main.coupon_code_dialog.view.*
import kotlinx.android.synthetic.main.coupon_icon.view.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.windowManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CouponHolder(item: View): RecyclerView.ViewHolder(item) {
    var layout = item.coupon_layout
    var photo = item.coupon_cafe_image
    var discount = item.coupon_discount
    var cafe_name_and_type = item.coupon_cafe_name_and_type
    var validity = item.coupon_validity
}

class CouponAdapter(
    val context: Context,
    val couponList: ArrayList<Coupon>
): RecyclerView.Adapter<CouponHolder>() {
    private val BASE_URL = "https://withpresso.gq"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponHolder {
        val coupon = LayoutInflater.from(context).inflate(R.layout.coupon_icon, null)

        return CouponHolder(coupon)
    }

    override fun getItemCount(): Int = couponList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CouponHolder, position: Int) {
        val curr_coupon = couponList[position]

        holder.discount.text = "${curr_coupon.discount}%"
        holder.cafe_name_and_type.text = "[${curr_coupon.cafe_name}] ${curr_coupon.coupon_type}"
        holder.validity.text = "유효기간: ${curr_coupon.validity}"
        drawCafePhoto(
            "${BASE_URL}/cafe_pics/${curr_coupon.cafe_asin}/1.jpg",
            holder.photo
        )

        holder.itemView.setOnClickListener {
            val couponCodeDialog = LayoutInflater.from(context).inflate(R.layout.coupon_code_dialog, null)
            couponCodeDialog.name_and_type.text = "[${curr_coupon.cafe_name}] ${curr_coupon.coupon_type}"
            couponCodeDialog.code.text = curr_coupon.coupon_code

            val alert = AlertDialog.Builder(context)
                .setView(couponCodeDialog)
                .setNegativeButton("돌아가기") { _: DialogInterface, _: Int -> }
                .show()
        }
    }

    private fun drawCafePhoto(photoUrl: String?, imageView: ImageView) {
        if(photoUrl.isNullOrBlank()) {
            Glide.with(context)
                .load(R.drawable.coffee)
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
                .error(R.drawable.coffee)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }
    }
}