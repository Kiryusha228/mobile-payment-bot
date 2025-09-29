package org.example.model.entity

import java.time.LocalDateTime

class PaymentEntity(

    var id: Long? = null,
    var user: UserEntity? = null,
    var phone: PhoneEntity? = null,
    var amount: Double,
    var providedAt: LocalDateTime,
    var providerTxId: Long
)