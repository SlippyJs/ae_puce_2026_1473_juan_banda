package com.pucetec.events.mappers

import com.pucetec.events.dto.ReservationResponse
import com.pucetec.events.entities.Reservation

fun Reservation.toResponse() = ReservationResponse(
    id = this.id,
    attendee = this.attendee.toResponse(),
    event = this.event.toResponse(),
    status = this.status.name,
    createdAt = this.createdAt
)
