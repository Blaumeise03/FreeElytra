package de.blaumeise03.freeElytra;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FreeElytra extends JavaPlugin {


    public static Plugin plugin;

    private static List<de.blaumeise03.freeElytra.Command> commands = new ArrayList<>();
    private static FileConfiguration configuration;
    private static File confF;

    @Override
    public void onEnable() {
        getLogger().info("FreeElytra by Blaumeise03");
        getLogger().info("Enabling...");
        plugin = this;
        getLogger().info("Loading Configs...");
        createConfigs();

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");

        getLogger().info("Disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
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

}
