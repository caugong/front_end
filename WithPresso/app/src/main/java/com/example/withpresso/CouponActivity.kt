package com.example.withpresso

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.withpresso.adapter.CouponAdapter
import com.example.withpresso.itemDeco.CafeRecItemDecoration
import com.example.withpresso.itemDeco.CouponItemDecoration
import com.example.withpresso.service.Coupon
import com.example.withpresso.service.CouponService
import kotlinx.android.synthetic.main.activity_coupon.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CouponActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String
    private lateinit var scope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)

        /* init */
        pref = getSharedPreferences("user_info", 0)
        BASE_URL = "https://withpresso.gq"
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        scope = CoroutineScope(Dispatchers.Main)
        coupon_recycler.layoutManager = LinearLayoutManager(this)
        val dividerItemDeco = DividerItemDecoration(coupon_recycler.context, LinearLayoutManager(this).orientation)
        coupon_recycler.addItemDecoration(dividerItemDeco)
        coupon_back_button.setOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()

        val user_asin = pref.getInt("uniq_num", 0)
        val couponService = retrofit.create(CouponService::class.java)
        couponService.requestCoupon(user_asin).enqueue(object :Callback<List<Coupon>> {
            /* 통신 실패 */
            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                Log.e("coupon list request", "failed")
                toast("쿠폰 받아오기 실패")
            }

            /* 통신 성공 */
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                Log.d("coupon list request", "success")

                val couponList = response.body()
                couponList?.let {
                    coupon_recycler.adapter = CouponAdapter(
                        this@CouponActivity, couponList as ArrayList<Coupon>
                    )
                }
            }
        })
    }
}