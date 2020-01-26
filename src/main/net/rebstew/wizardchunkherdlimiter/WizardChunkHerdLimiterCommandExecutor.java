package net.rebstew.wizardchunkherdlimiter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class WizardChunkHerdLimiterCommandExecutor implements CommandExecutor {

    private WizardChunkHerdLimiter plugin;

    WizardChunkHerdLimiterCommandExecutor(WizardChunkHerdLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if(args.length < 1) {
            return false;
        }

        if(!"list".equalsIgnoreCase(args[0]) && !"set".equalsIgnoreCase(args[0])){
            return false;
        }

        String worldName = args[1];
        Map<String, Integer> setupWorldConfig = plugin.getWorldConfigsMap();

        //case list
        if("list".equalsIgnoreCase(args[0])){
            commandSender.sendMessage("Set up chunk limit worlds " + setupWorldConfig);
            return true;
        } else if("set".equalsIgnoreCase(args[0])){
            //case limit in a world
            if(args.length < 3) return false;

            Integer limit;

            try {
                limit = Integer.valueOf(args[2]);
            } catch (NumberFormatException e){
                commandSender.sendMessage(ChatColor.RED + "Limit must be an integer! (e.g. 10)");
                return false;
            }

            plugin.limitAnimalsInWorld(worldName, limit);
            setupWorldConfig = plugin.getWorldConfigsMap();

            commandSender.sendMessage("World " + worldName
                    + " limit of animals set to " + limit + " with success!");
            commandSender.sendMessage("Current worlds already setup: " + setupWorldConfig);
            return true;
        }

        return true;
    }


}
