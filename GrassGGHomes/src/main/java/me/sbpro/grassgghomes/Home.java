package me.sbpro.grassgghomes;

import org.bukkit.Location;

public class Home {

    private final Location location;

    private String name;


    public Home(
            Location location,
            String name
    ) {

        this.location = location;
        this.name = name;
    }


    public Location getLocation() {

        return location;
    }


    public String getName() {

        return name;
    }


    public void setName(String name) {

        this.name = name;
    }

}