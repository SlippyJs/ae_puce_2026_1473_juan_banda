package com.pucetec.events.repositories

import com.pucetec.events.entities.Reservation
import com.pucetec.events.entities.ReservationStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationRepository : JpaRepository<Reservation, Long> {
    fun countByAttendeeIdAndStatus(attendeeId: Long, status: ReservationStatus): Long
}
