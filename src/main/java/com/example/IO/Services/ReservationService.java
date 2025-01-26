package com.example.IO.Services;

import com.example.IO.Models.Reservation;
import com.example.IO.Models.Room;
import com.example.IO.Models.Client;
import com.example.IO.Repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ClientService clientService;

    public Reservation createAndAssignReservation(Reservation reservation) {
        Room availableRoom = roomService.findAvailableRoom(
                        reservation.getRoomType(), reservation.getGuestsCount())
                .orElseThrow(() -> new IllegalArgumentException("No available rooms match the criteria."));

        reservation.setRoom(availableRoom);
        reservation.setStatus("Pending");

        Client savedClient = clientService.saveClient(reservation.getClient());
        reservation.setClient(savedClient);

        Reservation savedReservation = reservationRepository.save(reservation);

        roomService.updateRoomStatus(availableRoom.getId(), "Occupied");

        return savedReservation;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public String getRoomPrice(String roomType, int guestCount) {
        return roomService.findCheapestRoomPrice(roomType, guestCount)
                .map(BigDecimal::toString)
                .orElse("N/A");
    }
}
