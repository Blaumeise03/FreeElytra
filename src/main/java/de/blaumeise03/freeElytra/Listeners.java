
/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.freeElytra;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Listeners implements Listener {
    private static Map<Player, Long> lastChecked = new HashMap<>();

    public static void checkPlayer(Player p, boolean force) {
        if (!FreeElytra.isPlayerChecked(p) || force) {
            lastChecked.put(p, System.currentTimeMillis());
            boolean clean = true;
            if (p.getInventory().contains(Material.ELYTRA)) {
                for (ItemStack stack : p.getInventory()) {
                    if (stack != null)
                        if (stack.getType() == Material.ELYTRA) {
                            try {
                                if (stack.getItemMeta().getDisplayName().equalsIgnoreCase("§4Leih-Elytren")) {
                                    if (stack.getItemMeta().getLore().get(0).equalsIgnoreCase("§6Wird nach dem Flug automagisch zurückgegeben!")) {
                                        p.getInventory().remove(stack);
                                        clean = false;
                                    }
                                }
                            } catch (NullPointerException ignored) {
                            }
                        }
                }
            }

            for (ItemStack stack : p.getInventory().getArmorContents()) {
                if (stack != null)
                    if (stack.getType() == Material.ELYTRA) {
                        try {
                            if (stack.getItemMeta().getDisplayName().equalsIgnoreCase("§4Leih-Elytren")) {
                                if (stack.getItemMeta().getLore().get(0).equalsIgnoreCase("§6Wird nach dem Flug automagisch zurückgegeben!")) {
                                    p.getInventory().remove(stack);
                                    clean = false;
                                }
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
            }

            if (clean) FreeElytra.addCheckedPlayer(p);

        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if (FreeElytra.hasPlayerElytra(e.getPlayer())) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (e.getItem() != null) {
                    if (e.getItem().getType() == Material.FIREWORK_ROCKET) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (!FreeElytra.hasPlayerElytra((Player) e.getWhoClicked())) {
            Player p = (Player) e.getWhoClicked();
            if (!lastChecked.containsKey(p))
                checkPlayer(p, true);
            if (lastChecked.get(p) > 2000)
                checkPlayer(p, true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (FreeElytra.hasPlayerDamageProtection((Player) e.getEntity())) {
                if (!(e.getCause() == EntityDamageEvent.DamageCause.VOID))
                    e.setCancelled(true); //Please don't die, I would loose my Elytra!
            }
        }
    }

    /*EventHandler
    public void onMove(final PlayerMoveEvent e) {
        if (FreeElytra.hasPlayerElytra(e.getPlayer()) && !StartPadListeners.delay.contains(e.getPlayer()))
            if (e.getPlayer().isOnGround()) {
                //Give me back my Elytra
                e.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
                FreeElytra.removePlayer(e.getPlayer());
                FreeElytra.removePlayerDamage(e.getPlayer());

            }
        checkPlayer(e.getPlayer(), false);
    }*/

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (FreeElytra.hasPlayerElytra(e.getPlayer())) {
            e.getPlayer().getInventory().setChestplate(FreeElytra.getChestplate(e.getPlayer()));
            FreeElytra.removePlayer(e.getPlayer());
            FreeElytra.removeCheckedPlayer(e.getPlayer());
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 5));

        }
        checkPlayer(e.getPlayer(), false);
    }
}
