package com.pucetec.events.entities

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

enum class ReservationStatus {
    ACTIVE,
    CANCELLED
}

@Entity
@Table(name = "reservations")
class Reservation(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "attendee_id")
    var attendee: Attendee = Attendee(),

    @ManyToOne
    @JoinColumn(name = "event_id")
    var event: Event = Event(),

    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = ReservationStatus.ACTIVE,

    var createdAt: LocalDateTime = LocalDateTime.now()
)
