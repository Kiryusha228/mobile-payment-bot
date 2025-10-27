package org.example.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "yoomoney")
class YoomoneyProperties {
    lateinit var webhookSecret: String
}