package org.example.model.dto

import java.time.LocalDateTime

data class CreatePaymentDto (
    val userId: Long,
    val phoneId: String,
    val amount: Double,
    val providedAt: LocalDateTime,
    val providerTxId: Long
)