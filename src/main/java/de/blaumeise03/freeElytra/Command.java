package de.blaumeise03.freeElytra;

import org.bukkit.command.CommandSender;

abstract public class Command {
    private String label;

    private String help;

    public Command(String label, String help) {
        this.label = label;
        this.help = help;
    }

    public abstract void onCommand(String[] args, CommandSender sender);

    public String getLabel() {
        return label;
    }

    public String getHelp() {
        return help;
    }
}
