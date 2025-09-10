package org.example.model.dto

data class CreatePaymentDto (
    val userId: Long,
    val phoneId: Long,
    val amount: Double,
    val providerTxId: Long
)