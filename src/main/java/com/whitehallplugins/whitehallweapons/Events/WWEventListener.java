package com.whitehallplugins.whitehallweapons.Events;

import com.whitehallplugins.whitehallweapons.Items.ItemManager;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.Objects;

public class WWEventListener implements Listener {

//    @EventHandler
//    public static void onRightClick(PlayerInteractEvent event){
//        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
//            if (event.getItem() != null){
//                if (event.getItem().getItemMeta().equals(ItemManager.dragonSword.getItemMeta())){
//                    Player player = event.getPlayer();
//                    player.getWorld().createExplosion(player.getLocation(), 2.0f);
//                }
//            }
//        }
//    }

    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
        if (event.getEntity().getType().equals(EntityType.PLAYER)){
            Player attacker = event.getEntity();
            if (attacker.getActiveItem().getItemMeta().equals(ItemManager.dragonSword.getItemMeta())){
                if (maxHealth > 2.0){
                    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(maxHealth - 6.0);
                }
                else {
                    player.banPlayer("You have run out of life.");
                }
            }
        }
    }
}
