package org.example.model.entity

import jakarta.persistence.*

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
    var phoneNumber: String
)