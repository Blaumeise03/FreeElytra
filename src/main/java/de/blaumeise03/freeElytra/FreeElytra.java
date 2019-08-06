

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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FreeElytra extends JavaPlugin {


    public static Plugin plugin;

    private static List<de.blaumeise03.freeElytra.Command> commands = new ArrayList<>();

    private static Map<Player, ItemStack> chestplates = new HashMap<>(); //We won't the Players to loose their Stuff :c

    private static FileConfiguration configuration;
    private static File confF;

    public static boolean hasPlayerElytra(Player p) {
        return chestplates.containsKey(p);
    }

    public static ItemStack getChestplate(Player p) {
        return chestplates.get(p);
    }

    public static void removePlayer(Player p) {
        chestplates.remove(p);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createConfigs() {
        confF = new File(getDataFolder(), "config.yml");
        if (!confF.exists()) {
            confF.getParentFile().mkdirs();
            try {
                confF.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveResource("config.yml", false);
        }
        configuration = new YamlConfiguration();
        try {
            configuration.load(confF);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    private void saveConfigs() {
        try {
            configuration.save(confF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadConfigs() {
        createConfigs();
    }

    @Override
    public void onEnable() {
        getLogger().info("FreeElytra by Blaumeise03");
        getLogger().info("Enabling...");
        plugin = this;
        getLogger().info("Loading Configs...");
        createConfigs();

        getLogger().info("Setting up Events");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Listeners(), this);

        getLogger().info("Setting up Commands...");
        commands.add(new de.blaumeise03.freeElytra.Command("elytra", "Leiht dir eine Elytra", new Permission("freeElytra.elytra")) {
            @Override
            public void onCommand(String[] args, CommandSender sender) {
                if (sender instanceof Player) {
                    final Player player = (Player) sender;
                    if (chestplates.containsKey(player)) {
                        player.sendMessage("§4Du hast bereits eine!");
                        return;
                    }
                    chestplates.put(player, player.getInventory().getChestplate());
                    ItemStack elytra = new ItemStack(Material.ELYTRA);
                    elytra.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
                    ItemMeta meta = elytra.getItemMeta();
                    assert meta != null;
                    meta.setLore(Arrays.asList("§4Leih Elytra", "§6Wird nach dem Flug automatisch abgegeben!"));
                    elytra.setItemMeta(meta);
                    player.getInventory().setChestplate(elytra);
                    getLogger().info(player.getName() + " got an Elytra!");
                    Bukkit.getScheduler().runTaskLater(FreeElytra.plugin, () -> player.setGliding(true), 2);
                    player.sendMessage("§aViel Spaß!");
                    player.setVelocity(new Vector(0, 10, 0)); //Is this too much? Who cares!
                } else {
                    sender.sendMessage("You must be a Player!");
                }
            }
        });

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");
        if (chestplates.size() != 0) {
            getLogger().warning("==================STARTING LOG==================");
            getLogger().warning("WARNING! Some Players have an Elytra!");
            getLogger().warning("The Players might have got their Chestplate back.");
            getLogger().warning("SAVING TO LOG...");
            File file = new File(("elytraLog" + Bukkit.getWorlds().get(0).getTime() + ".txt"));
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileWriter fileWriter = new FileWriter(file);
                for (Player p : chestplates.keySet()) {
                    boolean restore = false;
                    try {
                        p.getInventory().setChestplate(chestplates.get(p));
                        restore = true;
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 5));
                    } catch (Exception ignored) {
                    }
                    ItemStack c = chestplates.get(p);
                    fileWriter.write("=====\nPlayer: " + p.getName() + "\nUUID: " + p.getUniqueId());
                    fileWriter.write("\nRestore (this might not be correct): " + restore);
                    fileWriter.write("\nChestplate: \n" + c.getType().name());
                    fileWriter.write("\nEnchantments:\n");
                    for (Enchantment e : c.getEnchantments().keySet()) {
                        fileWriter.write(e.toString() + ": " + c.getEnchantments().get(e) + "\n");
                    }
                }
                fileWriter.close();
                getLogger().warning("SAVED TO LOG \"" + file.getAbsolutePath() + "\"");
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Player p : chestplates.keySet()) {

                getLogger().warning("");
                ItemStack c = chestplates.get(p);
                getLogger().warning("Player: " + p.getName());
                getLogger().warning("UUID: " + p.getUniqueId());
                getLogger().warning("We try to restore the Item. For a information look into the log wich will be generated, but it might not be accurate.");
                getLogger().warning("Chestplate:");
                getLogger().warning(c.getType().name());
                getLogger().warning("");
                getLogger().warning("Enchantments:");
                for (Enchantment e : c.getEnchantments().keySet()) {
                    getLogger().warning(e.toString() + ": " + c.getEnchantments().get(e));
                }
            }
            getLogger().warning("==================LOG COMPLETE==================");
        }
        getLogger().info("Disabled!");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (de.blaumeise03.freeElytra.Command c : commands) {
            c.run(sender, label, args);
        }
        return true;
    }
}
