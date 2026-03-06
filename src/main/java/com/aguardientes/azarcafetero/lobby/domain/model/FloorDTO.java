package com.aguardientes.azarcafetero.lobby.domain.model;

public class FloorDTO {

    private final String name;
    private final String icon;
    private final String route;

    public FloorDTO(String name, String icon, String route) {
        this.name = name;
        this.icon = icon;
        this.route = route;
    }

    public static FloorDTO from(Floor floor) {
        return new FloorDTO(
            floor.getName(),
            floor.getIcon(),
            floor.getRoute()
        );
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
