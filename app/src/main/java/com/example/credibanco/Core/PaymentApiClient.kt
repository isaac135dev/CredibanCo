package com.example.credibanco.Core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PaymentApiClient {
    fun postPaymentAuthorization(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.13:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun PostPaymentAnnulment(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.13:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}