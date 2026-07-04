package com.pucetec.events.controllers

import com.pucetec.events.dto.ReservationRequest
import com.pucetec.events.dto.ReservationResponse
import com.pucetec.events.services.ReservationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reservations")
class ReservationController(
    private val reservationService: ReservationService
) {

    @GetMapping
    fun getAllReservations(): List<ReservationResponse> {
        return reservationService.getAllReservations()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReservation(@RequestBody request: ReservationRequest): ReservationResponse {
        return reservationService.createReservation(request)
    }

    @PutMapping("/{id}/cancel")
    fun cancelReservation(@PathVariable id: Long): ReservationResponse {
        return reservationService.cancelReservation(id)
    }
}
