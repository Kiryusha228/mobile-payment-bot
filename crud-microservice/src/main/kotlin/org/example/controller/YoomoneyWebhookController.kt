package org.example.controller

import org.example.model.dto.CreatePaymentDto
import org.example.properties.YoomoneyProperties
import org.example.service.PaymentService
import org.example.service.WebhookHandlerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/webhook/yoomoney")
class YoomoneyWebhookController(
    val paymentService: PaymentService,
    val webhookHandlerService: WebhookHandlerService,
    val yoomoneyProperties: YoomoneyProperties
) {
    @PostMapping
    fun handleWebhook(@RequestParam params: Map<String, String>): ResponseEntity<String> {
        println(params)
        val response = webhookHandlerService.parseYoomoneyWebhookResponse(params)

        println(yoomoneyProperties.webhookSecret)
        if (response.sha1Hash != yoomoneyProperties.webhookSecret){
            return ResponseEntity("ERROR", HttpStatus.LOCKED)
        }

        paymentService.createPayment(CreatePaymentDto(
            response.userId, response.phoneId, response.amount, response.operationId
        ))

        return ResponseEntity("OK", HttpStatus.OK)
    }
}