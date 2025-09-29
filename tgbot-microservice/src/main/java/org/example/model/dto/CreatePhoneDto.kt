package org.example.model.dto

import org.example.enums.Provider

data class CreatePhoneDto (
    val userId: Long,
    val phoneNumber: String,
    val provider: Provider
)