package com.example.worldcovid19tracker

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.worldcovid19tracker.databinding.ActivityProvinceBinding
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.opencovid.ca/"
private lateinit var binding: ActivityProvinceBinding
private lateinit var provincialData: MutableList<CovidData>
private lateinit var nationalDailyData: CovidData


class ProvinceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProvinceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val recyclerview = findViewById<RecyclerView>(R.id.provinces)

        recyclerview.layoutManager = LinearLayoutManager(this)

        val gson = GsonBuilder().setDateFormat("dd-MM-yyyy").create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val covidService: CovidService = retrofit.create(CovidService::class.java)

        covidService.getNationalData().enqueue(object : Callback<Summary> {
            @SuppressLint("NewApi", "SetTextI18n", "NotifyDataSetChanged")
            override fun onResponse(call: Call<Summary>, response: Response<Summary>) {
                Log.i(TAG, "onResponse $response")
                val nationalData = response.body()

                if (nationalData == null) {
                    Log.w(TAG, "Got an invalid response from server")
                    return
                }

                nationalDailyData = (nationalData.summary[0])
            }

            override fun onFailure(call: Call<Summary>, t: Throwable) {
                Log.e(TAG, "onFailure $t")            }
        }
        )

        covidService.getProvincialData().enqueue(object : Callback<Summary> {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<Summary>, response: Response<Summary>) {
                Log.i(TAG, "onResponse $response")
                val nationalData = response.body()

                if (nationalData == null) {
                    Log.w(TAG, "Got an invalid response from server")
                    return
                }

                provincialData = (nationalData.summary as MutableList<CovidData>)
                provincialData.removeIf {
                    it.province == "Repatriated"
                }
                provincialData.add(nationalDailyData)

                provincialData.sortBy { it.province }

                val adapter = ProvinceAdapter(provincialData)
                recyclerview.adapter = adapter

                Log.i(TAG, "Update graph with new daily national numbers")
            }

            override fun onFailure(call: Call<Summary>, t: Throwable) {
                Log.e(TAG, "onFailure $t")
            }
        })


    }
}