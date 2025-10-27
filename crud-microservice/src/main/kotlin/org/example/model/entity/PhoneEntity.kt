package org.example.model.entity

import jakarta.persistence.*
import enums.Provider

@Entity
@Table(name = "phone")
class PhoneEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null,

    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,

    @Column(name = "provider", nullable = false)
    var provider: Provider,

    @Column(name = "is_main", nullable = false)
    var isMain: Boolean = false
)