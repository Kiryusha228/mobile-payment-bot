package org.example.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "payment_user")
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "chat_id", nullable = false)
    var chatId: Long,

    @Column(name = "username", nullable = false)
    var username: String
)