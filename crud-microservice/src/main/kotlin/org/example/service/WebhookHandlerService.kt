package org.example.service

import org.example.model.dto.YoomoneyWebhookResponse
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class WebhookHandlerService {

    fun parseYoomoneyWebhookResponse(params: Map<String, String>) : YoomoneyWebhookResponse =
        params.let {
            val (phone, user) = it["label"].orEmpty().parsePhoneAndUser()

            YoomoneyWebhookResponse(
                status = it["notification_type"] ?: "unknown",
                amount = it["amount"]?.toDouble() ?: 0.0,
                operationId = it["operation_id"].orEmpty(),
                dateTime = it["datetime"].orEmpty().let { it1 -> LocalDateTime.parse(it1, DateTimeFormatter.ISO_DATE_TIME) },
                sha1Hash = it["sha1_hash"].orEmpty(),
                phoneId = phone,
                userId = user
            )
        }

    private fun String.parsePhoneAndUser(): Pair<Long, Long> {
        val parts = split("#")
        val phone = parts.getOrNull(0)?.removePrefix("phone")?.toLongOrNull() ?: 0L
        val user = parts.getOrNull(1)?.removePrefix("user")?.toLongOrNull() ?: 0L
        return phone to user
    }
}