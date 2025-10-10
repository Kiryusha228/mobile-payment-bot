package org.example.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "yoomoney")
data class YoomoneyProperties (
    val webhookSecret: String
)