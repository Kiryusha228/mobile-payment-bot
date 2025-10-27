package org.example.kafka.producer

import model.dto.MessagePaymentDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class PaymentProducer(
    val kafkaTemplate: KafkaTemplate<String, MessagePaymentDto>
) {
    fun sendPayment(messagePaymentDto: MessagePaymentDto) =
        kafkaTemplate.send("payments", messagePaymentDto)
}