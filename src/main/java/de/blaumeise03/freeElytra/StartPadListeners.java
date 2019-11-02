/*
 * Copyright (c) 2019 Blaumeise03
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
                Listeners.checkPlayer(e.getPlayer(), true);
                FreeElytra.removeCheckedPlayer(e.getPlayer());
                FreeElytra.shootPlayer(e.getPlayer());
                delay.add(e.getPlayer());
                Bukkit.getScheduler().runTaskLaterAsynchronously(FreeElytra.plugin, () -> delay.remove(e.getPlayer()), 10);
            }
        }
    }


}
