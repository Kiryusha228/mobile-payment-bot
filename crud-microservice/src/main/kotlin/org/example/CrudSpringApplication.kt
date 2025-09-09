package org.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["org.example"])
class CrudSpringApplication

fun main(args: Array<String>) {
    runApplication<CrudSpringApplication>(*args)
}