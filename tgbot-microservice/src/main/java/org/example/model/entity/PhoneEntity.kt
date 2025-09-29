package org.example.model.entity

import org.example.enums.Provider

class PhoneEntity(
    var id: Long? = null,
    var user: UserEntity? = null,
    var phoneNumber: String,
    var provider: Provider,
    var isMain: Boolean = false
)