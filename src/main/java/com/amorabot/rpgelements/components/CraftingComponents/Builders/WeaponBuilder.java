package com.amorabot.rpgelements.components.CraftingComponents.Builders;

import com.amorabot.rpgelements.Crafting.RangeTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.TierValuePair;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.WeaponModifiers;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import com.amorabot.rpgelements.Crafting.ItemRarities;
import com.amorabot.rpgelements.Crafting.ItemTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.DamageTypes;
import com.amorabot.rpgelements.Crafting.Weapons.Enums.WeaponTypes;
import com.amorabot.rpgelements.components.CraftingComponents.Items.BaseItem;
import com.amorabot.rpgelements.CustomDataTypes.RPGElementsContainerDataType;
import com.amorabot.rpgelements.components.CraftingComponents.Items.Weapon;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.CraftingComponents.CraftableItem;
import com.amorabot.rpgelements.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.amorabot.rpgelements.utils.Utils.color;

public class WeaponBuilder implements CraftableItem { //Em caso de novos protocoloss de tratamento de weapons, um novo service é criado

    private final RPGElements plugin;
    String tag;// TODO: reavaliar implementacao das tags
    BaseItem itemData;
    Weapon weaponData;
    public <T extends Enum<ItemTypes>> WeaponBuilder
            (RPGElements plugin, T itemType, WeaponTypes itemCategory, boolean id, int ilvl, ItemRarities rarity, String implicit, String coloredTag) {

        this.plugin = plugin;

        this.tag = rarity.toString();
        this.itemData = new BaseItem(itemType, itemCategory, id, ilvl, rarity, implicit); //Creating the base item's data //CRIAR NA DECLARACAO DESSA CLASSE VIA CONSTRUTOR.

        List<WeaponModifiers> weaponPrefixes = new ArrayList<>(); //Casting the Prefixes and Suffixes to Weapon so that their methods are available
        for (Enum<?> prefix : itemData.getPrefixes()){
            weaponPrefixes.add((WeaponModifiers) prefix);
        }
        List<WeaponModifiers> weaponSuffixes = new ArrayList<>();
        for (Enum<?> suffix : itemData.getSuffixes()){
            weaponSuffixes.add((WeaponModifiers) suffix);
        }

        this.weaponData = new Weapon(itemCategory, weaponPrefixes, weaponSuffixes, ilvl); //Creating weapon-specific data
    }
    @Override
    public ItemStack getItemForm(){

        ItemStack item = new ItemStack(weaponData.getItemMaterial());
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        List<String> lore = new ArrayList<>();

        int[] dmgRange = weaponData.getBaseDmg().get(DamageTypes.PHYSICAL);
        // "       &4", 4 1 e d
        String baseDamage = "&c DMG: " + dmgRange[0] + " - " + dmgRange[1];
        lore.add(color(baseDamage));

        for (DamageTypes dmgType : DamageTypes.values()){
            if (weaponData.getBaseDmg().containsKey(dmgType)){
                switch (dmgType){
                    case PHYSICAL:
                        //modificar o dmg base
                        break;
                    case FIRE:
                        int[] fireRange = weaponData.getBaseDmg().get(DamageTypes.FIRE);
                        String fireDmg = "       &4" + fireRange[0] + " - " + fireRange[1];
                        lore.add(color(fireDmg));
                        break;
                    case COLD:
                        break;
                    case LIGHTNING:
                        break;
                    case ABYSSAL:
                        int[] abyssRange = weaponData.getBaseDmg().get(DamageTypes.ABYSSAL);
                        String abyssDmg = "       &d" + abyssRange[0] + " - " + abyssRange[1];
                        lore.add(color(abyssDmg));
                        break;
                }
            }
        }

        if (itemData.getRarity() != ItemRarities.COMMON){
            lore.add(color("&8 _______________________ "));
        }

        renderAffixes(lore);

        lore.add("");
//        String displayName = "";
        switch (itemData.getRarity()){
            case COMMON:
//                displayName =  //4
                lore.add(color("&f&l"+tag+ " &6&l[&7" + itemData.getIlvl() + "&6&l]"));
                break;
            case MAGIC:
//                displayName = "&" + weaponData.getName(); //9
                lore.add(color("&9&l"+tag+ " &6&l[&7" + itemData.getIlvl() + "&6&l]"));
                break;
            case RARE:
//                displayName = "&e" + weaponData.getName(); //E
//                lore.add(color("&e&l"+tag+ "    &6&l[&7" + itemInfo.getIlvl() + "&6&l]")); // #4c2f91 /#494fc9 /#800617/#30022f
//                &#4c2f91&l*   //   &#5c023c&l*
                lore.add(ColorUtils.translateColorCodes("&e&l" + tag + " &6&l[&7" + itemData.getIlvl() + "&6&l]"));
                break;
        }
        itemMeta.setDisplayName(ColorUtils.translateColorCodes("&" + weaponData.getNameColor((WeaponTypes) itemData.getCategory()) + weaponData.getName()));

        itemMeta.setLore(lore);

        serializeContainers(dataContainer);

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }
    @Override
    public void serializeContainers(PersistentDataContainer itemContainer){
        //Item information container
        RPGElementsContainerDataType<BaseItem> itemDataContainer = new RPGElementsContainerDataType<>(BaseItem.class);
        itemContainer.set(new NamespacedKey(plugin, "data"), itemDataContainer, itemData);
        //Weapon information container
        RPGElementsContainerDataType<Weapon> weaponDataContainer = new RPGElementsContainerDataType<>(Weapon.class);
        itemContainer.set(new NamespacedKey(plugin, "stats"), weaponDataContainer, weaponData);
    }

    private void renderAffixes(List<String> lore){
        render(lore, itemData.getPrefixes());
        render(lore, itemData.getSuffixes());
    }
//    @Override
    public void render(List<String> lore, List<Enum<?>> affixList){
        for (Enum<?> mod : affixList){
            WeaponModifiers weaponMod = (WeaponModifiers) mod;
            TierValuePair modValues = weaponData.getModifierTierMap().get(weaponMod);

            int[] values = modValues.value();

            String modDisplayName = " " + weaponMod.getDisplayName();
            String modifiedString;
            if (weaponMod.getRangeType() == RangeTypes.SINGLE_RANGE){
                modifiedString = modDisplayName.replace("#", ("&#95dbdb" + values[0] + "&7"));
            } else {
                modifiedString = modDisplayName.replace("#", ("&#95dbdb" + values[0]));
                modifiedString = modifiedString.replace("@", ("&#95dbdb" + values[1] + "&7"));
            }
            lore.add(ColorUtils.translateColorCodes("&7" + modifiedString));
        }
    }
}
