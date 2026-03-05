package com.aguardientes.azarcafetero.lobby.domain.model;

import java.util.UUID;

public class Floor {

    private final UUID floorId;
    private final String name;
    private final String icon;
    private final String route;

    public Floor(UUID floorId, String name, String icon, String route) {
        this.floorId = floorId;
        this.name = name;
        this.icon = icon;
        this.route = route;
    }

    public static Floor create(UUID floorId, String name, String icon, String route) {
        return new Floor(floorId, name, icon, route);
    }

    public UUID getFloorId() {
        return floorId;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getRoute() {
        return route;
    }
}
