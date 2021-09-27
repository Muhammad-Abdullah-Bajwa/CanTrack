package com.example.worldcovid19tracker

import com.google.gson.annotations.SerializedName
import java.util.*

data class CovidData(
    @SerializedName("active_cases") val active_cases: Int,
    @SerializedName("active_cases_change") val active_cases_change: Int,
    @SerializedName("cases") val cases: Int,
    @SerializedName("cumulative_cases") val cumulative_cases: Int,
    @SerializedName("cumulative_deaths") val cumulative_deaths: Int,
    @SerializedName("cumulative_recovered") val cumulative_recovered: Int,
    @SerializedName("cumulative_testing") val cumulative_testing: Int,
    @SerializedName("deaths") val deaths: Int,
    @SerializedName("recovered") val recovered: Int,
    @SerializedName("testing") val testing: Int,
    @SerializedName("province") val province: String,
    @SerializedName("date") val date: Date,
)

data class Summary(
    @SerializedName("summary") val summary: List<CovidData>
)
