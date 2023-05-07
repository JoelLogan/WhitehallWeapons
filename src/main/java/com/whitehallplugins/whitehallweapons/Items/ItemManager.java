package com.whitehallplugins.whitehallweapons.Items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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

    private static void createDragonSword() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§4Dragon Sword"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7 -§1If a player is killed with this sword,"));
        lore.add(Component.text("§1they will permanently lose 3 hearts."));
        lore.add(Component.text("§7 -§1If a player is killed with this sword,"));
        lore.add(Component.text("§1they will receive a negative potion effect"));
        lore.add(Component.text("§1that will be active while the sword is held.")); // Make sure only held by a player
        meta.lore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        dragonSword = item;
    }

    private static void createFlameScythe() {

    }

    private static void createEvokerAxe() {

    }

    private static void createFreezeGun() {

    }

    private static void createBlastInducingBow() {

    }

    private static void createQuickPick() {

    }

}
