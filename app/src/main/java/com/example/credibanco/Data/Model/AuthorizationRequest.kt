package com.example.credibanco.Data.Model

data class AuthorizationRequest (
    val id: String,
    val commerceCode: String,
    val terminalCode: String,
    val amount: String,
    val card: String
)