package com.example.IO.Controllers;

import com.example.IO.Models.Reservation;
import com.example.IO.Services.ReservationService;
import com.example.IO.Services.ClientService;
import com.example.IO.Services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/")
    public String showReservationForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        return "reservation-form";
    }

    @PostMapping("/create")
    public String createReservation(@ModelAttribute Reservation reservation, Model model) {
        try {
            model.addAttribute("reservation", reservationService.createAndAssignReservation(reservation));
            return "reservation-confirmation";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "reservation-form";
        }
    }

    @GetMapping("/room-price")
    @ResponseBody
    public String getRoomPrice(@RequestParam String roomType, @RequestParam int guestCount) {
        return reservationService.getRoomPrice(roomType, guestCount);
    }

    @GetMapping("/list")
    public String viewAllReservations(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        return "reservations";
    }

    @GetMapping("/delete")
    public String deleteReservation(@RequestParam Long id) {
        reservationService.deleteReservation(id);
        return "redirect:/reservations/list";
    }
}
