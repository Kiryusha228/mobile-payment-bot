package org.example.service

import org.example.mapper.UserMapper
import org.example.model.dto.CreateUserDto
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
    fun createUser(createUserDto: CreateUserDto): UserEntity {
        return userRepository.save(userMapper.toEntity(createUserDto))
    }

    fun getUserById(userId: Long): UserEntity {
        return userRepository.findById(userId).get()
    }

    @Transactional
    fun deleteUserById(userId: Long) {
        userRepository.deleteById(userId)
    }
}