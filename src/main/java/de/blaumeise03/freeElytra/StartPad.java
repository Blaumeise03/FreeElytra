/*
 *     Copyright (C) 2019  Blaumeise03 - bluegame61@gmail.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.blaumeise03.freeElytra;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StartPad {
    public static List<StartPad> startPads;

    private Location loc1;
    private Location loc2;
    private String name;

    public StartPad(Location loc1, Location loc2, String name) {
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.name = name;
    }

    public static void load() {
        startPads = new ArrayList<>();
        FileConfiguration conf = FreeElytra.getConfiguration();

        List<String> pads = conf.getStringList("Pads.All");
        for (String padName : pads) {
            World world = Bukkit.getWorld(UUID.fromString(Objects.requireNonNull(conf.getString("Pads." + padName + ".world"))));

            startPads.add(new StartPad(new Location(
                    world,
                    conf.getInt("Pads." + padName + ".X1"),
                    conf.getInt("Pads." + padName + ".Y1"),
                    conf.getInt("Pads." + padName + ".Z1")
            ), new Location(
                    world,
                    conf.getInt("Pads." + padName + ".X2"),
                    conf.getInt("Pads." + padName + ".Y2"),
                    conf.getInt("Pads." + padName + ".Z2")
            ), padName));

        }
    }

    public static boolean isPads(Location location) {
        for (StartPad pad : startPads) {
            if (pad.isPad(location)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPad(Location location) {
        boolean validX = (loc1.getX() <= location.getX() && loc2.getX() >= location.getX()) || (loc1.getX() >= location.getX() && loc2.getX() <= location.getX());
        boolean validY = (loc1.getY() <= location.getY() && loc2.getY() >= location.getY()) || (loc1.getY() >= location.getY() && loc2.getY() <= location.getY());
        boolean validZ = (loc1.getZ() <= location.getZ() && loc2.getZ() >= location.getZ()) || (loc1.getZ() >= location.getZ() && loc2.getZ() <= location.getZ());
        boolean validWorld = location.getWorld() == loc1.getWorld();

        return validX && validY && validZ && validWorld;
    }

    public static List<StartPad> getStartPads() {
        return startPads;
    }

    public Location getLoc1() {
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public String getName() {
        return name;
    }
}
