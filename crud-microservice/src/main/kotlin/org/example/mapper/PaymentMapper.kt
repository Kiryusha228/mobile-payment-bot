package org.example.mapper

import org.example.model.dto.CreatePaymentDto
import org.example.model.entity.PaymentEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping


@Mapper(componentModel = "spring")
interface PaymentMapper {

    @Mapping(target = "providedAt", expression = "java(java.time.LocalDateTime.now())")
    fun toEntity(createPaymentDto: CreatePaymentDto): PaymentEntity

}