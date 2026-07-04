package com.pucetec.events.services

import com.pucetec.events.dto.ReservationRequest
import com.pucetec.events.dto.ReservationResponse
import com.pucetec.events.entities.Reservation
import com.pucetec.events.entities.ReservationStatus
import com.pucetec.events.exceptions.AttendeeNotFoundException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.ReservationAlreadyCancelledException
import com.pucetec.events.exceptions.ReservationLimitExceededException
import com.pucetec.events.exceptions.ReservationNotFoundException
import com.pucetec.events.exceptions.SoldOutException
import com.pucetec.events.mappers.toResponse
import com.pucetec.events.repositories.AttendeeRepository
import com.pucetec.events.repositories.EventRepository
import com.pucetec.events.repositories.ReservationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private const val MAX_ACTIVE_RESERVATIONS = 4

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val attendeeRepository: AttendeeRepository,
    private val eventRepository: EventRepository
) {

    fun createReservation(request: ReservationRequest): ReservationResponse {
        val attendee = attendeeRepository.findById(request.attendeeId)
            .orElseThrow { AttendeeNotFoundException("Attendee with id ${request.attendeeId} not found") }

        val event = eventRepository.findById(request.eventId)
            .orElseThrow { EventNotFoundException("Event with id ${request.eventId} not found") }

        if (event.availableTickets <= 0) {
            throw SoldOutException("Event with id ${event.id} is sold out")
        }

        val activeReservations = reservationRepository.countByAttendeeIdAndStatus(
            attendee.id,
            ReservationStatus.ACTIVE
        )
        if (activeReservations >= MAX_ACTIVE_RESERVATIONS) {
            throw ReservationLimitExceededException(
                "Attendee with id ${attendee.id} has reached the maximum of $MAX_ACTIVE_RESERVATIONS active reservations"
            )
        }

        event.availableTickets -= 1
        eventRepository.save(event)

        val reservation = Reservation(
            attendee = attendee,
            event = event,
            status = ReservationStatus.ACTIVE,
            createdAt = LocalDateTime.now()
        )
        val savedReservation = reservationRepository.save(reservation)
        return savedReservation.toResponse()
    }

    fun cancelReservation(reservationId: Long): ReservationResponse {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { ReservationNotFoundException("Reservation with id $reservationId not found") }

        if (reservation.status == ReservationStatus.CANCELLED) {
            throw ReservationAlreadyCancelledException("Reservation with id $reservationId is already cancelled")
        }

        reservation.status = ReservationStatus.CANCELLED
        val event = reservation.event
        event.availableTickets += 1
        eventRepository.save(event)

        val savedReservation = reservationRepository.save(reservation)
        return savedReservation.toResponse()
    }

    fun getAllReservations(): List<ReservationResponse> {
        return reservationRepository.findAll().map { it.toResponse() }
    }
}
