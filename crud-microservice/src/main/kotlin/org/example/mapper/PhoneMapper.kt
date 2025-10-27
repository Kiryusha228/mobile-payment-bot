package org.example.mapper

import model.dto.CreatePhoneDto
import model.dto.PhoneDto
import org.example.model.entity.PhoneEntity

import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface PhoneMapper {
    fun toEntity(createPhoneDto: CreatePhoneDto): PhoneEntity
    @Mapping(target = "isMain", source = "main")
    fun toDto(phoneEntity: PhoneEntity): PhoneDto
    fun toDtoList(phoneEntityList: List<PhoneEntity>): List<PhoneDto>
}