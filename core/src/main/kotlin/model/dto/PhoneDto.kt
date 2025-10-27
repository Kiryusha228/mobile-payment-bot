package model.dto

import enums.Provider

data class PhoneDto (
    val id : Long,
    val phoneNumber: String,
    val provider: Provider,
    val isMain: Boolean
)