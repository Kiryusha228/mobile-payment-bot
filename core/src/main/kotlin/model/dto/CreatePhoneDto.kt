package model.dto

import enums.Provider

data class CreatePhoneDto (
    val userId: Long,
    val phoneNumber: String,
    val provider: Provider
)