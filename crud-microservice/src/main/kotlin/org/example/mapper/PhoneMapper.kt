package org.example.mapper

import org.example.model.dto.CreatePhoneDto
import org.example.model.entity.PhoneEntity

import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PhoneMapper {
    fun toEntity(createPhoneDto: CreatePhoneDto): PhoneEntity
}