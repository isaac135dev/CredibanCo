package com.example.credibanco.Data.Model

data class LocalDatabaseAuthorization (
    val receiptId: String?,
    val rrn: String?,
    val statusCode: String?,
    val statusDescription: String?,
    var isCancelled: Boolean = false
)