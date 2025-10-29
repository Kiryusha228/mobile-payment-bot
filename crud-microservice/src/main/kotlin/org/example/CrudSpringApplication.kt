package org.example

import config.KafkaConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(KafkaConfig::class)
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = ["org.example"])
class CrudSpringApplication

fun main(args: Array<String>) {
    runApplication<CrudSpringApplication>(*args)
}