package org.example.controller

import org.example.model.dto.ChangeMainPhoneDto
import org.example.model.dto.CreatePhoneDto
import org.example.model.dto.PhoneDto
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

    @PatchMapping("/change")
    fun changeMainPhone(@RequestBody changeMainPhoneDto: ChangeMainPhoneDto) {
        return phoneService.changeMainPhone(changeMainPhoneDto)
    }

    @GetMapping("/get/main/{chatId}")
    fun getMainPhone(@PathVariable chatId: Long) : PhoneDto {
        return phoneService.getMainPhone(chatId)
    }

    @GetMapping("/get/phones/{chatId}")
    fun getPhonesByChatId(@PathVariable chatId: Long) : List<PhoneDto> {
        return phoneService.getPhonesByChatId(chatId)
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