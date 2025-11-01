plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "mobile-payment-bot"
include("crud-microservice")
include("tgbot-microservice")
include("core")
