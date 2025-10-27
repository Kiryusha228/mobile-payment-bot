package org.example.mapper

import model.dto.CreatePaymentDto
import model.dto.PaymentDto
import org.example.model.entity.PaymentEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping


@Mapper(componentModel = "spring")
interface PaymentMapper {

    @Mapping(target = "providedAt", expression = "java(java.time.LocalDateTime.now())")
    fun toEntity(createPaymentDto: CreatePaymentDto): PaymentEntity

    @Mapping(target = "phoneId", source = "phone.id")
    fun toDto(paymentEntity: PaymentEntity): PaymentDto
}