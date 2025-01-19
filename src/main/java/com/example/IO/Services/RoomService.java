package com.example.IO.Services;

import com.example.IO.Models.Room;
import com.example.IO.Repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public void saveRoom(Room room) {
        roomRepository.save(room);
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }
}