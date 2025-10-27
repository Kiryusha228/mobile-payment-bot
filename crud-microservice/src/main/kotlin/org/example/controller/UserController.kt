package org.example.controller

import model.dto.CreateUserDto
import org.example.model.entity.UserEntity
import org.example.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController (
    val userService: UserService
) {
    @PostMapping("/create")
    fun createUser(@RequestBody createUserDto: CreateUserDto) : UserEntity {
        return userService.createUser(createUserDto)
    }

    @GetMapping("/get/{userId}")
    fun getUserById(@PathVariable userId: Long): UserEntity {
        return userService.getUserById(userId)
    }

    @GetMapping("/get/id/{chatId}")
    fun getUserIdByChatId(@PathVariable chatId: Long): Long =
        userService.getUserIdByChatId(chatId)

    @DeleteMapping("/delete/{userId}")
    fun deleteUserById(@PathVariable userId: Long) {
        userService.deleteUserById(userId)
    }
}