package com.example.withpresso

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.adapter.CafeRecyclerViewAdapter
import com.example.withpresso.itemDeco.CafeRecItemDecoration
import com.example.withpresso.service.Cafe
import com.example.withpresso.service.CafeRecommendService
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList

data class CafeList(
    var cafeList: MutableList<Cafe>?,
    var nextPageNum: Int,
    var noMorePage: Boolean
)

class MainActivity : AppCompatActivity() {
    private val LOCATION_ACCESS_CODE = 1002

    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastLat: Double = 0.0
    private var lastLong: Double = 0.0
    private var lat: Double = 0.0
    private var long: Double = 0.0

    private lateinit var cafeList: CafeList

    private lateinit var scope: CoroutineScope

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* location permission */
        setupLocationPermission()

        /* 위치 정보 요청할 준비 */
        initLocation()

        /* init */
        pref = getSharedPreferences("user_info", 0)
        BASE_URL = "https://withpresso.gq"
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        scope = CoroutineScope(Dispatchers.IO)
        cafeList = CafeList(null, 1, false)

        cafes_recycle.layoutManager = GridLayoutManager(this, 2)
        cafes_recycle.addItemDecoration(CafeRecItemDecoration(this))

        scope.launch {
            Log.d("coroutine", "entered")
            initCafeList(cafeList)
            Log.d("initCafeList", "executed")

            scope.launch(Dispatchers.Main) {
                cafeList.cafeList?.let{
                    cafes_recycle.adapter = CafeRecyclerViewAdapter(
                        this@MainActivity,
                        cafeList.cafeList as ArrayList
                    )
                }
                Log.d("adapter", "initialized")
            }
        }

        /* set event listener */
        my_page_button.setOnClickListener {
            if(pref.contains("email") && pref.contains("password")) {
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
            }
        }

        cafe_recommend_swipe.setOnRefreshListener {
            Log.d("RefreshListener", "executed")
            scope.launch {
                Log.d("CoroutineScope", "entered")
                /* 위도 경도 업데이트 */
                lat = lastLat
                long = lastLong

                /* 추천 리스트 초기화 */
                initCafeList(cafeList)
                Log.d("initCafeList", "executed")
                scope.launch(Dispatchers.Main) {
                    cafeList.cafeList?.let {
                        cafes_recycle.adapter = CafeRecyclerViewAdapter(
                            this@MainActivity,
                            cafeList.cafeList as ArrayList<Cafe>
                        )
                        Log.d("adapter", "initialized")
                    }

                    /* 새로고침 마침 */
                    cafe_recommend_swipe.isRefreshing = false
                }
            }
        }

        cafes_recycle.setOnScrollChangeListener { view: View, i: Int, i1: Int, i2: Int, i3: Int ->
            Log.d("ScrollChangeListener", "executed")

            /* recycler view의 스크롤이 최하단 감지 */
            if(!cafes_recycle.canScrollVertically(1)) {
                /* 다음 페이지 요청 가능 확인 */
                if(cafeList.noMorePage) {
                    Toast.makeText(
                        this,
                        "주변의 모든 카페를 추천해드렸습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    /* 다음 페이지 요청하기 */
                    scope.launch {
                        val nextPage = requestPage(cafeList.nextPageNum)
                        Log.d("requestPage", "executed")
                        scope.launch(Dispatchers.Main) {
                            nextPage?.let {
                                cafeList.cafeList!!.addAll(nextPage)
                                cafeList.nextPageNum++
                                (cafes_recycle.adapter as CafeRecyclerViewAdapter).notifyDataSetChanged()
                            }
                            Log.d("cafe list update", "executed")
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        /* gps 기능 활성화(?) */
        requestLocationUpdate()

        if(intent.hasExtra("activity change")) {
            /*
            * 1 -> log in
            * 2 -> log out
            * 3 -> sign up
            * */
            when(intent.getIntExtra("activity change", -1)) {
                1, 2 -> {
                    intent.removeExtra("activity change")
                    scope.launch {
                        initCafeList(cafeList)
                        scope.launch(Dispatchers.Main) {
                            cafeList.cafeList?.let {
                                cafes_recycle.adapter = CafeRecyclerViewAdapter(
                                    this@MainActivity,
                                    cafeList.cafeList as ArrayList<Cafe>
                                )
                                Log.d("adapter", "initialized")
                            }
                        }
                    }
                }
                3 -> {
                    intent.removeExtra("activity change")
                    Log.d("activity change", "sign up")
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("설문 조사 하러 가기")
                        .setMessage("공부가 잘 되는 기준을 알려주세요!\n" +
                                "${pref.getString("nickname", "고객")}님에게 잘 어울리는 카페를 추천해드릴게요.")
                        .setPositiveButton("지금 할게요!") { _: DialogInterface, _: Int ->
                            val intent = Intent(this@MainActivity, SurveyActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("나중에 할래요.") { _: DialogInterface, _: Int ->
                            Toast.makeText(
                                this,
                                "설문 조사는 My Page에서 이용할 수 있어요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .show()

                    scope.launch {
                        initCafeList(cafeList)
                        scope.launch(Dispatchers.Main) {
                            cafeList.cafeList?.let {
                                cafes_recycle.adapter = CafeRecyclerViewAdapter(
                                    this@MainActivity,
                                    cafeList.cafeList as ArrayList<Cafe>
                                )
                                Log.d("adapter", "initialized")
                            }
                        }
                    }
                }
            }
        }

        /* 프로필 이미지 그리기 */
        val userUniqNum = pref.getInt("uniq_num", 0)
        val profileName = pref.getString("profile", "")
        val profileUrl = "${BASE_URL}/profiles/${userUniqNum}/${profileName}"
        drawProfile(this, profileUrl, my_page_button)
    }

    override fun onPause() {
        super.onPause()

        /* gps 기능 멈추기 */
        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        else
            fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /* 프로필 이미지 그리기 */
    private fun drawProfile(context: Context, profileUri: String?, des: ImageView) {
        if(profileUri.isNullOrBlank()) {
            Glide.with(this)
                .load(R.drawable.ic_baseline_person_24)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(des)
        }
        else {
            Glide.with(context)
                .load(profileUri)
                .centerCrop()
                .error(R.drawable.ic_baseline_person_24)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(des)
        }
    }

    /* 위치 정보 접근 권한 */
    private fun setupLocationPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )

        /* 이미 위치 정보 접근을 허용 했음 */
        if(permission == PermissionChecker.PERMISSION_GRANTED){
            return true
        }
        /* 위치 정보 접근이 허용된 적 없음 */
        else {
            val isAccessDenied = ActivityCompat.shouldShowRequestPermissionRationale(
                this, ACCESS_FINE_LOCATION
            )

            /* 최초로 위치 정보에 접근 시도 */
            if(!isAccessDenied) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(ACCESS_FINE_LOCATION),
                    LOCATION_ACCESS_CODE
                )
            }
            /* 위치 정보 접근 권한 제한됨 */
            else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("위치 정보 접근 권한 제한됨")
                dialog.setMessage("  위치 정보 접근을 허용해 주세요.\n\n" +
                        "  app info -> Permissions -> location")
                dialog.show()
            }

            return false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            /* 위치 정보에 접근이 허용되면 실행 */
            LOCATION_ACCESS_CODE -> {
                if(grantResults.isNotEmpty() &&
                    grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {

                }
            }
        }

        return
    }

    private fun initLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if(location == null) {
                    Log.e("location error", "location get fail")
                } else {
                    Log.d("location", "${location.latitude} , ${location.longitude}")
                    lastLat = location.latitude
                    lastLong = location.longitude
                }
            }
            .addOnFailureListener {
                Log.e("location error", "location error is ${it.message}")
                it.printStackTrace()
            }
    }

    /* 위치를 얻을 수 있으면 위도, 경도를 반환. 위도, 경도를 얻을 수 없으면 null을 반환 */
    private fun requestLocationUpdate() {
        /* 위치 정보 권한 검사 */
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_ACCESS_CODE
            )

            return
        }


        locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 60 * 1000
        }

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for((i, location) in it.locations.withIndex()) {
                        Log.d("history", "#$i ${location.latitude} , ${location.longitude}")
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private suspend fun initCafeList(cafeList: CafeList) {
        cafeList.cafeList?.clear()
        cafeList.nextPageNum = 1
        cafeList.noMorePage = false

        /* 첫 번째 페이지 가져오기 */
        cafeList.cafeList = requestPage(1)

        /* 실패, 뭐라도 반환하도록 해서 null이 나오는 상황 만들지 말기 */
        if(cafeList.cafeList == null)
            cafeList.noMorePage = true
        else
            cafeList.nextPageNum = 2
    }

    private suspend fun requestPage(page: Int): ArrayList<Cafe>? {
        /* 위치 권한이 중간에 막히면 requestLocationUpdate가 null을 반환할 수 있음 */
        val uniq_num = pref.getInt("uniq_num", 0)
        val cafeRecommendService = retrofit.create(CafeRecommendService::class.java)

        val nextCafeList = cafeRecommendService.requestCafeRecommendList(
            37.5078, 126.9601,
            uniq_num,
            page
        )

        return nextCafeList
    }
}