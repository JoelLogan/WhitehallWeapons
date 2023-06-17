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
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
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

    public WWEventListener (Main plugin) {
        this.plugin = plugin;
    }

    public static void init(){
        potionEffects.add(PotionEffectType.INCREASE_DAMAGE.createEffect(PotionEffect.INFINITE_DURATION, 1));
        potionEffects.add(PotionEffectType.SPEED.createEffect(PotionEffect.INFINITE_DURATION, 1));
        potionEffects.add(PotionEffectType.REGENERATION.createEffect(PotionEffect.INFINITE_DURATION, 1));
        potionEffects.add(PotionEffectType.FAST_DIGGING.createEffect(PotionEffect.INFINITE_DURATION, 1));
        potionEffects.add(PotionEffectType.DOLPHINS_GRACE.createEffect(PotionEffect.INFINITE_DURATION, 1));
        potionEffects.add(PotionEffectType.WATER_BREATHING.createEffect(PotionEffect.INFINITE_DURATION, 1));
    }

    /**
     * (When placing structure) worldborder = 10,000
     * Essence blade screenshot is wrong
     * Blast bow is in basault delta instead of y15
     * todo Check out plugin: <a href="https://www.spigotmc.org/resources/lifesteal-smp-plugin.94387/">...</a>
     */

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
                            for (int i = 1; i < 12; ++i) {
                                double blockX = location.getX() + (int) (playerDirection.getX() * i);
                                double blockY = location.getY() + (int) (playerDirection.getY() * i);
                                double blockZ = location.getZ() + (int) (playerDirection.getZ() * i);
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
                    if (item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)){
                        List<String> savedEffects = this.plugin.getConfig().getStringList("ActiveEffects." + attacker.getUniqueId());
                        if (savedEffects.size() != potionEffects.size()) {
                            addRandomPositiveEffect(attacker);
                            addEffects(attacker);
                            if (maxHealth > 2.0) {
                                maxHealth = (Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH))).getBaseValue() - 2.0;
                                savedMaxHealth.put(player, maxHealth);
                            } else {
                                player.banPlayer("You have run out of life.");
                            }
                        }
                        else {
                            attacker.sendMessage(Component.text("§8You are at max potion effects, so you now won't remove extra hearts from players killed with this sword."));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSwitchHeldItem(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        if (player.getInventory().getItem(event.getNewSlot()) != null) {
            if (Objects.requireNonNull(player.getInventory().getItem(event.getNewSlot())).getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)){
                addEffects(player);
            }
        }
        if (player.getInventory().getItem(event.getPreviousSlot()) != null) {
            if (Objects.requireNonNull(player.getInventory().getItem(event.getPreviousSlot())).getType().equals(Material.NETHERITE_SWORD)){
                if (Objects.requireNonNull(player.getInventory().getItem(event.getPreviousSlot())).getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)){
                    removeEffects(player);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        CheckEffects(event.getWhoClicked());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event){
        CheckEffects(event.getWhoClicked());
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event){
        CheckEffects(event.getPlayer());
    }

    private void CheckEffects(HumanEntity whoClicked) {
        Player player = (Player) whoClicked;
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean present = false;
                if (player.getInventory().getItemInMainHand().hasItemMeta()){
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_SWORD) && player.getInventory().getItemInMainHand().getItemFlags().contains(ItemFlag.HIDE_DYE)) {
                        present = true;
                    }
                }
                if (player.getInventory().getItemInOffHand().hasItemMeta()){
                    if (player.getInventory().getItemInOffHand().getType().equals(Material.NETHERITE_SWORD) && player.getInventory().getItemInOffHand().getItemFlags().contains(ItemFlag.HIDE_DYE)){
                        present = true;
                    }
                }
                if (present){
                    addEffects(player);
                }
                else {
                    removeEffects(player);
                }
            }
        }.runTaskLater(this.plugin, 10L);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event){
        if (event.getItemDrop().getItemStack().getItemFlags().contains(ItemFlag.HIDE_DYE)){
            removeEffects(event.getPlayer());
        }
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event){
        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player player = (Player) event.getEntity();
            if (event.getItem().getItemStack().getItemFlags().contains(ItemFlag.HIDE_DYE)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_SWORD)) {
                            if (player.getInventory().getItemInMainHand().getItemFlags().contains(ItemFlag.HIDE_DYE)) {
                                addEffects(player);
                            }
                        }
                    }
                }.runTaskLater(this.plugin, 10L);
            }
        }
    }

    private void addEffects(Player player){
        for (String effectTypeName : this.plugin.getConfig().getStringList("ActiveEffects." + player.getUniqueId())) {
            PotionEffectType effect = PotionEffectType.getByName(effectTypeName);
            if (effect != null) {
//                if (effect.getName().equals(PotionEffectType.WATER_BREATHING.getName()) || effect.getName().equals(PotionEffectType.DOLPHINS_GRACE.getName())) {
                player.addPotionEffect(effect.createEffect(PotionEffect.INFINITE_DURATION, 0));
//                } else {
//                    player.addPotionEffect(effect.createEffect(PotionEffect.INFINITE_DURATION, 1));
//                }
            }
        }
    }

    private void removeEffects(Player player){
        for (String effectTypeName : this.plugin.getConfig().getStringList("ActiveEffects." + player.getUniqueId())) {
            player.removePotionEffect(Objects.requireNonNull(PotionEffectType.getByName(effectTypeName)));
        }
    }

    private void addRandomPositiveEffect(Player player){
        boolean added = false;
        Random random = new Random();
        List<String> savedEffects = this.plugin.getConfig().getStringList("ActiveEffects." + player.getUniqueId());
        while(!added) {
            PotionEffect randomPotionEffect = potionEffects.get(random.nextInt(potionEffects.size()));
            if (!savedEffects.isEmpty()) {
                if (!savedEffects.contains(randomPotionEffect.getType().getName())) {
                    savedEffects.add(randomPotionEffect.getType().getName());
                    this.plugin.getConfig().set("ActiveEffects." + player.getUniqueId(), savedEffects);
                    added = true;
                }
                else {
                    if (savedEffects.size() == potionEffects.size()){
                        added = true;
                    }
                }
            }
            else {
                List<String> unsavedEffects = new ArrayList<>();
                unsavedEffects.add(randomPotionEffect.getType().getName());
                this.plugin.getConfig().set("ActiveEffects." + player.getUniqueId(), unsavedEffects);
                added = true;
            }
            this.plugin.saveConfig();
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
            if (event.getInventory().getSecondItem().getItemFlags().contains(ItemFlag.HIDE_DYE)) {
                event.setResult(null);
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
            if (player.getInventory().getItem(event.getHand()).getEnchantments().containsKey(Enchantment.LUCK)){
                if (player.getInventory().getItem(event.getHand()).getEnchantments().get(Enchantment.LUCK) == 14){
                    Arrow arrow = (Arrow) event.getProjectile();
                    arrow.addCustomEffect(PotionEffectType.SLOW.createEffect(200, 1), false);
                }
                else if (player.getInventory().getItem(event.getHand()).getEnchantments().get(Enchantment.LUCK) == 15){
                    if (event.getForce() > 0.9) {
                        Arrow arrow = (Arrow) event.getProjectile();
                        arrow.addCustomEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(1, 10), false);
                    }
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
                Location location = arrow.getLocation();
                location.createExplosion(arrow, 3F, false, true);
                for (double x = location.getX() - 2; x <= location.getX() + 2; x++) {
                    for (double y = location.getY() - 2; y <= location.getY() + 2; y++) {
                        for (double z = location.getZ() - 2; z <= location.getZ() + 2; z++) {
                            location.createExplosion(arrow, 0.75F, false, false);
                        }
                    }
                }

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
                block.getWorld().dropItemNaturally(location, new ItemStack(Material.NETHERITE_SCRAP, random.nextInt(1, 3)));
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

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        if (event.getDamager().getType().equals(EntityType.EVOKER_FANGS)){
            EvokerFangs fangs = (EvokerFangs) event.getDamager();
            if (fangs.getOwner() != null) {
                if (fangs.getOwner().getType().equals(EntityType.PLAYER)) {
                    event.setDamage(22.0);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntityType().equals(EntityType.DROPPED_ITEM)) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE)) {
                ItemStack item = ((Item) event.getEntity()).getItemStack();
                if (item.getType().equals(Material.BOW) || item.getType().equals(Material.CROSSBOW)) {
                    if (item.hasItemMeta()) {
                        if (item.getItemMeta().getEnchants().containsKey(Enchantment.LUCK)) {
                            if (item.getItemMeta().getEnchantLevel(Enchantment.LUCK) == 14 || item.getItemMeta().getEnchantLevel(Enchantment.LUCK) == 15) {
                                event.getEntity().setInvulnerable(true);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if ((!event.getEntity().isInLava() && !event.getEntity().isOnGround() && !event.getEntity().isVisualFire()) || !event.getEntity().isValid()) {
                                            event.getEntity().setInvulnerable(false);
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(this.plugin, 20L, 100L);
                            }
                        }
                    }
                }
            }
        }
    }
}
