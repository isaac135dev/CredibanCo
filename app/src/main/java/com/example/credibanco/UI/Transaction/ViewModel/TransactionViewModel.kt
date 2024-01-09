package com.example.credibanco.UI.Transaction.ViewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.credibanco.Core.PaymentApiClient
import com.example.credibanco.Data.DataBase.TransactionDatabase
import com.example.credibanco.Data.Model.AnnulmentData
import com.example.credibanco.Data.Model.AnnulmentRequest
import com.example.credibanco.Data.Model.AuthorizationDataModel
import com.example.credibanco.Data.Model.AuthorizationRequest
import com.example.credibanco.Data.Model.LocalDatabaseAuthorization
import com.example.credibanco.Data.Network.PaymentService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class TransactionViewModel : ViewModel() {

    private val _authorizationState = MutableLiveData<AuthorizationState>()

    private lateinit var receiptId: String
    private lateinit var rrn: String

    val authorizationState: LiveData<AuthorizationState>
        get() = _authorizationState

    enum class AuthorizationState {
        LOADING,
        SUCCESS,
        ERROR
    }

    fun paymentApiConnectAnnulation(context: Context, commerceCode: String, terminalCode: String) {
        viewModelScope.launch {
            val retrofit = PaymentApiClient.PostPaymentAnnulment()
            val paymentService = retrofit.create(PaymentService::class.java)
            val request = AnnulmentRequest(receiptId, rrn)

            val credentials = "$commerceCode$terminalCode"
            val base64Credentials =
                Base64.encodeToString(credentials.encodeToByteArray(), Base64.NO_WRAP)
            val basicAuth = "Basic $base64Credentials"
            val call = paymentService.annulmentPayment(basicAuth, request)

            call.enqueue(object : Callback<AnnulmentData> {
                override fun onResponse(
                    call: Call<AnnulmentData>,
                    response: Response<AnnulmentData>
                ) {
                    val annulmentDataModel = response.body()
                    val authorizationDataModel: AuthorizationDataModel

                    if (response.isSuccessful) {

                        if (annulmentDataModel != null) {

                            val activity = context as? Activity
                            if (activity != null && !activity.isFinishing) {
                                val localTransaction = LocalDatabaseAuthorization(
                                    receiptId = receiptId,
                                    rrn = rrn,
                                    statusCode = annulmentDataModel.statusCode,
                                    statusDescription = annulmentDataModel.statusDescription
                                )

                                val dbHelper = TransactionDatabase(context)
                                dbHelper.insertTransaction(localTransaction)

                                // Mostrar el cuadro de diálogo exitoso
                                MaterialAlertDialogBuilder(context)
                                    .setTitle("Operacion Exitosa")
                                    .setMessage("Transaccion: ${annulmentDataModel.statusDescription}")
                                    .setOnDismissListener {
                                        activity.finish()
                                    }
                                    .setPositiveButton("Aceptar") { dialog, _ ->
                                        activity.finish()
                                    }
                                    .show()
                            }
                        } else {
                            println("Error: No se pudo convertir la respuesta a AuthorizationDataModel")
                        }
                    } else {
                        println("Error: ${response.code()} - ${response.message()}")
                        val errorBody = response.errorBody()?.string()
                        println("Error body: $errorBody")


                        MaterialAlertDialogBuilder(context)
                            .setTitle("Error en la Transacción")
                            .setMessage("No se pudo completar la transacción. Verifique los datos e inténtelo de nuevo.")
                            .setNegativeButton("Cancelar") { dialog, _ ->

                                dialog.dismiss()
                            }
                            .setPositiveButton("Reintentar") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }

                override fun onFailure(call: Call<AnnulmentData>, t: Throwable) {
                    // Manejar el fallo aquí
                    println("DEBUG RESPONSE Error: ${t.message}")
                }
            })
        }
    }


    fun paymentApiConnect(
        context: Context,
        commerceCode: String,
        terminalCode: String,
        amount: String,
        card: String
    ) {
        viewModelScope.launch {
            _authorizationState.value = AuthorizationState.LOADING

            val retrofit = PaymentApiClient.postPaymentAuthorization()
            val paymentService = retrofit.create(PaymentService::class.java)
            val id = UUID.randomUUID().toString()
            val request = AuthorizationRequest(id, commerceCode, terminalCode, amount, card)
            val credentials = "$commerceCode$terminalCode"

            val base64Credentials =
                Base64.encodeToString(credentials.encodeToByteArray(), Base64.NO_WRAP)
            val basicAuth = "Basic $base64Credentials"
            val call = paymentService.authorizePayment(basicAuth, request)

            call.enqueue(object : Callback<AuthorizationDataModel> {
                override fun onResponse(
                    call: Call<AuthorizationDataModel>,
                    response: Response<AuthorizationDataModel>
                ) {
                    val authorizationDataModel = response.body()

                    if (response.isSuccessful) {
                        _authorizationState.value = AuthorizationState.SUCCESS

                        if (authorizationDataModel != null) {
                            receiptId = authorizationDataModel.receiptId
                            rrn = authorizationDataModel.rrn
                        } else {
                            MaterialAlertDialogBuilder(context)
                                .setTitle("Operacion Exitosa")
                                .setMessage("Transaccion: No se pudo Obtener una respuesta ")
                                .show()
                        }
                    } else {
                        _authorizationState.value = AuthorizationState.ERROR

                        println("Error: ${response.code()} - ${response.message()}")
                        val errorBody = response.errorBody()?.string()
                        println("Error body: $errorBody")

                        val localTransaction = LocalDatabaseAuthorization(
                            receiptId = authorizationDataModel?.receiptId,
                            rrn = authorizationDataModel?.rrn,
                            statusCode = authorizationDataModel?.statusCode,
                            statusDescription = authorizationDataModel?.statusDescription ?: "denegada"
                        )

                        val dbHelper = TransactionDatabase(context)
                        dbHelper.insertTransaction(localTransaction)

                        MaterialAlertDialogBuilder(context)
                            .setTitle("Error en la Transacción")
                            .setMessage("No se pudo completar la transacción. Verifique los datos e inténtelo de nuevo. Error: ${authorizationDataModel?.statusDescription}")
                            .setNegativeButton("Cancelar") { dialog, _ ->
                                // Tu código aquí
                                dialog.dismiss()
                            }
                            .setPositiveButton("Reintentar") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }

                override fun onFailure(call: Call<AuthorizationDataModel>, t: Throwable) {
                    _authorizationState.value = AuthorizationState.ERROR
                    println("DEBUG RESPONSE Error: ${t.message}")
                }
            })
        }
    }

}