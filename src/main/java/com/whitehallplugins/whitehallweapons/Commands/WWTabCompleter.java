package com.whitehallplugins.whitehallweapons.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WWTabCompleter implements TabCompleter {

    private static final List<String> WW_COMMANDS = Arrays.asList(
            "DragonSword", "FlameScythe", "EvokerAxe", "FreezeGun", "BlastInducingBow", "QuickPick");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        if (!(sender instanceof Player)){
            return Collections.emptyList();
        }
        List<String> filteredCommands = new ArrayList<>();

        if (args.length == 1){
            for (String wwcommand : WW_COMMANDS) {
                if (wwcommand.toLowerCase().startsWith((args[args.length - 1]).toLowerCase())) {
                    filteredCommands.add(wwcommand);
                }
            }
            return filteredCommands;
        }

        return Collections.emptyList();
    }

}
