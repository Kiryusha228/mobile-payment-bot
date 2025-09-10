package org.example.mapper

import org.example.model.dto.CreateUserDto
import org.example.model.entity.UserEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserMapper {
    fun toEntity(userDto: CreateUserDto): UserEntity
}