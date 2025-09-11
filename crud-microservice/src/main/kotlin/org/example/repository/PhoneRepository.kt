package org.example.repository

import org.example.model.entity.PhoneEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PhoneRepository : JpaRepository<PhoneEntity, Long> {
    fun findFirstByUserIdAndIsMainTrue(userId: Long?): Optional<PhoneEntity>
    fun findAllByUser_Id(userId: Long?): List<PhoneEntity>
}
