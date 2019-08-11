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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class StartPadListeners implements Listener {

    public static List<Player> delay = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (StartPad.isPads(e.getPlayer().getLocation())) {
            if (!delay.contains(e.getPlayer())) {
                FreeElytra.shootPlayer(e.getPlayer());
                delay.add(e.getPlayer());
                Bukkit.getScheduler().runTaskLaterAsynchronously(FreeElytra.plugin, () -> delay.remove(e.getPlayer()), 10);
            }
        }
    }


}
