
/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.freeElytra;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

abstract public class Command {
    private String label;
    private List<String> alias = new ArrayList<>();
    private Permission permission;
    private String help;
    private boolean onlyPlayer = false;

    public Command(String label, String help, Permission permission) {
        this.label = label;
        this.help = help;
        this.permission = permission;
    }

    public Command(String label, String help, Permission permission, List<String> alias) {
        this.label = label;
        this.help = help;
        this.permission = permission;
        this.alias = alias;
    }

    public Command(String label, String help, Permission permission, List<String> alias, boolean onlyPlayer) {
        this.label = label;
        this.help = help;
        this.permission = permission;
        this.alias = alias;
        this.onlyPlayer = onlyPlayer;
    }

    public Command(String label, String help, Permission permission, boolean onlyPlayer) {
        this.label = label;
        this.help = help;
        this.permission = permission;
        this.onlyPlayer = onlyPlayer;
    }

    public abstract void onCommand(String[] args, CommandSender sender);

    public void onNoPermission(CommandSender sender) {
        sender.sendMessage("§4Dazu hast du keine Rechte!");
    }

    public boolean isCommand(final String label) {
        final boolean[] al = {false};
        alias.forEach(s -> al[0] = (s.equalsIgnoreCase(label) || al[0]));
        return (label.equalsIgnoreCase(this.label) || al[0]);
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }

    public void run(CommandSender sender, String label, String[] args) {
        if (isCommand(label)) {
            if (sender instanceof Player) {
                if (hasPermission((Player) sender)) {
                    onCommand(args, sender);
                } else onNoPermission(sender);
            } else if (!onlyPlayer) {
                onCommand(args, sender);
            } else sender.sendMessage("You must be a Player to execute this Command!");
        }
    }

    public String getLabel() {
        return label;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getHelp() {
        return help;
    }

    public void addAlias(String alias) {
        this.alias.add(alias);
    }
}
