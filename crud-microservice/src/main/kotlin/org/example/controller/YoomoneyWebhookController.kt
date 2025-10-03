package org.example.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/webhook/yoomoney")
class YoomoneyWebhookController {

    @PostMapping
    fun handleWebhook(@RequestParam params: Map<String, String>): ResponseEntity<String> {
        println(params)

        val status = params["notification_type"] ?: "unknown"
        val amount = params["amount"]
        val operationId = params["operation_id"]
        val label = params["label"]
        val sha1Hash = params["sha1_hash"]

        //todo сделать валидацию

        println("Status: $status, Amount: $amount, OperationId: $operationId, Label: $label")

        return ResponseEntity("OK", HttpStatus.OK)
    }
}