package com.whitehallplugins.whitehallweapons.Commands;

import com.whitehallplugins.whitehallweapons.Items.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WWCommands implements CommandExecutor {

    private static final String WW = "[§6WW§r] ";
    private static final TextComponent NoPermission = Component.text("§4You don't have permission to access this resource.");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Component.text("Only players can use this command."));
            return true;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("weapons")){
            if (player.hasPermission("whitehallweapons.admin")) {
                if (args.length == 0){
                    player.sendMessage(Component.text(WW + "To access one of the custom items, type /weapons <weapon>"));
                    return true;
                }
                else {
                    switch (args[0].toLowerCase()) {
                        case "dragonsword":
                            player.getInventory().addItem(ItemManager.dragonSword);
                            player.sendMessage(Component.text(WW + "§aYou have been given the Dragon Sword"));
                            return true;
                        case "flamescythe":
                            player.getInventory().addItem(ItemManager.flameScythe);
                            player.sendMessage(Component.text(WW + "§aYou have been given the Flame Scythe"));
                            return true;
                        case "evokeraxe":
                            player.getInventory().addItem(ItemManager.evokerAxe);
                            player.sendMessage(Component.text(WW + "§aYou have been given the Evoker Axe"));
                            return true;
                        case "freezegun":
                            player.getInventory().addItem(ItemManager.freezeGun);
                            player.sendMessage(Component.text(WW + "§aYou have been given the Freeze Gun"));
                            return true;
                        case "blastinducingbow":
                            player.getInventory().addItem(ItemManager.blastInducingBow);
                            player.sendMessage(Component.text(WW + "§aYou have been given the Blast-Inducing Bow"));
                            return true;
                        case "quickpick":
                            player.getInventory().addItem(ItemManager.quickPick);
                            player.sendMessage(Component.text(WW + "§aYou have been given the Quick Pick"));
                            return true;
                    }
                }
            }
            else {
                player.sendMessage(NoPermission);
                return true;
            }
        }
        return true;
    }



}
