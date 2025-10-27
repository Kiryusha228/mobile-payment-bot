package org.example.service

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

    fun parseYoomoneyWebhookResponse(params: Map<String, String>) : YoomoneyWebhookResponse =
        params.let {
            // todo: enum
            val (phone, user) = it["label"].orEmpty().parsePhoneAndUser()
            YoomoneyWebhookResponse(
                status = it["notification_type"] ?: "unknown",
                amount = params["amount"]?.toDouble() ?: 0.0,
                operationId = it["operation_id"].orEmpty(),
                dateTime = it["datetime"].orEmpty().let { it1 -> LocalDateTime.parse(it1, DateTimeFormatter.ISO_DATE_TIME) },
                sha1Hash = it["sha1_hash"].orEmpty(),
                phoneId = phone,
                userId = user
            )
        }

    private fun String.parsePhoneAndUser(): Pair<Long, Long> {
        val parts = split("!")
        val phone = parts.getOrNull(0)?.removePrefix("phone")?.toLongOrNull() ?: 0L
        val user = parts.getOrNull(1)?.removePrefix("user")?.toLongOrNull() ?: 0L
        return phone to user
    }

    private fun buildVerificationString(params: Map<String, String>): String =
         listOf(
             params["notification_type"],
             params["operation_id"],
             params["amount"],
             params["currency"],
             params["datetime"],
             params["sender"],
             params["codepro"],
             yoomoneyProperties.webhookSecret,
             params["label"]
        ).joinToString("&") { it ?: "" }


    // todo: check existing methods
    fun calculateSHA1Hash(params: Map<String, String>): String {
        val data = buildVerificationString(params)
        val digest = MessageDigest.getInstance("SHA-1")
        val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

//    fun calculateSHA1Hash(data: String): String =
//        MessageDigest.getInstance("SHA-1").let { it ->
//            it.digest(data.toByteArray(Charsets.UTF_8)).let { it1 ->
//                it1.joinToString("") { "%02x".format(it) }
//            }
//        }


}