package org.example.repository

import org.example.model.entity.PhoneEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhoneRepository : JpaRepository<PhoneEntity, Long>