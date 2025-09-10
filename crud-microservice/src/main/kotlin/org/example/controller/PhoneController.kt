package org.example.controller

import org.example.model.dto.CreatePhoneDto
import org.example.model.entity.PhoneEntity
import org.example.service.PhoneService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/phone")
class PhoneController (
    val phoneService: PhoneService
) {
    @PostMapping("/create")
    fun createPhone(@RequestBody createPhoneDto: CreatePhoneDto) : PhoneEntity {
        return phoneService.createPhone(createPhoneDto)
    }

    @GetMapping("/get/{phoneId}")
    fun getPhoneById(@PathVariable phoneId: Long): PhoneEntity {
        return phoneService.getPhoneById(phoneId)
    }

    @DeleteMapping("/delete/{phoneId}")
    fun deleteUserById(@PathVariable phoneId: Long) {
        phoneService.deletePhoneById(phoneId)
    }
}