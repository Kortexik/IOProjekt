package com.example.IO.Services;

import com.example.IO.Models.Reservation;
import com.example.IO.Models.Room;
import com.example.IO.Repositories.ReservationRepository;
import com.example.IO.Repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Reservation createReservation(Reservation reservation) {
        Optional<Room> room = roomRepository.findById(reservation.getRoom().getId());
        if (room.isPresent() && reservation.getGuestsCount() <= room.get().getCapacity()) {
            return reservationRepository.save(reservation);
        } else {
            throw new IllegalArgumentException("Room is not available or exceeds capacity");
        }
    }
}
