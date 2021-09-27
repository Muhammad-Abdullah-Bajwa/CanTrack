package com.example.worldcovid19tracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.worldcovid19tracker.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import org.eazegraph.lib.models.PieModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "MainActivity"
private const val BASE_URL = "https://api.opencovid.ca/"

class MainActivity : AppCompatActivity() {

    private lateinit var provincialData: Map<String, List<CovidData>>
    private lateinit var nationalDailyData: CovidData
    private lateinit var binding: ActivityMainBinding
    private var province = "Canada"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.extras?.getString("province") != null) {
            province = intent.extras?.getString("province")!!
        }

        val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        val gson = GsonBuilder().setDateFormat("dd-MM-yyyy").create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val covidService: CovidService = retrofit.create(CovidService::class.java)

        covidService.getNationalData().enqueue(object : Callback<Summary> {
            @SuppressLint("NewApi", "SetTextI18n")
            override fun onResponse(call: Call<Summary>, response: Response<Summary>) {
                Log.i(TAG, "onResponse $response")
                val nationalData = response.body()

                if (nationalData == null) {
                    Log.w(TAG, "Got an invalid response from server")
                    return
                }

                nationalDailyData = nationalData.summary[0]
                Log.i(TAG, "Update graph with new daily national numbers")

                if (province == "Canada") {
                    binding.countryName.text = province

                    binding.ConfirmedNumbers.text =
                        NumberFormat.getInstance().format(nationalDailyData.cumulative_cases)
                    binding.ConfirmedNumbersToday.text =
                        NumberFormat.getInstance().format(nationalDailyData.active_cases_change)

                    binding.ActiveNumbers.text =
                        NumberFormat.getInstance().format(nationalDailyData.active_cases)
                    binding.ActiveNumbersToday.text =
                        NumberFormat.getInstance().format(nationalDailyData.active_cases_change)

                    binding.RecoveredNumbers.text =
                        NumberFormat.getInstance().format(nationalDailyData.cumulative_recovered)
                    binding.RecoveredNumbersToday.text =
                        NumberFormat.getInstance().format(nationalDailyData.recovered)

                    binding.DeadNumbers.text =
                        NumberFormat.getInstance().format(nationalDailyData.cumulative_deaths)
                    binding.DeathNumbersToday.text =
                        NumberFormat.getInstance().format(nationalDailyData.deaths)

                    binding.TestsNumbers.text =
                        NumberFormat.getInstance().format(nationalDailyData.cumulative_testing)
                    binding.TestsNumbersToday.text =
                        NumberFormat.getInstance().format(nationalDailyData.testing)

                    binding.lastUpdated.text =
                        "Last updated at: " + outputDateFormat.format(nationalDailyData.date)

                    binding.piechart.addPieSlice(
                        PieModel(
                            "Confirmed",
                            nationalDailyData.cumulative_cases.toFloat(),
                            getColor(R.color.yellow)
                        )
                    )
                    binding.piechart.addPieSlice(
                        PieModel(
                            "Active",
                            nationalDailyData.active_cases.toFloat(),
                            getColor(R.color.blue_pie)
                        )
                    )
                    binding.piechart.addPieSlice(
                        PieModel(
                            "Recovered",
                            nationalDailyData.cumulative_recovered.toFloat(),
                            getColor(R.color.green_pie)
                        )
                    )
                    binding.piechart.addPieSlice(
                        PieModel(
                            "Death",
                            nationalDailyData.cumulative_deaths.toFloat(),
                            getColor(R.color.red_pie)
                        )
                    )

                    binding.piechart.startAnimation()
                }

            }

            override fun onFailure(call: Call<Summary>, t: Throwable) {
                Log.e(TAG, "onFailure $t")
            }

        })

        covidService.getProvincialData().enqueue(object : Callback<Summary> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(call: Call<Summary>, response: Response<Summary>) {
                Log.i(TAG, "onResponse $response")
                val nationalData = response.body()

                if (nationalData == null) {
                    Log.w(TAG, "Got an invalid response from server")
                    return
                }

                nationalData.summary.toMutableList().add(nationalDailyData)

                provincialData = nationalData.summary.groupBy { it.province }

                val data = provincialData[province]?.first()

                if (data != null) {
                    setValue(data)
                }



                Log.i(TAG, "Update graph with new daily national numbers")
            }

            override fun onFailure(call: Call<Summary>, t: Throwable) {
                Log.e(TAG, "onFailure $t")
            }
        })

        binding.countryName.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProvinceActivity::class.java
                )
            )
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun setValue (provincialData: CovidData) {
        val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        binding.countryName.text = province

        binding.ConfirmedNumbers.text =
            NumberFormat.getInstance().format(provincialData.cumulative_cases)
        binding.ConfirmedNumbersToday.text =
            NumberFormat.getInstance().format(provincialData.active_cases_change)

        binding.ActiveNumbers.text =
            NumberFormat.getInstance().format(provincialData.active_cases)
        binding.ActiveNumbersToday.text =
            NumberFormat.getInstance().format(provincialData.active_cases_change)

        binding.RecoveredNumbers.text =
            NumberFormat.getInstance().format(provincialData.cumulative_recovered)
        binding.RecoveredNumbersToday.text =
            NumberFormat.getInstance().format(provincialData.recovered)

        binding.DeadNumbers.text =
            NumberFormat.getInstance().format(provincialData.cumulative_deaths)
        binding.DeathNumbersToday.text =
            NumberFormat.getInstance().format(provincialData.deaths)

        binding.TestsNumbers.text =
            NumberFormat.getInstance().format(provincialData.cumulative_testing)
        binding.TestsNumbersToday.text =
            NumberFormat.getInstance().format(provincialData.testing)

        binding.lastUpdated.text =
            "Last updated at: " + outputDateFormat.format(provincialData.date)

        binding.piechart.addPieSlice(
            PieModel(
                "Confirmed",
                provincialData.cumulative_cases.toFloat(),
                getColor(R.color.yellow)
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                "Active",
                provincialData.active_cases.toFloat(),
                getColor(R.color.blue_pie)
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                "Recovered",
                provincialData.cumulative_recovered.toFloat(),
                getColor(R.color.green_pie)
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                "Death",
                provincialData.cumulative_deaths.toFloat(),
                getColor(R.color.red_pie)
            )
        )

        binding.piechart.startAnimation()

    }
}