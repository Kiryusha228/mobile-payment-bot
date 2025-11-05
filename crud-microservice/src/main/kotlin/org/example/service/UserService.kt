package org.example.service

import model.dto.CreateUserDto
import org.example.mapper.UserMapper
import org.example.model.entity.UserEntity
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userRepository: UserRepository,
    val userMapper: UserMapper
) {
    @Transactional
    fun createUser(createUserDto: CreateUserDto): UserEntity =
        userRepository.save(userMapper.toEntity(createUserDto))


    fun getUserById(userId: Long): UserEntity =
        userRepository.findById(userId).get()

    fun getUserIdByChatId(chatId: Long): Long =
        userRepository.findByChatId(chatId).id
            ?: throw IllegalArgumentException("Пользователь не найден")

    @Transactional
    fun deleteUserById(userId: Long) =
        userRepository.deleteById(userId)

}