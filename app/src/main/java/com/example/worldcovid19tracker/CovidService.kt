package com.example.worldcovid19tracker

import retrofit2.Call
import retrofit2.http.GET

interface CovidService {

    @GET("summary")
    fun getProvincialData(): Call<Summary>

    @GET("summary?loc=canada")
    fun getNationalData(): Call<Summary>
}