/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.freeElytra;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
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
    private static List<Player> damageDelay = new ArrayList<>(); //To prevent Player from taking damage directly after loosing the Elytra
    private static List<Player> checkedPlayers = new ArrayList<>(); //Tp prevent Player from stealing the Elytra

    private static FileConfiguration configuration;
    private static File confF;


    public static boolean hasPlayerElytra(Player p) {
        return chestplates.containsKey(p);
    }

    public static boolean hasPlayerDamageProtection(Player p) {
        return damageDelay.contains(p);
    }

    public static ItemStack getChestplate(Player p) {
        return chestplates.get(p);
    }

    public static void removePlayer(Player p) {
        chestplates.remove(p);
    }

    public static void removePlayerDamage(Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                damageDelay.remove(p);
            }
        }, 10);
    }

    public static boolean isPlayerChecked(Player p) {
        return checkedPlayers.contains(p);
    }

    public static void addCheckedPlayer(Player p) {
        checkedPlayers.add(p);
    }

    public static void removeCheckedPlayer(Player p) {
        checkedPlayers.remove(p);
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

    public static FileConfiguration getConfiguration() {
        return configuration;
    }

    public static void shootPlayer(Player player) {
        if (player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().getChestplate() != null) {

                player.getInventory().addItem(player.getInventory().getChestplate());
            }
        } else {
            player.sendMessage("§4Dein Inventar ist voll!");
            return;
        }
        checkedPlayers.remove(player);

        if (!chestplates.containsKey(player)) {
            chestplates.put(player, player.getInventory().getChestplate());
        }
        damageDelay.add(player);
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        //elytra.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        elytra.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        elytra.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        ItemMeta meta = elytra.getItemMeta();
        assert meta != null;
        meta.setLore(Arrays.asList("§6Wird nach dem Flug automagisch zurückgegeben!"));
        meta.setDisplayName("§4Leih-Elytren");
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        elytra.setItemMeta(meta);
        player.getInventory().setChestplate(elytra);
        FreeElytra.plugin.getLogger().info(player.getName() + " got an Elytra!");
        Bukkit.getScheduler().runTaskLater(FreeElytra.plugin, () -> player.setGliding(true), 6);
        player.sendMessage("§aViel Spaß!");
        Vector v = player.getVelocity();
        v.add(new Vector(1.5, 10, 1.5));

        player.setVelocity(new Vector(Math.min(v.getX(), 4), Math.min(v.getY(), 15), Math.min(v.getZ(), 4))); //Is this too much? Who cares!
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (de.blaumeise03.freeElytra.Command c : commands) {
            c.run(sender, label, args);
        }
        return true;
    }

    @Override
    public void onEnable() {
        getLogger().info("FreeElytra by Blaumeise03");
        getLogger().info("Enabling...");
        plugin = this;
        getLogger().info("Loading Configs...");
        createConfigs();
        reloadConfigs();
        getLogger().info("Setting up Events");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Listeners(), this);
        pm.registerEvents(new StartPadListeners(), this);

        getLogger().info("Setting up Commands...");
        commands.add(new de.blaumeise03.freeElytra.Command("elytra", "Leiht dir eine Elytra", new Permission("freeElytra.elytra"), false) {
            @Override
            public void onCommand(String[] args, CommandSender sender) {
                if (args.length > 0) {
                    Player player = Bukkit.getPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage(args[0] + " ist kein Spieler!");
                        return;
                    }
                    shootPlayer(player);
                } else if (sender instanceof Player) {

                    final Player player = (Player) sender;
                    if (chestplates.containsKey(player)) {
                        player.sendMessage("§4Du hast bereits eine!");
                        return;
                    }
                    shootPlayer(player);
                } else {
                    sender.sendMessage("You must be a Player!");
                }
            }
        });

        commands.add(new de.blaumeise03.freeElytra.Command("addPad", "Fügt ein Start-Pad hinzu", new Permission("freeElytra.settings"), true) {
            @Override
            public void onCommand(String[] args, CommandSender sender) {
                Player player = (Player) sender;
                if (args.length < 7) {
                    player.sendMessage("§aBenutzung: §4/addPad <name> <pos1x> <pos1y> <pos1z> <pos2x> <pos2y> <pos2z>");
                } else {
                    try {
                        int x1 = Integer.parseInt(args[1]);
                        int y1 = Integer.parseInt(args[2]);
                        int z1 = Integer.parseInt(args[3]);

                        int x2 = Integer.parseInt(args[4]);
                        int y2 = Integer.parseInt(args[5]);
                        int z2 = Integer.parseInt(args[6]);

                        String name = args[0];

                        configuration.set("Pads." + name.toLowerCase() + ".X1", x1);
                        configuration.set("Pads." + name.toLowerCase() + ".Y1", y1);
                        configuration.set("Pads." + name.toLowerCase() + ".Z1", z1);

                        configuration.set("Pads." + name.toLowerCase() + ".X2", x2);
                        configuration.set("Pads." + name.toLowerCase() + ".Y2", y2);
                        configuration.set("Pads." + name.toLowerCase() + ".Z2", z2);

                        configuration.set("Pads." + name.toLowerCase() + ".world", player.getWorld().getUID().toString());

                        List<String> pads = configuration.getStringList("Pads.All");
                        if (!pads.contains(name.toLowerCase()))
                            pads.add(name.toLowerCase());
                        else sender.sendMessage("§6Pad wird überschrieben.");
                        configuration.set("Pads.All", pads);
                        StartPad.load();
                        sender.sendMessage("§aStart-Pad erzeugt!");
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Bitte gebe Zahlen an!");
                    }
                }
            }
        });

        commands.add(new de.blaumeise03.freeElytra.Command("removePad", "Löscht ein StartPad", new Permission("freeElytra.settings")) {
            @Override
            public void onCommand(String[] args, CommandSender sender) {
                if (args.length >= 1) {
                    List<String> pads = configuration.getStringList("Pads.All");
                    String pad = args[0].toLowerCase();
                    if (pads.contains(pad)) {
                        pads.remove(pad);
                        configuration.set("Pads." + pad, null);
                        configuration.set("Pads.All", pads);
                        sender.sendMessage("§aStartPad gelöscht!");
                        StartPad.load();
                    } else {
                        sender.sendMessage("§4StartPad nicht vorhanden!");
                    }
                } else {
                    sender.sendMessage("§4Bitte gebe ein StartPad an!");
                }
            }
        });

        commands.add(new de.blaumeise03.freeElytra.Command("listPads", "Listet alle Pads auf", new Permission("freeElytra.settings"), false) {
            @Override
            public void onCommand(String[] args, CommandSender sender) {
                for (StartPad pad : StartPad.getStartPads()) {
                    sender.sendMessage(ChatColor.GREEN + "StartPad: " + pad.getName() + ", Eckpunkt1: " + pad.getLoc1().getX() + "|" + pad.getLoc1().getY() + "|" + pad.getLoc1().getZ() + " Eckpunkt2: " + pad.getLoc2().getX() + "|" + pad.getLoc2().getY() + "|" + pad.getLoc2().getZ());
                }
            }
        });

        getLogger().info("Loading Start-Pads...");
        StartPad.load();

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");
        checkedPlayers = new ArrayList<>();
        if (chestplates.size() != 0) {
            getLogger().warning("==================STARTING LOG==================");
            getLogger().warning("WARNING! Some Players have an Elytra!");
            getLogger().warning("The Players might have got their Chestplate back.");
            getLogger().warning("SAVING TO LOG...");
            File file = new File(("elytraLogs/elytraLog-" + Bukkit.getWorlds().get(0).getTime() + ".txt"));
            try {
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
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
                    fileWriter.write("\nChestplate: \n" + (c == null ? "EMPTY" : c.getType().name()));
                    fileWriter.write("\nEnchantments:\n");
                    if (c != null)
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
                getLogger().warning(c == null ? "EMPTY" : c.getType().name());
                getLogger().warning("");
                getLogger().warning("Enchantments:");
                if (c != null)
                    for (Enchantment e : c.getEnchantments().keySet()) {
                        getLogger().warning(e.toString() + ": " + c.getEnchantments().get(e));
                    }
            }
            getLogger().warning("==================LOG COMPLETE==================");
        }
        saveConfigs();
        getLogger().info("Disabled!");
    }
}
