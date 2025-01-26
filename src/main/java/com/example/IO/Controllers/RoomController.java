package com.example.IO.Controllers;

import com.example.IO.Models.Room;
import com.example.IO.Services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/list")
    public String getRooms(Model model) {
        model.addAttribute("rooms", roomService.getSortedRoomsById());
        return "room-list";
    }

    @GetMapping("/create")
    public String showCreateRoomForm(Model model) {
        model.addAttribute("room", roomService.createDefaultRoom());
        return "room-form";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute Room room, Model model) {
        if (roomService.trySaveRoom(room, model)) {
            return "redirect:/rooms/list";
        }
        return "room-form";
    }

    @PostMapping("/delete")
    public String deleteRoom(@RequestParam Long roomId, Model model) {
        if (roomService.tryDeleteRoom(roomId, model)) {
            return "redirect:/rooms/list";
        }
        return "room-list";
    }
}
