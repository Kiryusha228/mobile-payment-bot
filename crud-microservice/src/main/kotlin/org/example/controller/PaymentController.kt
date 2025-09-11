package org.example.controller

import org.example.model.dto.CreatePaymentDto
import org.example.model.dto.PaymentDto
import org.example.model.entity.PaymentEntity
import org.example.service.PaymentService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payment")
class PaymentController (
    val paymentService: PaymentService
) {
    @PostMapping("/create")
    fun createPayment(@RequestBody createPaymentDto: CreatePaymentDto) : PaymentEntity {
        return paymentService.createPayment(createPaymentDto)
    }

    @GetMapping("/get/{paymentId}")
    fun getPhoneById(@PathVariable paymentId: Long): PaymentDto {
        return paymentService.getPaymentById(paymentId)
    }

    @DeleteMapping("/delete/{paymentId}")
    fun deleteUserById(@PathVariable paymentId: Long) {
        paymentService.deletePaymentById(paymentId)
    }
}