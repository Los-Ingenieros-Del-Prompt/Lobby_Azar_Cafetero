package com.aguardientes.azarcafetero.lobby.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Building {

    private final UUID id;
    private final List<Floor> floors;

    public Building(UUID id) {
        this.id = id;
        this.floors = new ArrayList<>();
    }

    public Building(UUID id, List<Floor> floors) {
        this.id = id;
        this.floors = new ArrayList<>(floors);
    }

    public static Building create(UUID id) {
        return new Building(id);
    }

    public void addFloor(Floor floor) {
        this.floors.add(floor);
    }

    public UUID getId() {
        return id;
    }

    public List<Floor> getFloors() {
        return Collections.unmodifiableList(floors);
    }
}
