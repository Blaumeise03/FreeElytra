
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

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.ListIterator;

public class Listeners implements Listener {

    @EventHandler
    public void onMove(final PlayerMoveEvent e) {
        if (FreeElytra.hasPlayerElytra(e.getPlayer()) && !StartPadListeners.delay.contains(e.getPlayer()))
            if (e.getPlayer().isOnGround()) {
                //Give me back my Elytra
                e.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
                FreeElytra.removePlayer(e.getPlayer());
                FreeElytra.removePlayerDamage(e.getPlayer());

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
            if (FreeElytra.hasPlayerDamageProtection((Player) e.getEntity())) {
                if (!(e.getCause() == EntityDamageEvent.DamageCause.VOID))
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
        if (FreeElytra.isPlayerChecked(e.getPlayer())) {
            Player p = e.getPlayer();
            if (p.getInventory().contains(Material.ELYTRA)) {
                ListIterator<ItemStack> iterator = p.getInventory().iterator();
                while (iterator.hasNext()) {
                    ItemStack stack = iterator.next();
                    if (stack.getType() == Material.ELYTRA) {
                        if (stack.containsEnchantment(Enchantment.DURABILITY)) {
                            if (stack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) == 10) {
                                try {
                                    if (stack.getItemMeta().getLore().equals(Arrays.asList("§4Leih Elytra", "§6Wird nach dem Flug automatisch abgegeben!"))) {
                                        p.getInventory().remove(stack);
                                        break;
                                    }
                                } catch (NullPointerException ignored) {
                                }
                            }
                        }
                    }
                }
            } else {
                FreeElytra.addCheckedPlayer(p);
            }
        }
    }
}
