package org.example.model.dto

import java.time.LocalDateTime

data class YoomoneyWebhookResponse (
    val status: String,
    val amount: Double,
    val operationId: String,
    val dateTime: LocalDateTime,
    val sha1Hash: String,
    val phoneId: Long,
    val userId: Long
)