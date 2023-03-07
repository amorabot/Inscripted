package pluginstudies.pluginstudies.components;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pluginstudies.pluginstudies.Crafting.Weapons.AxeAffixes;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInfoDataType;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInformation;
import pluginstudies.pluginstudies.PluginStudies;
import pluginstudies.pluginstudies.utils.Utils;

import java.util.*;

import static pluginstudies.pluginstudies.utils.Utils.color;
import static pluginstudies.pluginstudies.utils.Utils.log;

public abstract class CraftableItem {

    protected boolean identified;
    protected int ilvl;
    protected int rarity; // 0 = Common, 1 = Magic, 2 = Rare, TODO: 3 = Unique
    protected int affixLimit;
    protected Material material;
    protected String implicit;
    protected List<String> prefixes = new ArrayList<>();
    protected List<String> suffixes = new ArrayList<>();
    protected List<String> affixes = new ArrayList<>();

    protected ItemStack generateItem(PluginStudies plugin, int rarity){
        ItemStack item = null;
        switch(rarity){
            case 0:
                item =  generateCommon(plugin);
                break;
            case 1:
                item = generateMagic(plugin);
                break;
            case 2:
                item = generateRare(plugin);
                break;
        }
        return item;
    }

    private ItemStack generateCommon(PluginStudies plugin){
        setRarity(0);

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        List<String> lore = new ArrayList<>();

        dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "IDED");
        dataContainer.set(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER, ilvl);
        dataContainer.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER, rarity);
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, 0);

        itemMeta.setDisplayName(color("&f&lCommon item"));
        if (this instanceof CraftableWeapon){
            log("DMG: " + ((CraftableWeapon) this).getBaseDmg()[0] + "/" + ((CraftableWeapon) this).getBaseDmg()[1]);
            dataContainer.set(new NamespacedKey(plugin, "type"), PersistentDataType.STRING, "WEAPON");
            dataContainer.set(new NamespacedKey(plugin, "weapon-type"), PersistentDataType.STRING, ((CraftableWeapon) this).getWeaponType());
            dataContainer.set(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY, ((CraftableWeapon) this).getBaseDmg());

            lore.add(color("&c DMG: " + ((CraftableWeapon) this).getBaseDmg()[0] + "-" + ((CraftableWeapon) this).getBaseDmg()[1]));
            lore.add(color("&7"));
        }
        lore.add(color("&7Item level: " + "&6&l" + ilvl));
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(itemMeta);
        return item;
    }
    private ItemStack generateMagic(PluginStudies plugin){
        setRarity(1);
        int modifiers = getRandomNumber(1, affixLimit);

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "UNIDED");
        dataContainer.set(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER, ilvl);
        dataContainer.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER, rarity);
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, modifiers);

        if (this instanceof CraftableWeapon){
            dataContainer.set(new NamespacedKey(plugin, "type"), PersistentDataType.STRING, "WEAPON");
            dataContainer.set(new NamespacedKey(plugin, "weapon-type"), PersistentDataType.STRING, ((CraftableWeapon) this).getWeaponType());
            dataContainer.set(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY, ((CraftableWeapon) this).getBaseDmg());
        }
        //TODO: fazer a equivalência para armaduras

        Map<String, int[]> mappedAffixes = selectTiers(selectAffixes(modifiers), dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING));

        ModifierInformation modData = new ModifierInformation(this.affixes, mappedAffixes);
        dataContainer.set(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType(), modData);

        itemMeta.setDisplayName(color("&4&lUnidentified item"));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }
    private ItemStack generateRare(PluginStudies plugin){
        setRarity(2);
        int modifiers = getRandomNumber(3, affixLimit);

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(new NamespacedKey(plugin, "state"), PersistentDataType.STRING, "UNIDED");
        dataContainer.set(new NamespacedKey(plugin, "ilvl"), PersistentDataType.INTEGER, ilvl);
        dataContainer.set(new NamespacedKey(plugin, "rarity"), PersistentDataType.INTEGER, rarity);
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, modifiers);

        if (this instanceof CraftableWeapon){
            dataContainer.set(new NamespacedKey(plugin, "type"), PersistentDataType.STRING, "WEAPON");
            dataContainer.set(new NamespacedKey(plugin, "weapon-type"), PersistentDataType.STRING, ((CraftableWeapon) this).getWeaponType());
            dataContainer.set(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY, ((CraftableWeapon) this).getBaseDmg());
        }

        Map<String, int[]> mappedAffixes = selectTiers(selectAffixes(modifiers), dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING));

        ModifierInformation modData = new ModifierInformation(this.affixes, mappedAffixes);
        dataContainer.set(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType(), modData);

        itemMeta.setDisplayName(color("&4&lUnidentified item"));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }
    private Map<String, int[]> selectTiers(List<String> selectedAffixes, String itemType){
        switch (itemType){
            case "WEAPON":
                String weaponType = ((CraftableWeapon) this).getWeaponType();
                switch (weaponType){
                    case "AXE":
                        //Sorting axe affixes
                        List<String> AxeAffixList = new ArrayList<>();

                        String[] axePrefixes = AxeAffixes.PREFIXES.getAffixList();
                        String[] axeSuffixes = AxeAffixes.SUFFIXES.getAffixList();
                        AxeAffixList.addAll(Arrays.asList(axePrefixes));
                        AxeAffixList.addAll(Arrays.asList(axeSuffixes));

                        //Comparing the 2 lists and creating a third one
                        List<String> finalList = new ArrayList<>();
                        for (String affix : AxeAffixList){
                            //Checking the full list and getting the right order
                            if (selectedAffixes.contains(affix)){
                                //If there's a match, append to the end of the final affix list
                                finalList.add(affix);
                            }
                        }
                        this.affixes = finalList;

                        //Tier selection
                        Map<String, int[]> tierMapping = new HashMap<>();

                        for (String affix : finalList){
                            Map<Integer, List<int[]>> currentAffixMap;

                            if (Arrays.asList(axePrefixes).contains(affix)){ // If prefix:
                                //Get it on the prefix map
                                currentAffixMap = AxeAffixes.PREFIXES.getAffix(affix);
                            } else { // Its a suffix, do:
                                currentAffixMap = AxeAffixes.SUFFIXES.getAffix(affix);
                            }

                            Set<Integer> currentKeySet = currentAffixMap.keySet();
                            List<Integer> sortedKeySet = new ArrayList<>();
                            sortedKeySet.addAll(currentKeySet);
                            sortedKeySet.sort(Comparator.naturalOrder());
                            //Find the possible roll ranges
                            int match = matchItemLevel(sortedKeySet);
                            int maxTierIndex = sortedKeySet.indexOf(match);
                            //Choose a random tier
                            int chosenTier = getRandomNumber(0, maxTierIndex);
                            //Select the corresponding key to that tier
                            int tier = sortedKeySet.get(chosenTier);
                            //Get the value associated with that tier
                            List<int[]> chosenTierRanges = currentAffixMap.get(tier);
                            //Roll the tier of the chosen prefix
                            if (chosenTierRanges.size() == 2){ //If its a 2 range modifier
                                int[] firstRange = chosenTierRanges.get(0);
                                int[] secondRange = chosenTierRanges.get(1);

                                int firstValue = getRandomNumber(firstRange[0], firstRange[1]);
                                int secondValue = getRandomNumber(secondRange[0], secondRange[1]);
                                int[] finalRange = {firstValue, secondValue};

                                //Map the rolled mod
                                tierMapping.put(affix, finalRange);
                            } else { // Its a single range mod.
                                int[] range = chosenTierRanges.get(0);

                                int[] finalValue = {getRandomNumber(range[0], range[1])};

                                tierMapping.put(affix, finalValue);
                            }
                        }
                        return tierMapping;
                    case "POLEARM":
                        break;
                    case "SHORTSWORD":
                        break;
                }
                break;
            case "ARMOR":
                break;
        }
        log("Unable to select affix tiers for this item");
        return null;
    }
    private List<String> selectAffixes(int modifiers){ //TODO: deixar a seleção de affixos genérica (apenas para axe no momento)
        List<String> selectedAffixes = new ArrayList<>();
        for (int i = 0; i < modifiers; i++){
            selectedAffixes.add(getRandomAffix(selectedAffixes));
        }
        return selectedAffixes;
    }
    private String getRandomAffix(List<String> selectedAffixes){
        String affix;

        double selector = Math.random();
        if (selector > 0.5){
            //Prefix
            if (!hasOpenPrefix()){
                affix = getRandomSuffix(selectedAffixes);
                return affix;
            }
            affix = getRandomPrefix(selectedAffixes);
        } else {
            //Suffix
            if (!hasOpenSuffix()){ //Se não tiver um suffix disponivel, gere um prefixo
                affix = getRandomPrefix(selectedAffixes);
                return affix;
            } //senão, gere o suffix
            affix = getRandomSuffix(selectedAffixes);
        }
        return affix;
    }
    private String getRandomPrefix(List<String> selectedAffixes){
        String prefix = "";

        boolean found = false;
        while (!found){
            int rng = (int) (Math.random() * (AxeAffixes.PREFIXES.getAffixList().length));
            String randomPrefix = AxeAffixes.PREFIXES.getAffixList()[rng];
            if (!(selectedAffixes.contains(randomPrefix))){
                prefix = randomPrefix;
                found = true;
            }
        }
        this.prefixes.add(prefix);
        return prefix;
    }
    private boolean hasOpenPrefix(){
        switch (rarity){
            case 1:
                //checar se tem mod livre
                if ((this.prefixes.size()+this.suffixes.size()) < affixLimit){
                    return true;
                }
                break;
            case 2:
                //checar prefixes.size()
                if (this.prefixes.size()<3){
                    return true;
                }
                break;
        }
        return false;
    }
    private String getRandomSuffix(List<String> selectedAffixes){
        String suffix = "";

        boolean found = false;
        while (!found){
            int rng = (int) (Math.random() * (AxeAffixes.SUFFIXES.getAffixList().length));
            String randomSuffix = AxeAffixes.SUFFIXES.getAffixList()[rng];
            if (!(selectedAffixes.contains(randomSuffix))){
                suffix = randomSuffix;
                found = true;
            }
        }
        this.suffixes.add(suffix);
        return suffix;
    }
    private boolean hasOpenSuffix(){
        switch (rarity){
            case 1:
                //checar se tem mod livre
                if ((this.prefixes.size()+this.suffixes.size()) < affixLimit){
                    return true;
                }
                break;
            case 2:
                if (this.suffixes.size()<3){
                    return true;
                }
                break;
        }
        return false;
    }
    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * ((max - min)+1)) + min);
    }
    private void setRarity(int rarity){
        this.rarity = rarity;
        switch(rarity){
            case 0:
                this.affixLimit = 0;
                break;
            case 1:
                this.affixLimit = 2;
                break;
            case 2:
                this.affixLimit = 6;
                break;
        }
    }
    protected int matchItemLevel(Set<Integer> ilvlRanges){ //TODO simplify matchItemLevel()
        int match = 0;
        List<Integer> list = new ArrayList<>();
        list.addAll(ilvlRanges);
        if (!(list.contains(this.ilvl))){
            list.add(this.ilvl);
        } else { //é um match
            match = this.ilvl;
            return match;
        }
        list.sort(Comparator.naturalOrder()); //ordenando a lista
        if (list.indexOf(this.ilvl) != 0){
            match = list.get(list.indexOf(this.ilvl)-1);
            return match;
        }
        return list.get(0);
    }
    private int matchItemLevel(List<Integer> ilvlRanges){
        int match = 0;
        List<Integer> list = new ArrayList<>();
        list.addAll(ilvlRanges);
        if (!(list.contains(this.ilvl))){
            list.add(this.ilvl);
        } else { //é um match
            match = this.ilvl;
            return match;
        }
        list.sort(Comparator.naturalOrder()); //ordenando a lista
        if (list.indexOf(this.ilvl) != 0){
            match = list.get(list.indexOf(this.ilvl)-1);
            return match;
        }
        return list.get(0);
    }
}