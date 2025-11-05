package org.example.controller

import model.dto.CreatePaymentDto
import model.dto.PaymentDto
import org.example.model.entity.PaymentEntity
import org.example.service.PaymentService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payment")
class PaymentController (
    private val paymentService: PaymentService
) {
    @PostMapping("/create")
    fun createPayment(@RequestBody createPaymentDto: CreatePaymentDto) : PaymentEntity =
        paymentService.createPayment(createPaymentDto)

    @GetMapping("/get/{paymentId}")
    fun getPhoneById(@PathVariable paymentId: Long): PaymentDto =
        paymentService.getPaymentById(paymentId)


    @DeleteMapping("/delete/{paymentId}")
    fun deleteUserById(@PathVariable paymentId: Long) =
        paymentService.deletePaymentById(paymentId)

}