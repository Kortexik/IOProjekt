package com.example.IO.Controllers;

import com.example.IO.Models.Reservation;
import com.example.IO.Models.Room;
import com.example.IO.Models.Client;
import com.example.IO.Services.ReservationService;
import com.example.IO.Services.ClientService;
import com.example.IO.Services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.Optional;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/new")
    public String showReservationForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        return "reservation-form";
    }

    @PostMapping("/create")
    public String createReservation(Reservation reservation, Model model) {
        Room availableRoom = roomService.findAll().stream()
                .filter(room -> "Available".equals(room.getStatus())
                        && room.getCapacity() >= reservation.getGuestsCount()
                        && room.getRoomType().equals(reservation.getRoomType()))
                .min(Comparator.comparing(Room::getPricePerNight))
                .orElse(null);

        if (availableRoom == null) {
            model.addAttribute("error", "Brak dostępnych pokoi spełniających wymagania.");
            return "reservation-form";
        }

        reservation.setRoom(availableRoom);
        reservation.setStatus("Pending");

        try {
            Client client = reservation.getClient();
            Client savedClient = clientService.saveClient(client);

            reservation.setClient(savedClient);
            Reservation savedReservation = reservationService.createReservation(reservation);

            availableRoom.setStatus("Occupied");
            roomService.saveRoom(availableRoom);

            model.addAttribute("reservation", savedReservation);
            return "reservation-confirmation";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "reservation-form";
        }
    }

    @GetMapping("/room-price")
    @ResponseBody
    public String getRoomPrice(@RequestParam String roomType, @RequestParam int guestCount) {
        Optional<Room> availableRoom = roomService.findAll().stream()
                .filter(room -> "Available".equals(room.getStatus())
                        && room.getCapacity() >= guestCount
                        && room.getRoomType().equals(roomType))
                .min(Comparator.comparing(Room::getPricePerNight));

        return availableRoom.map(room -> room.getPricePerNight().toString()).orElse("N/A");
    }
}