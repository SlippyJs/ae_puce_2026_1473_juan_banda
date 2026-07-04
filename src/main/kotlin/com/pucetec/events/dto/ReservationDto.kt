package com.pucetec.events.dto

import java.time.LocalDateTime

data class ReservationRequest(
    val attendeeId: Long,
    val eventId: Long
)

data class ReservationResponse(
    val id: Long,
    val attendee: AttendeeResponse,
    val event: EventResponse,
    val status: String,
    val createdAt: LocalDateTime
)
