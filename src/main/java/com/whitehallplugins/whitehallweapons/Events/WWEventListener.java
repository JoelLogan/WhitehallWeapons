package com.whitehallplugins.whitehallweapons.Events;

import com.whitehallplugins.whitehallweapons.Items.ItemManager;
import com.whitehallplugins.whitehallweapons.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WWEventListener implements Listener {

    private final Main plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public WWEventListener (Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getItem() != null){
                ItemStack item = event.getItem();
                if (item.getItemMeta().getEnchants().containsKey(Enchantment.LUCK)){
                    if (item.getItemMeta().getEnchantLevel(Enchantment.LUCK) == 13) {
                        Player player = event.getPlayer();
                        if (!cooldowns.containsKey(player)) {
                            Location location = player.getLocation();
                            Vector playerDirection = location.getDirection();
                            for (int i = 1; i < 10; ++i) {
                                int blockX = location.getBlockX() + (int) (playerDirection.getX() * i);
                                int blockY = location.getBlockY() + (int) (playerDirection.getY() * i);
                                int blockZ = location.getBlockZ() + (int) (playerDirection.getZ() * i);
                                Location finalLocation = new Location(player.getWorld(), blockX, blockY, blockZ);
                                EvokerFangs evokerFangs = (EvokerFangs) event.getPlayer().getWorld().spawnEntity(finalLocation, EntityType.EVOKER_FANGS);
                                evokerFangs.setOwner(event.getPlayer());
                            }
                            cooldowns.put(player, System.currentTimeMillis());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    cooldowns.remove(player);
                                }
                            }.runTaskLater(this.plugin, 200L);
                        }
                        else {
                            long secondsLeft = ((cooldowns.get(player) / 1000) + 10) - (System.currentTimeMillis() / 1000);
                            if (secondsLeft > 0) {
                                player.sendMessage(Component.text("§8Axe on cooldown for " + secondsLeft + " more seconds."));
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
        if (Objects.requireNonNull(player.getKiller()).getType().equals(EntityType.PLAYER)){
            Player attacker = player.getKiller();
            ItemStack item = attacker.getActiveItem();
            if (item.getType().equals(Material.NETHERITE_SWORD)) {
                attacker.sendMessage(Component.text("You are holding a " + item.toString()));
                if (item.getItemMeta().getEnchants().containsKey(Enchantment.LUCK)) {
                    if (item.getItemMeta().getEnchantLevel(Enchantment.LUCK) == 11) {
                        if (maxHealth > 2.0) {
                            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(maxHealth - 6.0);
                        } else {
                            player.banPlayer("You have run out of life.");
                        }
                    }
                }
            }
        }
    }



    // @TODO Add rename+enchant cancel for custom items

    // @TODO Finish Lifesteal/Potion setup

    // @TODO Add powdered snow ability NEEDED
    // @TODO Add always slowness 2 arrows when loading


    // @TODO Add auto-smelt + 3x3 mining
}
