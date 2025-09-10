package org.example.service

import org.example.mapper.PhoneMapper
import org.example.model.dto.CreatePhoneDto
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

    fun getPhoneById(phoneId: Long): PhoneEntity {
        return phoneRepository.findById(phoneId).get()
    }

    @Transactional
    fun deletePhoneById(phoneId: Long) {
        phoneRepository.deleteById(phoneId)
    }
}