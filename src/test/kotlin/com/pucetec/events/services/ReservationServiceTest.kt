package com.pucetec.events.services

import com.pucetec.events.dto.ReservationRequest
import com.pucetec.events.entities.Attendee
import com.pucetec.events.entities.Event
import com.pucetec.events.entities.Reservation
import com.pucetec.events.entities.ReservationStatus
import com.pucetec.events.exceptions.AttendeeNotFoundException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.ReservationAlreadyCancelledException
import com.pucetec.events.exceptions.ReservationLimitExceededException
import com.pucetec.events.exceptions.ReservationNotFoundException
import com.pucetec.events.exceptions.SoldOutException
import com.pucetec.events.repositories.AttendeeRepository
import com.pucetec.events.repositories.EventRepository
import com.pucetec.events.repositories.ReservationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ReservationServiceTest {

    @Mock
    lateinit var reservationRepository: ReservationRepository

    @Mock
    lateinit var attendeeRepository: AttendeeRepository

    @Mock
    lateinit var eventRepository: EventRepository

    @InjectMocks
    lateinit var reservationService: ReservationService

    @Test
    fun `createReservation happy path decrements availableTickets`() {
        // Arrange
        val attendee = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        val event = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 50)
        val request = ReservationRequest(attendeeId = 1L, eventId = 1L)
        val savedReservation = Reservation(
            id = 1L,
            attendee = attendee,
            event = event,
            status = ReservationStatus.ACTIVE,
            createdAt = LocalDateTime.now()
        )

        whenever(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        whenever(eventRepository.findById(1L)).thenReturn(Optional.of(event))
        whenever(reservationRepository.countByAttendeeIdAndStatus(1L, ReservationStatus.ACTIVE)).thenReturn(2L)
        whenever(eventRepository.save(any())).thenReturn(event)
        whenever(reservationRepository.save(any())).thenReturn(savedReservation)

        // Act
        val response = reservationService.createReservation(request)

        // Assert
        assertEquals("ACTIVE", response.status)
        assertEquals(49, event.availableTickets)
    }

    @Test
    fun `createReservation with non existing attendee throws AttendeeNotFoundException`() {
        // Arrange
        val request = ReservationRequest(attendeeId = 1L, eventId = 1L)
        whenever(attendeeRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(AttendeeNotFoundException::class.java) {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation with non existing event throws EventNotFoundException`() {
        // Arrange
        val attendee = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        val request = ReservationRequest(attendeeId = 1L, eventId = 1L)
        whenever(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        whenever(eventRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(EventNotFoundException::class.java) {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation with sold out event throws SoldOutException`() {
        // Arrange
        val attendee = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        val event = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 0)
        val request = ReservationRequest(attendeeId = 1L, eventId = 1L)
        whenever(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        whenever(eventRepository.findById(1L)).thenReturn(Optional.of(event))

        // Act & Assert
        assertThrows(SoldOutException::class.java) {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation with attendee at reservation limit throws ReservationLimitExceededException`() {
        // Arrange
        val attendee = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        val event = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 50)
        val request = ReservationRequest(attendeeId = 1L, eventId = 1L)
        whenever(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        whenever(eventRepository.findById(1L)).thenReturn(Optional.of(event))
        whenever(reservationRepository.countByAttendeeIdAndStatus(1L, ReservationStatus.ACTIVE)).thenReturn(4L)

        // Act & Assert
        assertThrows(ReservationLimitExceededException::class.java) {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `cancelReservation happy path increments availableTickets`() {
        // Arrange
        val attendee = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        val event = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 49)
        val reservation = Reservation(
            id = 1L,
            attendee = attendee,
            event = event,
            status = ReservationStatus.ACTIVE,
            createdAt = LocalDateTime.now()
        )
        whenever(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation))
        whenever(eventRepository.save(any())).thenReturn(event)
        whenever(reservationRepository.save(any())).thenReturn(reservation)

        // Act
        val response = reservationService.cancelReservation(1L)

        // Assert
        assertEquals("CANCELLED", response.status)
        assertEquals(50, event.availableTickets)
    }

    @Test
    fun `cancelReservation with non existing reservation throws ReservationNotFoundException`() {
        // Arrange
        whenever(reservationRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(ReservationNotFoundException::class.java) {
            reservationService.cancelReservation(1L)
        }
    }

    @Test
    fun `cancelReservation with already cancelled reservation throws ReservationAlreadyCancelledException`() {
        // Arrange
        val attendee = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        val event = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 50)
        val reservation = Reservation(
            id = 1L,
            attendee = attendee,
            event = event,
            status = ReservationStatus.CANCELLED,
            createdAt = LocalDateTime.now()
        )
        whenever(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation))

        // Act & Assert
        assertThrows(ReservationAlreadyCancelledException::class.java) {
            reservationService.cancelReservation(1L)
        }
    }

    @Test
    fun `getAllReservations returns mapped list`() {
        // Arrange
        val attendee = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        val event = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 50)
        val reservation = Reservation(
            id = 1L,
            attendee = attendee,
            event = event,
            status = ReservationStatus.ACTIVE,
            createdAt = LocalDateTime.now()
        )
        whenever(reservationRepository.findAll()).thenReturn(listOf(reservation))

        // Act
        val response = reservationService.getAllReservations()

        // Assert
        assertEquals(1, response.size)
    }
}
