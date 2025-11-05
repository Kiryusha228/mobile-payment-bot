package org.example.service

import org.example.mapper.PhoneMapper
import model.dto.ChangeMainPhoneDto
import model.dto.CreatePhoneDto
import model.dto.PhoneDto
import org.example.model.entity.PhoneEntity
import org.example.repository.PhoneRepository
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhoneService(
    val phoneRepository: PhoneRepository,
    val userRepository: UserRepository,
    val phoneMapper: PhoneMapper
) {
    @Transactional
    fun createPhone(createPhoneDto: CreatePhoneDto): PhoneEntity =
        phoneMapper.toEntity(createPhoneDto).apply {
            user = userRepository.findById(createPhoneDto.userId).get()
        }.let {
            phoneRepository.save(it)
        }

    @Transactional
    fun changeMainPhone(changeMainPhoneDto: ChangeMainPhoneDto) {
        val phoneOptional = phoneRepository.findFirstByUserIdAndIsMainTrue(
            userId = userRepository.findByChatId(changeMainPhoneDto.chatId).id
        )

        if (phoneOptional.isPresent) {
            phoneOptional.get().apply {
                isMain = false
            }.let {
                phoneRepository.save(it)
            }
        }

        phoneRepository.findById(changeMainPhoneDto.phoneId).get().apply {
            isMain = true
        }.let {
            phoneRepository.save(it)
        }
    }

    fun getMainPhone(chatId: Long): PhoneDto =
        phoneRepository.findFirstByUserIdAndIsMainTrue(
            userRepository.findByChatId(chatId).id
        ).let {
            phoneMapper.toDto(it.get())
        }


    fun getPhonesByChatId(chatId: Long): List<PhoneDto> =
        phoneRepository.findAllByUser_Id(
            userRepository.findByChatId(chatId).id
        ).let {
            phoneMapper.toDtoList(it)
        }


    fun getPhoneById(phoneId: Long): PhoneEntity =
        phoneRepository.findById(phoneId).get()


    @Transactional
    fun deletePhoneById(phoneId: Long) =
        phoneRepository.deleteById(phoneId)

}