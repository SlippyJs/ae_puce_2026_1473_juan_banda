package com.pucetec.events.services

import com.pucetec.events.dto.EventRequest
import com.pucetec.events.entities.Event
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.InvalidCapacityException
import com.pucetec.events.repositories.EventRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class EventServiceTest {

    @Mock
    lateinit var eventRepository: EventRepository

    @InjectMocks
    lateinit var eventService: EventService

    @Test
    fun `createEvent happy path sets availableTickets equal to totalTickets`() {
        // Arrange
        val request = EventRequest(name = "Rock Fest", venue = "Estadio", totalTickets = 100)
        val savedEntity = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 100)
        whenever(eventRepository.save(any())).thenReturn(savedEntity)

        // Act
        val response = eventService.createEvent(request)

        // Assert
        assertEquals(100, response.availableTickets)
        assertEquals(100, response.totalTickets)
    }

    @Test
    fun `createEvent with blank name throws BlankFieldException`() {
        // Arrange
        val request = EventRequest(name = "", venue = "Estadio", totalTickets = 100)

        // Act & Assert
        assertThrows(BlankFieldException::class.java) {
            eventService.createEvent(request)
        }
    }

    @Test
    fun `createEvent with blank venue throws BlankFieldException`() {
        // Arrange
        val request = EventRequest(name = "Rock Fest", venue = "", totalTickets = 100)

        // Act & Assert
        assertThrows(BlankFieldException::class.java) {
            eventService.createEvent(request)
        }
    }

    @Test
    fun `createEvent with totalTickets less than 1 throws InvalidCapacityException`() {
        // Arrange
        val request = EventRequest(name = "Rock Fest", venue = "Estadio", totalTickets = 0)

        // Act & Assert
        assertThrows(InvalidCapacityException::class.java) {
            eventService.createEvent(request)
        }
    }

    @Test
    fun `getAllEvents returns mapped list`() {
        // Arrange
        val entity = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 100)
        whenever(eventRepository.findAll()).thenReturn(listOf(entity))

        // Act
        val response = eventService.getAllEvents()

        // Assert
        assertEquals(1, response.size)
        assertEquals("Rock Fest", response[0].name)
    }

    @Test
    fun `getEventById happy path returns event`() {
        // Arrange
        val entity = Event(id = 1L, name = "Rock Fest", venue = "Estadio", totalTickets = 100, availableTickets = 100)
        whenever(eventRepository.findById(1L)).thenReturn(Optional.of(entity))

        // Act
        val response = eventService.getEventById(1L)

        // Assert
        assertEquals(1L, response.id)
    }

    @Test
    fun `getEventById with non existing id throws EventNotFoundException`() {
        // Arrange
        whenever(eventRepository.findById(99L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(EventNotFoundException::class.java) {
            eventService.getEventById(99L)
        }
    }
}
