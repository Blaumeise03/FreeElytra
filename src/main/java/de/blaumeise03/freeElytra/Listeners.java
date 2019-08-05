package de.blaumeise03.freeElytra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Listeners implements Listener {

    @EventHandler
    public void onMove(final PlayerMoveEvent e) {
        if (FreeElytra.hasPlayerElytra(e.getPlayer()))
            if (e.getPlayer().isOnGround()) {
                //Give me back my Elytra
                e.getPlayer().getInventory().setChestplate(FreeElytra.getChestplate(e.getPlayer()));
                Bukkit.getScheduler().runTaskLater(FreeElytra.plugin, new Runnable() {
                    @Override
                    public void run() {
                        FreeElytra.removePlayer(e.getPlayer());
                    }
                }, 5);

            }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (FreeElytra.hasPlayerElytra((Player) e.getWhoClicked())) {
            e.setCancelled(true);
            e.getWhoClicked().getOpenInventory().close();
            e.getWhoClicked().sendMessage("§4Das darfst du nicht!");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (FreeElytra.hasPlayerElytra((Player) e.getEntity())) {
                e.setCancelled(true); //Please don't die, I would loose my Elytra!
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (FreeElytra.hasPlayerElytra(e.getPlayer())) {
            e.getPlayer().getInventory().setChestplate(FreeElytra.getChestplate(e.getPlayer()));
            FreeElytra.removePlayer(e.getPlayer());
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 5));
        }
    }
}
