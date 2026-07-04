package com.pucetec.events.repositories

import com.pucetec.events.entities.Attendee
import org.springframework.data.jpa.repository.JpaRepository

interface AttendeeRepository : JpaRepository<Attendee, Long>
