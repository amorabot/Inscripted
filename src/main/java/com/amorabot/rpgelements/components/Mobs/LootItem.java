package com.amorabot.rpgelements.components.Mobs;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LootItem {
    private ItemStack item;
    private int min = 1, max = 1;
    private double dropRate;
    private static Random randomiser = new Random();

    public LootItem(ItemStack item, double dropRate){ //Esse construtor será para se quisermos dropar apenas 1 item
        this.item = item;
        this.dropRate = dropRate;
    }

    public LootItem(ItemStack item, int min, int max, double dropRate) { //Esse é para itens com um range de qtd.
        this.item = item;
        this.min = min;
        this.max = max;
        this.dropRate = dropRate;
    }

    public void tryDropItem(Location location){
        if (Math.random() * 101 > dropRate){ return; } //se, digamos, dropRate é 75, tudo abaixo de 75 é aceitavel
        int amount = randomiser.nextInt(max - min + 1) + min; //cria inteiros entre min e max
        if (amount == 0){ return; }
        ItemStack item = this.item.clone();
        item.setAmount(amount);
        location.getWorld().dropItemNaturally(location, item);
    }
}
