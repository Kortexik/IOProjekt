package com.example.IO.Services;

import com.example.IO.Models.Room;
import com.example.IO.Repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public List<Room> getSortedRoomsById() {
        List<Room> rooms = findAll();
        rooms.sort(Comparator.comparing(Room::getId));
        return rooms;
    }

    public Room createDefaultRoom() {
        Room newRoom = new Room();
        newRoom.setStatus("Available");
        return newRoom;
    }

    public boolean trySaveRoom(Room room, Model model) {
        try {
            saveRoom(room);
            return true;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create room: " + e.getMessage());
            return false;
        }
    }

    public boolean tryDeleteRoom(Long roomId, Model model) {
        try {
            deleteRoom(roomId);
            return true;
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Room not found.");
        }
        return false;
    }

    public Optional<Room> findAvailableRoom(String roomType, int guestCount) {
        return findAll().stream()
                .filter(room -> "Available".equals(room.getStatus())
                        && room.getCapacity() >= guestCount
                        && room.getRoomType().equals(roomType))
                .min(Comparator.comparing(Room::getPricePerNight));
    }

    public Optional<BigDecimal> findCheapestRoomPrice(String roomType, int guestCount) {
        return findAll().stream()
                .filter(room -> "Available".equals(room.getStatus())
                        && room.getCapacity() >= guestCount
                        && room.getRoomType().equals(roomType))
                .map(Room::getPricePerNight)
                .min(Comparator.naturalOrder());
    }

    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if ("Occupied".equals(room.getStatus())) {
            throw new IllegalStateException("Cannot delete a room that is currently occupied.");
        }

        roomRepository.deleteById(roomId);
    }

    public void updateRoomStatus(Long roomId, String status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        room.setStatus(status);
        roomRepository.save(room);
    }
}
