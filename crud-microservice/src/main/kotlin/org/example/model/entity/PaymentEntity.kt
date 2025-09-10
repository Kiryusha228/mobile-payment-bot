package org.example.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payment")
class PaymentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,

    @ManyToOne
    @JoinColumn(name = "phone_id", nullable = false)
    var phone: PhoneEntity,

    @Column(name = "amount", nullable = false)
    var amount: Double,

    @Column(name = "provided_at", nullable = false)
    var providedAt: LocalDateTime,

    @Column(name = "provider_tx_id", nullable = false)
    var providerTxId: Long
)