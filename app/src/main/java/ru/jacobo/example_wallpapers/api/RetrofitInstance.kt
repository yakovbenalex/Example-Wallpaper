package ru.jacobo.example_wallpapers.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.jacobo.example_wallpapers.util.Constants.Companion.BASE_URL

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: RetrofitAPI by lazy {
        retrofit.create(RetrofitAPI::class.java)
    }
}