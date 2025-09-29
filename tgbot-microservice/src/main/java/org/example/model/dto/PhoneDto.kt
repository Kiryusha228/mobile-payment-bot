package org.example.model.dto

import org.example.enums.Provider

data class PhoneDto (
    val id : Long,
    val phoneNumber: String,
    val provider: Provider,
    val isMain: Boolean
)