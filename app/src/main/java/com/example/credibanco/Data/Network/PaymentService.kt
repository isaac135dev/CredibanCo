package com.example.credibanco.Data.Network


import com.example.credibanco.Data.Model.AnnulmentData
import com.example.credibanco.Data.Model.AnnulmentRequest
import com.example.credibanco.Data.Model.AuthorizationDataModel
import com.example.credibanco.Data.Model.AuthorizationRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PaymentService {
    @Headers(
        "Content-type: application/json"
    )
    @POST("/api/payments/authorization")
    fun authorizePayment(
        @Header("Authorization") basicAuth: String,
        @Body request: AuthorizationRequest
    ): Call<AuthorizationDataModel>

    @Headers("Content-type: application/json")
    @POST("/api/payments/annulment")
    fun annulmentPayment(
        @Header("Authorization") basicAuth: String,
        @Body request: AnnulmentRequest
    ): Call<AnnulmentData>

}