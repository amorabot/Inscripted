package pluginstudies.pluginstudies.components.CraftingComponents.Equippable;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import pluginstudies.pluginstudies.Crafting.ItemRarities;
import pluginstudies.pluginstudies.Crafting.ItemTypes;
import pluginstudies.pluginstudies.Crafting.Weapons.Enums.DamageTypes;
import pluginstudies.pluginstudies.Crafting.Weapons.Enums.WeaponTypes;
import pluginstudies.pluginstudies.Crafting.Weapons.Modifiers.WeaponModifiers;
import pluginstudies.pluginstudies.CustomDataTypes.ItemInfo.ItemInfoDataType;
import pluginstudies.pluginstudies.CustomDataTypes.ItemInfo.ItemInformation;
import pluginstudies.pluginstudies.CustomDataTypes.WeaponStats.WeaponStats;
import pluginstudies.pluginstudies.CustomDataTypes.WeaponStats.WeaponStatsDataType;
import pluginstudies.pluginstudies.RPGElements;
import pluginstudies.pluginstudies.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pluginstudies.pluginstudies.utils.Utils.color;

public class Weapon extends Item {

    private RPGElements plugin;
    WeaponStats weaponStats;

    public <T extends Enum<ItemTypes>> Weapon
            (RPGElements plugin, T itemType, WeaponTypes itemCategory, boolean id, int ilvl, ItemRarities rarity, String implicit, String coloredTag) {

        super(coloredTag);

        this.plugin = plugin;
        this.itemInfo = new ItemInformation(itemType, itemCategory, id, ilvl, rarity, implicit); //Creating the base item's data

        List<WeaponModifiers> weaponPrefixes = new ArrayList<>(); //Casting the Prefixes and Suffixes so that their methods are available
        for (Enum<?> prefix : itemInfo.getPrefixes()){
            weaponPrefixes.add((WeaponModifiers) prefix);
        }
        List<WeaponModifiers> weaponSuffixes = new ArrayList<>();
        for (Enum<?> suffix : itemInfo.getSuffixes()){
            weaponSuffixes.add((WeaponModifiers) suffix);
        }

        this.weaponStats = new WeaponStats(itemCategory, weaponPrefixes, weaponSuffixes, ilvl); //Creating weapon-specific data
    }

    public ItemStack getItemForm(){

        ItemStack item = new ItemStack(weaponStats.getItemMaterial());
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        List<String> lore = new ArrayList<>();

        int[] dmgRange = weaponStats.getBaseDmg().get(DamageTypes.PHYSICAL);
        // "       &4", 4 1 e d
        String baseDamage = "&c DMG: " + dmgRange[0] + " - " + dmgRange[1];
        lore.add(color(baseDamage));

        for (DamageTypes dmgType : DamageTypes.values()){
            if (weaponStats.getBaseDmg().containsKey(dmgType)){
                switch (dmgType){
                    case PHYSICAL:
                        //modificar o dmg base
                        break;
                    case FIRE:
                        int[] fireRange = weaponStats.getBaseDmg().get(DamageTypes.FIRE);
                        String fireDmg = "       &4" + fireRange[0] + " - " + fireRange[1];
                        lore.add(color(fireDmg));
                        break;
                    case COLD:
                        break;
                    case LIGHTNING:
                        break;
                    case ABYSSAL:
                        int[] abyssRange = weaponStats.getBaseDmg().get(DamageTypes.ABYSSAL);
                        String abyssDmg = "       &d" + abyssRange[0] + " - " + abyssRange[1];
                        lore.add(color(abyssDmg));
                        break;
                }
            }
        }

        if (itemInfo.getRarity() != ItemRarities.COMMON){
            lore.add(color("&8   _______________________"));
        }

        renderAffixes(lore);

        lore.add("");
        String displayName = "";
        switch (itemInfo.getRarity()){
            case COMMON:
                displayName = "&f&l" + weaponStats.getName();
                lore.add(color("&f&l"+tag+ "    &6&l[&7" + itemInfo.getIlvl() + "&6&l]"));
                break;
            case MAGIC:
                displayName = "&9&l" + weaponStats.getName();
                lore.add(color("&9&l"+tag+ "    &6&l[&7" + itemInfo.getIlvl() + "&6&l]"));
                break;
            case RARE:
                displayName = "&e&l" + weaponStats.getName();
//                lore.add(color("&e&l"+tag+ "    &6&l[&7" + itemInfo.getIlvl() + "&6&l]")); // #4c2f91 /#494fc9 /#800617/#30022f
                //
                lore.add(ColorUtils.translateColorCodes("&e&l" + tag + " &6&l[&#4c2f91&l*&7" + itemInfo.getIlvl() + "&#5c023c&l*&6&l]"));
                break;
        }
        itemMeta.setDisplayName(color(displayName));

        itemMeta.setLore(lore);

        //Item information container
        dataContainer.set(new NamespacedKey(plugin, "data"), new ItemInfoDataType(), itemInfo);
        //Weapon information container
        dataContainer.set(new NamespacedKey(plugin, "stats"), new WeaponStatsDataType(), weaponStats);

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }

    private void renderAffixes(List<String> lore){
        render(lore, itemInfo.getPrefixes());
        render(lore, itemInfo.getSuffixes());
    }
    private void render(List<String> lore, List<Enum<?>> affixList){
        for (Enum<?> mod : affixList){
            WeaponModifiers weaponMod = (WeaponModifiers) mod;
            Map<Integer, int[]> modValues = weaponStats.getModifierTierMap().get(weaponMod);
            Set<Integer> modValueKeys = modValues.keySet();

            for (Integer key : modValueKeys){
                int[] values = modValues.get(key);

                String modDisplayName = " " + weaponMod.getDisplayName();
                String modifiedString;
                if (weaponMod.hasSingleRange()){
                    modifiedString = modDisplayName.replace("#", ("&b" + values[0] + "&7"));
                } else {
                    modifiedString = modDisplayName.replace("#", ("&b" + values[0]));
                    modifiedString = modifiedString.replace("@", ("&b" + values[1] + "&7"));
                }
                lore.add(color("&7" + modifiedString));
            }
        }
    }

    @Override
    protected void generateMods(ItemMeta itemMeta) {
        switch (itemInfo.getRarity()){
            case COMMON:

                break;
            case MAGIC:

                break;
            case RARE:

                break;
        }
    }
}
