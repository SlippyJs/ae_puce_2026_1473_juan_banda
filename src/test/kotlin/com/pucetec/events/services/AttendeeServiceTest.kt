package com.pucetec.events.services

import com.pucetec.events.dto.AttendeeRequest
import com.pucetec.events.entities.Attendee
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.repositories.AttendeeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AttendeeServiceTest {

    @Mock
    lateinit var attendeeRepository: AttendeeRepository

    @InjectMocks
    lateinit var attendeeService: AttendeeService

    @Test
    fun `createAttendee happy path returns saved attendee`() {
        // Arrange
        val request = AttendeeRequest(name = "Ana Perez", email = "ana@mail.com")
        val savedEntity = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        whenever(attendeeRepository.save(any())).thenReturn(savedEntity)

        // Act
        val response = attendeeService.createAttendee(request)

        // Assert
        assertEquals(1L, response.id)
        assertEquals("Ana Perez", response.name)
        assertEquals("ana@mail.com", response.email)
    }

    @Test
    fun `createAttendee with blank name throws BlankFieldException`() {
        // Arrange
        val request = AttendeeRequest(name = "", email = "ana@mail.com")

        // Act & Assert
        assertThrows(BlankFieldException::class.java) {
            attendeeService.createAttendee(request)
        }
    }

    @Test
    fun `createAttendee with blank email throws BlankFieldException`() {
        // Arrange
        val request = AttendeeRequest(name = "Ana Perez", email = "")

        // Act & Assert
        assertThrows(BlankFieldException::class.java) {
            attendeeService.createAttendee(request)
        }
    }

    @Test
    fun `getAllAttendees returns mapped list`() {
        // Arrange
        val entity = Attendee(id = 1L, name = "Ana Perez", email = "ana@mail.com")
        whenever(attendeeRepository.findAll()).thenReturn(listOf(entity))

        // Act
        val response = attendeeService.getAllAttendees()

        // Assert
        assertEquals(1, response.size)
        assertEquals("Ana Perez", response[0].name)
    }
}
