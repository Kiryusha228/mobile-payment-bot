package model.entity

data class UserEntity(
    var id: Long? = null,
    var chatId: Long,
    var username: String
)