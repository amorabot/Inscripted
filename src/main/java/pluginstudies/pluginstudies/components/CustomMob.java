package pluginstudies.pluginstudies.components;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pluginstudies.pluginstudies.utils.Utils.*;

public enum CustomMob {

    NAGA_SHAMAN("&3Naga Shaman", 15, 60, EntityType.ZOMBIE, null,
            makeArmorSet(new ItemStack(Material.LEATHER_HELMET), null, null, null),
            new LootItem(createItem(Material.WOODEN_HOE, 1, false, false, false, "&fNaga Staff",
                    "&7A simple wooden staff used by nagas"), 30)),
    SKELETAL_MAGE("&dSkeletal Mage", 20, 25, EntityType.SKELETON,
            createItem(Material.BONE, 1, true, false, false, null),
            makeArmorSet(new ItemStack(Material.IRON_HELMET), null, null, null),
            new LootItem(createItem(Material.BONE, 1, true, false, false,
            "&fBone Staff","&7" + "&7Ancient bone from a powerful mage"), 10),
            new LootItem(createItem(Material.BONE_MEAL, 1, false, false, false,
                    "&f&lBone Splinters", "&7" + "&7 Splinters of a skeletal mage's bones"), 1, 6, 100)),
    BARO("&dBaro the Fanatic", 40, 10, EntityType.WITHER_SKELETON,
            createItem(Material.STONE_AXE, 1, false, true, true, null),
            makeArmorSet(new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS)),
            new LootItem(createItem(Material.STONE_AXE, 1, true, true, true,
                    "&e&LBurick's corrupted axe", "&7" + "&7Was once a sacred weapon held by the fanatic priest"), 10)),
    BOB("&3Bob the crying zombie", 5, 5, EntityType.ZOMBIE,
            createItem(Material.GHAST_TEAR, 1, true, false, false, null),
            makeArmorSet(new ItemStack(Material.CHAINMAIL_HELMET), null, null, null),
            new LootItem(createItem(Material.GHAST_TEAR, 1, false, false, false,
                    "&f&lBob's tear", "&7", "&7poor bob..."), 1,3, 3));
    private String name;
    private double maxHealth;
    private double spawnChance;
    private EntityType type;
    private ItemStack mainHand;
    private ItemStack[] armor;
    private List<LootItem> lootTable;

    CustomMob(String name, double maxHealth, double spawnChance, EntityType type, ItemStack mainHand, ItemStack[] armor, LootItem... lootItems) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.spawnChance = spawnChance;
        this.type = type;
        this.mainHand = mainHand;
        this.armor = armor;
        this.lootTable = Arrays.asList(lootItems);
    }

    public LivingEntity spawn(Location location){
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
        entity.setCustomNameVisible(true);
        entity.setCustomName(color(name + "&r&c [" + (int) maxHealth + "/" + (int) maxHealth + "♥]"));
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth); //set base value
        entity.setHealth(maxHealth); //set current value
        EntityEquipment entityInv = entity.getEquipment();

        if (armor != null){
            entityInv.setArmorContents(armor); //se setArmorContents recebe null, retorna erro
        }
        entityInv.setHelmetDropChance(0F);
        entityInv.setChestplateDropChance(0F);
        entityInv.setLeggingsDropChance(0F);
        entityInv.setBootsDropChance(0F);
        entityInv.setItemInMainHand(mainHand);
        entityInv.setItemInMainHandDropChance(0F);

        return entity;
    }

    public void tryDropLoot(Location location){
        for (LootItem item : lootTable){
            item.tryDropItem(location);
        }
    }
    public String getName(){
        return name;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getSpawnChance() {
        return spawnChance;
    }
}
