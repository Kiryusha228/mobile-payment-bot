package org.example.service

import org.example.mapper.PhoneMapper
import org.example.model.dto.ChangeMainPhoneDto
import org.example.model.dto.CreatePhoneDto
import org.example.model.dto.PhoneDto
import org.example.model.entity.PhoneEntity
import org.example.repository.PhoneRepository
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhoneService (
    val phoneRepository: PhoneRepository,
    val userRepository: UserRepository,
    val phoneMapper: PhoneMapper
) {
    @Transactional
    fun createPhone(createPhoneDto: CreatePhoneDto) : PhoneEntity {
        val phoneEntity = phoneMapper.toEntity(createPhoneDto)
        phoneEntity.user = userRepository.findById(createPhoneDto.userId).get()
        return phoneRepository.save(phoneEntity)
    }

    @Transactional
    fun changeMainPhone(changeMainPhoneDto: ChangeMainPhoneDto) {
        val userId = userRepository.findByChatId(changeMainPhoneDto.chatId).id
        val phoneOptional = phoneRepository.findFirstByUserIdAndIsMainTrue(userId)
        if (phoneOptional.isPresent) {
            val phone = phoneOptional.get()
            phone.isMain = false
            phoneRepository.save(phone)
        }

        val phone = phoneRepository.findById(changeMainPhoneDto.phoneId).get()
        phone.isMain = true
        phoneRepository.save(phone)
    }

    fun getMainPhone(chatId: Long) : PhoneDto {
        val userId = userRepository.findByChatId(chatId).id
        val phone = phoneRepository.findFirstByUserIdAndIsMainTrue(userId).get()
        return phoneMapper.toDto(phone)
    }

    fun getPhonesByChatId(chatId: Long): List<PhoneDto> {
        val userId = userRepository.findByChatId(chatId).id
        val phoneEntityList = phoneRepository.findAllByUser_Id(userId)
        return phoneMapper.toDtoList(phoneEntityList)
    }

    fun getPhoneById(phoneId: Long): PhoneEntity {
        return phoneRepository.findById(phoneId).get()
    }

    @Transactional
    fun deletePhoneById(phoneId: Long) {
        phoneRepository.deleteById(phoneId)
    }
}