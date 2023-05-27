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
import pluginstudies.pluginstudies.CustomDataTypes.ItemInfo.ItemInformationDataType;
import pluginstudies.pluginstudies.CustomDataTypes.ItemInfo.BaseItem;
import pluginstudies.pluginstudies.CustomDataTypes.RPGElementsContainerDataType;
import pluginstudies.pluginstudies.CustomDataTypes.WeaponStats.Weapon;
import pluginstudies.pluginstudies.CustomDataTypes.WeaponStats.WeaponStatsDataType;
import pluginstudies.pluginstudies.RPGElements;
import pluginstudies.pluginstudies.components.CraftingComponents.CraftableItem;
import pluginstudies.pluginstudies.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pluginstudies.pluginstudies.utils.Utils.color;

public class WeaponService implements CraftableItem { //Em caso de novos protocoloss de tratamento de weapons, um novo service é criado

    private RPGElements plugin;
    String tag;
    BaseItem itemData; //NÃO IMPLEMENTADO, POIS É REPONSABILIDADE DO WEAPON SERVICE MANEJAR AS INFOS DE ITEM
    Weapon weaponData;

    public <T extends Enum<ItemTypes>> WeaponService
            (RPGElements plugin, T itemType, WeaponTypes itemCategory, boolean id, int ilvl, ItemRarities rarity, String implicit, String coloredTag) {

//        super(coloredTag);

        this.plugin = plugin;

        this.tag = rarity.toString();
        this.itemData = new BaseItem(itemType, itemCategory, id, ilvl, rarity, implicit); //Creating the base item's data

        List<WeaponModifiers> weaponPrefixes = new ArrayList<>(); //Casting the Prefixes and Suffixes so that their methods are available
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
            lore.add(color("&8   _______________________"));
        }

        renderAffixes(lore);

        lore.add("");
        String displayName = "";
        switch (itemData.getRarity()){
            case COMMON:
                displayName = "&f&l" + weaponData.getName();
                lore.add(color("&f&l"+tag+ "    &6&l[&7" + itemData.getIlvl() + "&6&l]"));
                break;
            case MAGIC:
                displayName = "&9&l" + weaponData.getName();
                lore.add(color("&9&l"+tag+ "    &6&l[&7" + itemData.getIlvl() + "&6&l]"));
                break;
            case RARE:
                displayName = "&e&l" + weaponData.getName();
//                lore.add(color("&e&l"+tag+ "    &6&l[&7" + itemInfo.getIlvl() + "&6&l]")); // #4c2f91 /#494fc9 /#800617/#30022f
//                &#4c2f91&l*   //   &#5c023c&l*
                lore.add(ColorUtils.translateColorCodes("&e&l" + tag + " &6&l[&7" + itemData.getIlvl() + "&6&l]"));
                break;
        }
        itemMeta.setDisplayName(color(displayName));

        itemMeta.setLore(lore);

        serializeContainers(dataContainer);

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }
    @Override
    public void serializeContainers(PersistentDataContainer itemContainer){
//        //Item information container
//        itemContainer.set(new NamespacedKey(plugin, "data"), new ItemInformationDataType(), itemData);
//        //Weapon information container
//        itemContainer.set(new NamespacedKey(plugin, "stats"), new WeaponStatsDataType(), weaponData);
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
    @Override
    public void render(List<String> lore, List<Enum<?>> affixList){
        for (Enum<?> mod : affixList){
            WeaponModifiers weaponMod = (WeaponModifiers) mod;
            Map<Integer, int[]> modValues = weaponData.getModifierTierMap().get(weaponMod);
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
}
