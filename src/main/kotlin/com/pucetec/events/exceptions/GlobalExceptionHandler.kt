package com.pucetec.events.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ExceptionResponse(
    val message: String?,
    val source: String
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BlankFieldException::class)
    fun handleBlankField(ex: BlankFieldException): ResponseEntity<ExceptionResponse> {
        return buildResponse(ex, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidCapacityException::class)
    fun handleInvalidCapacity(ex: InvalidCapacityException): ResponseEntity<ExceptionResponse> {
        return buildResponse(ex, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AttendeeNotFoundException::class)
    fun handleAttendeeNotFound(ex: AttendeeNotFoundException): ResponseEntity<ExceptionResponse> {
        return buildResponse(ex, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(EventNotFoundException::class)
    fun handleEventNotFound(ex: EventNotFoundException): ResponseEntity<ExceptionResponse> {
        return buildResponse(ex, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ReservationNotFoundException::class)
    fun handleReservationNotFound(ex: ReservationNotFoundException): ResponseEntity<ExceptionResponse> {
        return buildResponse(ex, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SoldOutException::class)
    fun handleSoldOut(ex: SoldOutException): ResponseEntity<ExceptionResponse> {
        return buildResponse(ex, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ReservationLimitExceededException::class)
    fun handleReservationLimitExceeded(ex: ReservationLimitExceededException): ResponseEntity<ExceptionResponse> {
        return buildResponse(ex, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ReservationAlreadyCancelledException::class)
    fun handleReservationAlreadyCancelled(ex: ReservationAlreadyCancelledException): ResponseEntity<ExceptionResponse> {
        return buildResponse(ex, HttpStatus.CONFLICT)
    }

    private fun buildResponse(ex: RuntimeException, status: HttpStatus): ResponseEntity<ExceptionResponse> {
        val body = ExceptionResponse(
            message = ex.message,
            source = ex::class.simpleName ?: "Exception"
        )
        return ResponseEntity.status(status).body(body)
    }
}
