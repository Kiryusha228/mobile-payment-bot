package org.example.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentResponseDto(
    val status: String,
    val request_id: String? = null
)