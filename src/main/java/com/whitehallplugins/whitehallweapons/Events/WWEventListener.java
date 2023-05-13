package com.whitehallplugins.whitehallweapons.Events;

import com.whitehallplugins.whitehallweapons.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class WWEventListener implements Listener {

    private final Main plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private final Map<Player, Double> savedMaxHealth = new HashMap<>();
    private static final List<PotionEffect> potionEffects = new ArrayList<>();
    private UUID holdingSword = UUID.randomUUID();

    public WWEventListener (Main plugin) {
        this.plugin = plugin;
    }


    /**
     * @TODO No Positive Effects For Dragon Sword Holder
     */


    public static void init(){
        potionEffects.add(PotionEffectType.INCREASE_DAMAGE.createEffect(PotionEffect.INFINITE_DURATION, 2));
        potionEffects.add(PotionEffectType.SPEED.createEffect(PotionEffect.INFINITE_DURATION, 2));
        potionEffects.add(PotionEffectType.REGENERATION.createEffect(PotionEffect.INFINITE_DURATION, 2));
        potionEffects.add(PotionEffectType.FAST_DIGGING.createEffect(PotionEffect.INFINITE_DURATION, 2));
        potionEffects.add(PotionEffectType.DOLPHINS_GRACE.createEffect(PotionEffect.INFINITE_DURATION, 1));
        potionEffects.add(PotionEffectType.WATER_BREATHING.createEffect(PotionEffect.INFINITE_DURATION, 1));
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
        savedMaxHealth.put(player, maxHealth);
        if (player.getKiller() != null){
            if (player.getKiller().getType().equals(EntityType.PLAYER)) {
                Player attacker = player.getKiller();
                ItemStack item = attacker.getInventory().getItemInMainHand();
                if (item.getType().equals(Material.NETHERITE_SWORD)) {
                    if (item.getItemMeta().getEnchants().containsKey(Enchantment.LUCK)) {
                        if (item.getItemMeta().getEnchantLevel(Enchantment.LUCK) == 11) {
                            addRandomPositiveEffect(player);
                            if (maxHealth > 2.0) {
                                maxHealth = (Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH))).getBaseValue() - 6.0;
                                savedMaxHealth.put(player, maxHealth);
                            } else {
                                player.banPlayer("You have run out of life.");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSwitchHeldItem(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.LUCK)){
            if (player.getInventory().getItemInMainHand().getEnchantments().get(Enchantment.LUCK) == 11){
                if (!holdingSword.equals(player.getUniqueId())){
                    holdingSword = player.getUniqueId();
                    if (this.plugin.getConfig().getList("ActiveEffects." + player.getUniqueId()) != null) {
                        for (Object effectType : Objects.requireNonNull(this.plugin.getConfig().getStringList("ActiveEffects." + player.getUniqueId()))) {
                            PotionEffectType effect = (PotionEffectType) effectType;
                            if (effect == PotionEffectType.WATER_BREATHING || effect == PotionEffectType.DOLPHINS_GRACE){
                                player.addPotionEffect(effect.createEffect(PotionEffect.INFINITE_DURATION, 1));
                            }
                            else {
                                player.addPotionEffect(effect.createEffect(PotionEffect.INFINITE_DURATION, 2));
                            }
                        }
                    }
                }
            }
        }
        else {
            if (holdingSword.equals(player.getUniqueId())){
                holdingSword = UUID.randomUUID();
                if (this.plugin.getConfig().getList("ActiveEffects." + player.getUniqueId()) != null) {
                    for (Object effectType : Objects.requireNonNull(this.plugin.getConfig().getList("ActiveEffects." + player.getUniqueId()))) {
                        player.removePotionEffect((PotionEffectType) effectType);
                    }
                }
            }
        }
    }

    private void addRandomPositiveEffect(Player player){
        boolean added = false;
        Random random = new Random();
        List<String> savedEffects = this.plugin.getConfig().getStringList("ActiveEffects." + player.getUniqueId());
        while(!added) {
            PotionEffect randomPotionEffect = potionEffects.get(random.nextInt(potionEffects.size() - 1));
            if (!savedEffects.contains(randomPotionEffect.getType().toString())) {
                savedEffects.add(randomPotionEffect.getType().toString());
                this.plugin.getConfig().set("ActiveEffects." + player.getUniqueId(), savedEffects);
            }
            this.plugin.saveConfig();
            added = true;
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (savedMaxHealth.containsKey(player)){
            double savedHealth = savedMaxHealth.get(player);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(savedHealth);
        }
    }

    @EventHandler
    public void onEnchantItem(PrepareItemEnchantEvent event) {
        if (event.getItem().getEnchantments().containsKey(Enchantment.LUCK)){
            if (event.getItem().getEnchantments().get(Enchantment.LUCK) > 10){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent event){
        if (event.getInventory().getFirstItem() != null) {
            if (event.getInventory().getFirstItem().getEnchantments().containsKey(Enchantment.LUCK)) {
                if (event.getInventory().getFirstItem().getEnchantments().get(Enchantment.LUCK) > 10) {
                    event.setResult(null);
                }
            }
        }
        if (event.getInventory().getSecondItem() != null){
            if (event.getInventory().getSecondItem().getEnchantments().containsKey(Enchantment.LUCK)) {
                if (event.getInventory().getSecondItem().getEnchantments().get(Enchantment.LUCK) > 10) {
                    event.setResult(null);
                }
            }
        }
    }

    @EventHandler
    public void onPrepareGrindstone(PrepareGrindstoneEvent event){
        if (event.getInventory().getUpperItem() != null) {
            if (event.getInventory().getUpperItem().getEnchantments().containsKey(Enchantment.LUCK)) {
                if (event.getInventory().getUpperItem().getEnchantments().get(Enchantment.LUCK) > 10) {
                    event.setResult(null);
                }
            }
        }
        if (event.getInventory().getLowerItem() != null){
            if (event.getInventory().getLowerItem().getEnchantments().containsKey(Enchantment.LUCK)) {
                if (event.getInventory().getLowerItem().getEnchantments().get(Enchantment.LUCK) > 10) {
                    event.setResult(null);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFireArrow(EntityShootBowEvent event){
        if (event.getEntity().getType().equals(EntityType.PLAYER)){
            Player player = (Player) event.getEntity();
            if (player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.LUCK)){
                if (player.getInventory().getItemInMainHand().getEnchantments().get(Enchantment.LUCK) == 14){
                    Arrow arrow = (Arrow) event.getProjectile();
                    arrow.addCustomEffect(PotionEffectType.SLOW.createEffect(200, 1), false);
                }
                else if (player.getInventory().getItemInMainHand().getEnchantments().get(Enchantment.LUCK) == 15){
                    Arrow arrow = (Arrow) event.getProjectile();
                    arrow.addCustomEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(1, 10), false);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if (event.getEntity().getType().equals(EntityType.ARROW)){
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getCustomEffects().contains(PotionEffectType.SLOW.createEffect(200, 1))){
                if (event.getHitEntity() != null){
                    if (event.getHitEntity().getType().equals(EntityType.PLAYER)){
                        event.getHitEntity().setFreezeTicks(400);
                    }
                }
                arrow.remove();
            }
            else if (arrow.getCustomEffects().contains(PotionEffectType.INCREASE_DAMAGE.createEffect(1, 10))){
                arrow.getLocation().createExplosion(arrow, 4F, false, true);
                arrow.remove();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Random random = new Random();
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.LUCK)){
            if (player.getInventory().getItemInMainHand().getEnchantments().get(Enchantment.LUCK) == 16){
                Block block = event.getBlock();
                for (int x = block.getX() - 1; x <= block.getX() + 1; x++) {
                    for (int y = block.getY() - 1; y <= block.getY() + 1; y++) {
                        for (int z = block.getZ() - 1; z <= block.getZ() + 1; z++) {
                            autoSmelt(player, block, random, x, y, z);
                        }
                    }
                }

            }
        }
    }

    private void autoSmelt(Player player, Block mainBlock, Random random, int x, int y, int z) {
        Location location = new Location(mainBlock.getWorld(), x, y, z);
        Block block = mainBlock.getWorld().getBlockAt(location);
        Material blockType = block.getWorld().getBlockAt(x, y, z).getType();
        switch (blockType) {
            case ANCIENT_DEBRIS:
                block.getWorld().getBlockAt(location).setType(Material.AIR);
                block.getWorld().dropItemNaturally(location, new ItemStack(Material.NETHERITE_SCRAP));
                break;
            case COPPER_ORE:
                block.getWorld().getBlockAt(location).setType(Material.AIR);
                block.getWorld().dropItemNaturally(location, new ItemStack(Material.COPPER_INGOT, random.nextInt(9, 25)));
                break;
            case GOLD_ORE:
                block.getWorld().getBlockAt(location).setType(Material.AIR);
                block.getWorld().dropItemNaturally(location, new ItemStack(Material.GOLD_INGOT, random.nextInt(2, 5)));
                break;
            case IRON_ORE:
                block.getWorld().getBlockAt(location).setType(Material.AIR);
                block.getWorld().dropItemNaturally(location, new ItemStack(Material.IRON_INGOT, random.nextInt(2, 5)));
                break;
            default:
                block.getWorld().getBlockAt(x, y, z).breakNaturally(player.getInventory().getItemInMainHand());
                break;
        }
    }
}
