package com.example.withpressoowner.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.withpressoowner.R
import com.example.withpressoowner.adapter.ReviewAdapter
import com.example.withpressoowner.service.ReadReviewService
import kotlinx.android.synthetic.main.fragment_review.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewFragment : Fragment() {
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireContext().getSharedPreferences("owner_info", 0)
        BASE_URL = "https://withpresso.gq"
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        comment_recycler.layoutManager = LinearLayoutManager(requireContext())
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.IO).launch {
            val cafe_asin = pref.getInt("cafe_asin", 0)
            val readReviewService = retrofit.create(ReadReviewService::class.java)
            val commentList = readReviewService.requestReadReview(cafe_asin)

            this.launch(Dispatchers.Main) {
                toast("cafe_asin: ${cafe_asin}")
                var mean_rating = arguments?.getFloat("mean_rating") ?: 0f
                mean_rating = Math.round(mean_rating * 10f) / 10f
                total_ratingBar.rating = mean_rating
                review_mean_point.text = "${getString(R.string.review_point)} ${mean_rating}점"

                total_num_of_review.text = "${getString(R.string.total_num_of_review)} ${commentList.size}개"

                comment_recycler.adapter = ReviewAdapter(requireContext(), commentList)
            }
        }
    }
}