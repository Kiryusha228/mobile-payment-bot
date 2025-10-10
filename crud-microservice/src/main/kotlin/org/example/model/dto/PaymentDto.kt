package org.example.model.dto

import java.time.LocalDateTime

data class PaymentDto(
    val id: Long,
    val amount: Double,
    val providedAt: LocalDateTime,
    val providerTxId: String,
)