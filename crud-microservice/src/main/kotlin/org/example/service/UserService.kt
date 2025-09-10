package org.example.service

import org.example.model.dto.CreateUserDto
import org.example.model.entity.UserEntity
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userRepository: UserRepository
) {
    @Transactional
    fun createUser(createUserDto: CreateUserDto): UserEntity {
        val user = UserEntity(
            chatId = createUserDto.chatId,
            username = createUserDto.username
        )
        return userRepository.save(user)
    }

    fun getUserById(id: Long): UserEntity {
        return userRepository.findById(id).get()
    }

    @Transactional
    fun deleteUserById(id: Long) {
        userRepository.deleteById(id)
    }
}