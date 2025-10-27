package model.entity

import enums.Provider

data class PhoneEntity(
    var id: Long? = null,
    var user: UserEntity? = null,
    var phoneNumber: String,
    var provider: Provider,
    var isMain: Boolean = false
)