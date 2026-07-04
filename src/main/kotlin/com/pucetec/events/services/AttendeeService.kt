package com.pucetec.events.services

import com.pucetec.events.dto.AttendeeRequest
import com.pucetec.events.dto.AttendeeResponse
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.mappers.toEntity
import com.pucetec.events.mappers.toResponse
import com.pucetec.events.repositories.AttendeeRepository
import org.springframework.stereotype.Service

@Service
class AttendeeService(
    private val attendeeRepository: AttendeeRepository
) {

    fun createAttendee(request: AttendeeRequest): AttendeeResponse {
        if (request.name.isBlank() || request.email.isBlank()) {
            throw BlankFieldException("name and email must not be blank")
        }
        val attendee = attendeeRepository.save(request.toEntity())
        return attendee.toResponse()
    }

    fun getAllAttendees(): List<AttendeeResponse> {
        return attendeeRepository.findAll().map { it.toResponse() }
    }
}
