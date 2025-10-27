package org.example.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.example.kafka.producer.PaymentProducer
import model.dto.CreatePaymentDto
import model.dto.MessagePaymentDto
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
    val paymentProducer: PaymentProducer
) {
    private val log = KotlinLogging.logger { }

    @PostMapping
    fun handleWebhook(@RequestParam params: Map<String, String>): ResponseEntity<String> {

        //println(params)

        log.info { params }

        val response = webhookHandlerService.parseYoomoneyWebhookResponse(params)
        if (response.sha1Hash != webhookHandlerService.calculateSHA1Hash(params)){
            return ResponseEntity("ERROR", HttpStatus.LOCKED)
        }

        paymentService.createPayment(
            CreatePaymentDto(
                response.userId,
                response.phoneId,
                response.amount,
                response.operationId
            )
        ).let {
            paymentProducer.sendPayment(MessagePaymentDto(it.user?.chatId, it.id))
        }

        return ResponseEntity("OK", HttpStatus.OK)
    }
}