package com.whitehallplugins.whitehallweapons.Items;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemManager {

    public static ItemStack dragonSword, flameScythe, evokerAxe, freezeGun, blastInducingBow, quickPick;

    public static void init() {
        createDragonSword();
        createFlameScythe();
        createEvokerAxe();
        createFreezeGun();
        createBlastInducingBow();
        createQuickPick();
    }

    /**
     * Weapon 1: A sword that is crafted from the dragon egg, that has a special ability
     * where if you kill a player it takes 3 of their hearts and deletes them and for every
     * 3 hearts it takes it adds a potion effect (like infuse meets lifeslteal) but the effects only work while holding the sword
     */
    private static void createDragonSword() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§5Essence Blade"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§aA deadly sword that §7§lEffectively §aremoves hearts"));
        lore.add(Component.text(""));
        lore.add(Component.text("§7 -§9If a player is killed with this sword,"));
        lore.add(Component.text("§9they will permanently lose §d3 hearts§9."));
        lore.add(Component.text(""));
        lore.add(Component.text("§7 -§9If a player is killed with this sword,"));
        lore.add(Component.text("§9they will receive a §anegative potion effect"));
        lore.add(Component.text("§9that will be active while the sword is held.")); // Make sure only held by a player
        meta.lore(lore);
        meta.addEnchant(Enchantment.LUCK, 11, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        dragonSword = item;

        ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft("dragonsword"), dragonSword);
        sr.shape(" E ",
                 " S ",
                 "   ");
        sr.setIngredient('E', Material.DRAGON_EGG);
        sr.setIngredient('S', Material.STICK);
        Bukkit.getServer().addRecipe(sr);
    }

    /**
     * Weapon 2: A Scythe (retextured hoe) that has sweeping edge 10, fire aspect 3,
     * 9 attack damage and a 5 second hit cooldown found in a temple on the nether roof.
     */
    private static void createFlameScythe() {
        ItemStack item = new ItemStack(Material.NETHERITE_HOE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§4Reaper Scythe"));         // @TODO Loot Generation NEEDED
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§9A powerful weapon that §4§lBURNS §9enemies"));
        meta.lore(lore);
        meta.addEnchant(Enchantment.LUCK, 12, true);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 15, true);
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 10, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 3, true);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "Slower", -3.8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        flameScythe = item;
    }

    /**
     * Weapon 3: An Axe that does 10 attack damage, and has
     * the jaw abilities of an evoker found in an tribal village in a lush cave given by a wandering trader.
     */
    private static void createEvokerAxe() {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§aAxe Of The Green Earth"));         // @TODO Loot Generation NEEDED
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§9A hungry axe that can summon"));
        lore.add(Component.text("§6Evoker jaws §9when right-clicked"));
        lore.add(Component.text("§8(10 Second Cooldown)"));
        meta.lore(lore);
        meta.addEnchant(Enchantment.LUCK, 13, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        evokerAxe = item;
    }

    /**
     * Weapon 4: A freeze gun (crossbow) that when it shoots players it gives them
     * slowness 2 for 10 seconds, and gives them the powdered snow effect.
     * Found in a ice cave in the side of a snowy mountain guarded by mobs
     */
    private static void createFreezeGun() {
        ItemStack item = new ItemStack(Material.CROSSBOW, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§bFrost Bow"));         // @TODO Loot Generation+Mobs NEEDED
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§3An icy crossbow that can inflicts"));
        lore.add(Component.text("§3slowness and appears to freeze enemies"));
        meta.lore(lore);
        meta.addEnchant(Enchantment.LUCK, 14, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        freezeGun = item;
    }

    /**
     * Weapon 5: A bow that explodes on impact found in a basalt delta
     */
    private static void createBlastInducingBow() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§aBlast Bow"));         // @TODO Loot Generation NEEDED
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7A seemingly normal bow that"));
        lore.add(Component.text("§ccreates an §lexplosion §cupon impact"));
        meta.lore(lore);
        meta.addEnchant(Enchantment.LUCK, 15, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        blastInducingBow = item;
    }

    /**
     * Weapon 6: a pickaxe that mines 3x3 (blast mining), efficiency 7, fortune 4, and auto smelting.
     * Found at an altar somewhere in the outer end islands
     */
    private static void createQuickPick() {
        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§6Pick of Vitality"));         // @TODO Loot Generation NEEDED
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§6The ultimate pickaxe!"));
        lore.add(Component.text(""));
        lore.add(Component.text("§5A fortunate pickaxe that mines a 3x3 hole"));
        lore.add(Component.text("§5and automatically smelts ores mined"));
        meta.lore(lore);
        meta.addEnchant(Enchantment.LUCK, 16, true);
        meta.addEnchant(Enchantment.DIG_SPEED, 7, true);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 4, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        quickPick = item;
    }

}
