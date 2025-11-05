package org.example.controller


import model.dto.ChangeMainPhoneDto
import model.dto.CreatePhoneDto
import model.dto.PhoneDto
import org.example.model.entity.PhoneEntity
import org.example.service.PhoneService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/phone")
class PhoneController(
    private val phoneService: PhoneService
) {
    @PostMapping("/create")
    fun createPhone(@RequestBody createPhoneDto: CreatePhoneDto): PhoneEntity =
        phoneService.createPhone(createPhoneDto)


    @PatchMapping("/main/change")
    fun changeMainPhone(@RequestBody changeMainPhoneDto: ChangeMainPhoneDto) =
        phoneService.changeMainPhone(changeMainPhoneDto)


    @GetMapping("/get/main/{chatId}")
    fun getMainPhone(@PathVariable chatId: Long): PhoneDto =
        phoneService.getMainPhone(chatId)


    @GetMapping("/get/phones/{chatId}")
    fun getPhonesByChatId(@PathVariable chatId: Long): List<PhoneDto> =
        phoneService.getPhonesByChatId(chatId)


    @GetMapping("/get/{phoneId}")
    fun getPhoneById(@PathVariable phoneId: Long): PhoneEntity =
        phoneService.getPhoneById(phoneId)


    @DeleteMapping("/delete/{phoneId}")
    fun deleteUserById(@PathVariable phoneId: Long) =
        phoneService.deletePhoneById(phoneId)

}