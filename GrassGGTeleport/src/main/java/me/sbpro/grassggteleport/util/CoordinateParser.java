package me.sbpro.grassggteleport.util;

import org.bukkit.Location;
import org.bukkit.World;

public final class CoordinateParser {

    private CoordinateParser() {
    }

    public static Location parse(
            Location origin,
            String xInput,
            String yInput,
            String zInput
    ) throws IllegalArgumentException {
        double x = parseCoordinate(xInput, origin.getX());
        double y = parseCoordinate(yInput, origin.getY());
        double z = parseCoordinate(zInput, origin.getZ());

        World world = origin.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("World cannot be null.");
        }

        return new Location(
                world,
                x,
                y,
                z,
                origin.getYaw(),
                origin.getPitch()
        );
    }

    private static double parseCoordinate(
            String input,
            double current
    ) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Invalid coordinate.");
        }

        if (input.equals("~")) {
            return current;
        }

        if (input.startsWith("~")) {
            String offset = input.substring(1);

            if (offset.isEmpty()) {
                return current;
            }

            try {
                return current + Double.parseDouble(offset);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid coordinate.");
            }
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid coordinate.");
        }
    }
}