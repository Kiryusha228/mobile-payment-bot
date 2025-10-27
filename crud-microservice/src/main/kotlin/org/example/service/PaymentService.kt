package org.example.service

import model.dto.CreatePaymentDto
import model.dto.PaymentDto
import org.example.mapper.PaymentMapper
import org.example.model.entity.PaymentEntity
import org.example.repository.PaymentRepository
import org.example.repository.PhoneRepository
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val userRepository: UserRepository,
    private val phoneRepository: PhoneRepository,
    private val paymentMapper: PaymentMapper
) {
    @Transactional
    fun createPayment(createPaymentDto: CreatePaymentDto): PaymentEntity =
        paymentMapper.toEntity(createPaymentDto).apply {
            user = userRepository.findById(createPaymentDto.userId).get()
            phone = phoneRepository.findById(createPaymentDto.phoneId).get()
        }.let {
            paymentRepository.save(it)
        }


    fun getPaymentById(paymentId: Long): PaymentDto =
        paymentRepository.findById(paymentId).get().let {
            paymentMapper.toDto(it)
        }

    @Transactional
    fun deletePaymentById(paymentId: Long) =
        paymentRepository.deleteById(paymentId)
}