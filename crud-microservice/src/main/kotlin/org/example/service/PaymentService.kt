package org.example.service

import org.example.mapper.PaymentMapper
import org.example.model.dto.CreatePaymentDto
import org.example.model.entity.PaymentEntity
import org.example.repository.PaymentRepository
import org.example.repository.PhoneRepository
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    val paymentRepository: PaymentRepository,
    val userRepository: UserRepository,
    val phoneRepository: PhoneRepository,
    val paymentMapper: PaymentMapper
) {
    @Transactional
    fun createPayment(createPaymentDto: CreatePaymentDto) : PaymentEntity {
        val paymentEntity = paymentMapper.toEntity(createPaymentDto)
        paymentEntity.user = userRepository.findById(createPaymentDto.userId).get()
        paymentEntity.phone = phoneRepository.findById(createPaymentDto.phoneId).get()
        return paymentRepository.save(paymentEntity)
    }

    fun getPaymentById(paymentId: Long): PaymentEntity {
        return paymentRepository.findById(paymentId).get()
    }

    @Transactional
    fun deletePaymentById(paymentId: Long) {
        paymentRepository.deleteById(paymentId)
    }
}