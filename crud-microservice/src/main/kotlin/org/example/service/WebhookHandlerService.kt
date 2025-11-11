package org.example.service

import enums.WebhookLabelParams
import enums.YoomoneyWebhookResponseParams
import model.dto.YoomoneyWebhookResponse
import org.example.properties.YoomoneyProperties
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class WebhookHandlerService(
    val yoomoneyProperties: YoomoneyProperties
) {

    fun parseYoomoneyWebhookResponse(params: Map<String, String>): YoomoneyWebhookResponse =
        params.let {
            // todo: enum
            val (phone, user) = it[YoomoneyWebhookResponseParams.LABEL.value].orEmpty().parsePhoneAndUser()
            YoomoneyWebhookResponse(
                status = it[YoomoneyWebhookResponseParams.NOTIFICATION_TYPE.value] ?: "unknown",
                amount = params[YoomoneyWebhookResponseParams.AMOUNT.value]?.toDouble() ?: 0.0,
                operationId = it[YoomoneyWebhookResponseParams.OPERATION_ID.value].orEmpty(),
                dateTime = it[YoomoneyWebhookResponseParams.DATETIME.value].orEmpty().let { it1 ->
                    LocalDateTime.parse(it1, DateTimeFormatter.ISO_DATE_TIME)
                },
                sha1Hash = it[YoomoneyWebhookResponseParams.SHA1_HASH.value].orEmpty(),
                phoneId = phone,
                userId = user
            )
        }

    private fun String.parsePhoneAndUser(): Pair<Long, Long> {
        val parts = split(WebhookLabelParams.DELIMITER.value)
        val phone = parts.getOrNull(0)?.removePrefix(WebhookLabelParams.PHONE.value)?.toLongOrNull() ?: 0L
        val user = parts.getOrNull(1)?.removePrefix(WebhookLabelParams.USER.value)?.toLongOrNull() ?: 0L
        return phone to user
    }

    private fun buildVerificationString(params: Map<String, String>): String = buildList {
        add(YoomoneyWebhookResponseParams.LABEL.value)
        add(YoomoneyWebhookResponseParams.OPERATION_ID.value)
        add(YoomoneyWebhookResponseParams.AMOUNT.value)
        add(YoomoneyWebhookResponseParams.CURRENCY.value)
        add(YoomoneyWebhookResponseParams.DATETIME.value)
        add(YoomoneyWebhookResponseParams.SENDER.value)
        add(YoomoneyWebhookResponseParams.CODEPRO.value)
        add(yoomoneyProperties.webhookSecret)
        add(YoomoneyWebhookResponseParams.LABEL.value)
    }.joinToString("&")


    // todo: check existing methods
    fun calculateSHA1Hash(params: Map<String, String>): String {
        val data = buildVerificationString(params)
        val digest = MessageDigest.getInstance("SHA-1")
        val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}