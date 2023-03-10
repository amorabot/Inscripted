package pluginstudies.pluginstudies.components;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pluginstudies.pluginstudies.Crafting.TableAcessInterface;
import pluginstudies.pluginstudies.Crafting.Weapons.AxeAffixes;
import pluginstudies.pluginstudies.Crafting.Weapons.ShortswordAffixes;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInfoDataType;
import pluginstudies.pluginstudies.CustomDataTypes.ModifierInformation;
import pluginstudies.pluginstudies.PluginStudies;

import java.util.*;

import static pluginstudies.pluginstudies.utils.Utils.color;
import static pluginstudies.pluginstudies.utils.Utils.log;

public abstract class CraftableItem {

    protected boolean identified;
    protected int ilvl;
    protected int rarity; // 0 = Common, 1 = Magic, 2 = Rare, TODO: 3 = Unique
    protected int affixLimit;
    protected Material material;
    protected String implicit = "";
    protected List<String> prefixes = new ArrayList<>();
    protected List<String> suffixes = new ArrayList<>();
    protected List<String> affixes = new ArrayList<>();
    protected List<Integer> affixTiers = new ArrayList<>();

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
        dataContainer.set(new NamespacedKey(plugin, "implicit"), PersistentDataType.STRING, implicit);
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, 0);

        itemMeta.setDisplayName(color("&f&lCommon item"));
        if (this instanceof CraftableWeapon){
//            log("DMG: " + ((CraftableWeapon) this).getBaseDmg()[0] + "/" + ((CraftableWeapon) this).getBaseDmg()[1]);
            dataContainer.set(new NamespacedKey(plugin, "type"), PersistentDataType.STRING, "WEAPON");
            dataContainer.set(new NamespacedKey(plugin, "weapon-type"), PersistentDataType.STRING, ((CraftableWeapon) this).getWeaponType());
            dataContainer.set(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY, ((CraftableWeapon) this).getBaseDmg());

            lore.add(color("&c DMG: " + ((CraftableWeapon) this).getBaseDmg()[0] + "-" + ((CraftableWeapon) this).getBaseDmg()[1]));
            lore.add(color("&7"));

            itemMeta.setDisplayName(color("&f&lCommon " + ((CraftableWeapon) this).getWeaponType().toLowerCase()));
        }
        if (this instanceof CraftableArmor){
            //do something
        }
        lore.add(color("&7    Ilvl: " + "&6&l" + ilvl + "&8&l    " + dataContainer.get(new NamespacedKey(plugin, "implicit"), PersistentDataType.STRING)));
        lore.add(color("&7&l___________________"));
//        lore.add(color("&7Item level: " + "&6&l" + ilvl)); //VERSÃO ANTIGA DE DISPLAY
//        lore.add(color("&7&l______" + "&7&l&n" + dataContainer.get(new NamespacedKey(plugin, "implicit"), PersistentDataType.STRING) +"&7&l______")); //toda arma/armor pode ter impl.

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
        dataContainer.set(new NamespacedKey(plugin, "implicit"), PersistentDataType.STRING, implicit);
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, modifiers);

        if (this instanceof CraftableWeapon){
            dataContainer.set(new NamespacedKey(plugin, "type"), PersistentDataType.STRING, "WEAPON");
            dataContainer.set(new NamespacedKey(plugin, "weapon-type"), PersistentDataType.STRING, ((CraftableWeapon) this).getWeaponType());
            dataContainer.set(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY, ((CraftableWeapon) this).getBaseDmg());
            itemMeta.setLore(Collections.singletonList(color(" &9&lMagic " + ((CraftableWeapon) this).getWeaponType().toLowerCase())));
        }
        if (this instanceof CraftableArmor){
            //do something
        }

        Map<String, int[]> mappedAffixes = selectTiers(selectAffixes(modifiers), dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING));

        ModifierInformation modData = new ModifierInformation(this.affixes, this.affixTiers, mappedAffixes);
        dataContainer.set(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType(), modData);

        itemMeta.setDisplayName(color("&4&lUnidentified"));
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
        dataContainer.set(new NamespacedKey(plugin, "implicit"), PersistentDataType.STRING, implicit); //TESTANDO IMPLEMENTAR IMPLICITOS
        dataContainer.set(new NamespacedKey(plugin, "modifiers"), PersistentDataType.INTEGER, modifiers);

        if (this instanceof CraftableWeapon){
            dataContainer.set(new NamespacedKey(plugin, "type"), PersistentDataType.STRING, "WEAPON");
            dataContainer.set(new NamespacedKey(plugin, "weapon-type"), PersistentDataType.STRING, ((CraftableWeapon) this).getWeaponType());
            dataContainer.set(new NamespacedKey(plugin, "base-dmg"), PersistentDataType.INTEGER_ARRAY, ((CraftableWeapon) this).getBaseDmg());
            itemMeta.setLore(Collections.singletonList(color(" &e&lRare " + ((CraftableWeapon) this).getWeaponType().toLowerCase())));
        }
        if (this instanceof CraftableArmor){
            //do something
        }

        Map<String, int[]> mappedAffixes = selectTiers(selectAffixes(modifiers), dataContainer.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING));

        ModifierInformation modData = new ModifierInformation(this.affixes, this.affixTiers, mappedAffixes);
        dataContainer.set(new NamespacedKey(plugin, "modifier-data"), new ModifierInfoDataType(), modData);

        itemMeta.setDisplayName(color("&4&lUnidentified"));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }
    private Map<String, int[]> selectTiers(List<String> selectedAffixes, String itemType){
        switch (itemType){
            case "WEAPON":
                String weaponType = ((CraftableWeapon) this).getWeaponType();
                List<String> sortedList;
                switch (weaponType){
                    case "AXE":
                        //Sorting axe affixes
                        List<String> AxeAffixList = new ArrayList<>();

                        String[] axePrefixes = AxeAffixes.PREFIXES.getAffixNameArray();
                        String[] axeSuffixes = AxeAffixes.SUFFIXES.getAffixNameArray();
                        AxeAffixList.addAll(Arrays.asList(axePrefixes));
                        AxeAffixList.addAll(Arrays.asList(axeSuffixes));

                        sortedList = sortAffixes(selectedAffixes, AxeAffixList);

                        //Tier selection
                        return generateTiers(sortedList, axePrefixes, AxeAffixes.PREFIXES, AxeAffixes.SUFFIXES);
                    case "POLEARM":
                        break;
                    case "SHORTSWORD":
                        //Sorting sword affixes
                        List<String> swordAffixList = new ArrayList<>();

                        String[] swordPrefixes = ShortswordAffixes.PREFIXES.getAffixNameArray();
                        String[] swordSuffixes = ShortswordAffixes.SUFFIXES.getAffixNameArray();
                        swordAffixList.addAll(Arrays.asList(swordPrefixes));
                        swordAffixList.addAll(Arrays.asList(swordSuffixes));

                        sortedList = sortAffixes(selectedAffixes, swordAffixList);

                        //Tier selection
                        return generateTiers(sortedList, swordPrefixes, ShortswordAffixes.PREFIXES, ShortswordAffixes.SUFFIXES);
                }
                break;
            case "ARMOR":
                break;
        }
        log("Unable to select affix tiers for this item");
        return null;
    }

    private <T extends Enum<T> & TableAcessInterface> Map<String, int[]> generateTiers(List<String> sortedList, String[] prefixArray, T genericPrefixMap, T genericSuffixMap){
        Map<String, int[]> tierMapping = new HashMap<>();
        List<Integer> tierList = new ArrayList<>();
        for (String affix : sortedList){
            Map<Integer, List<int[]>> currentAffixMap;

            if (Arrays.asList(prefixArray).contains(affix)){ // If prefix:
                //Get it on the prefix map
                currentAffixMap = genericPrefixMap.getAffix(affix);
            } else { // Its a suffix, do:
                currentAffixMap = genericSuffixMap.getAffix(affix);
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
            //Add that tier to the item tier list
            tierList.add(chosenTier);
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
        this.affixTiers = tierList;

        return tierMapping;
    }
    private List<String> sortAffixes(List<String> selectedAffixes, List<String> fullAffixList){

        //Comparing the 2 lists and creating a third one
        List<String> finalList = new ArrayList<>();
        for (String affix : fullAffixList){
            //Checking the full list and getting the right order
            if (selectedAffixes.contains(affix)){
                //If there's a match, append to the end of the final affix list
                finalList.add(affix);
            }
        }
        this.affixes = finalList;

        return finalList;
    }
    private List<String> selectAffixes(int modifiers){
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
            if (!hasOpenPrefix()){ //If no open prefix, get a suffix
                affix = getRandomSuffix(selectedAffixes);
                return affix;
            }//Get the prefix
            affix = getRandomPrefix(selectedAffixes);
        } else {
            //Suffix
            if (!hasOpenSuffix()){ //If no open suffix, get a prefix
                affix = getRandomPrefix(selectedAffixes);
                return affix;
            } //Get the suffix
            affix = getRandomSuffix(selectedAffixes);
        }
        return affix;
    }
    private <T extends Enum<T > & TableAcessInterface> String randomizeAffix(T affixTable, List<String> selectedAffixes){
        String chosenAffix = "";

        boolean found = false;
        while (!found){
            int rng = (int) (Math.random() * (affixTable.getAffixNameArray().length));
            String randomAffix = affixTable.getAffixNameArray()[rng];
            if (!(selectedAffixes.contains(randomAffix))){
                if (prefixCanBeSelected(randomAffix, affixTable)){
                    found = true;
                    chosenAffix = randomAffix;
                }
            }
        }
        return chosenAffix;
    }
    private String getRandomPrefix(List<String> selectedAffixes){
        String prefix = "";

        if (this instanceof CraftableWeapon){
            String weaponType = ((CraftableWeapon) this).getWeaponType();
            switch (weaponType){
                case "AXE":
                    prefix = randomizeAffix(AxeAffixes.PREFIXES, selectedAffixes);
                    this.prefixes.add(prefix);
                    break;
                case "SHORTSWORD":
                    prefix = randomizeAffix(ShortswordAffixes.PREFIXES, selectedAffixes);
                    this.prefixes.add(prefix);
                    break;
                case "POLEARM":
                    break;
            }
            return prefix;
        }
        if (this instanceof CraftableArmor){
            //do something
        }
        log("Could not generate random prefix");
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

        if (this instanceof CraftableWeapon){
            String weaponType = ((CraftableWeapon) this).getWeaponType();
            switch (weaponType){
                case "AXE":
                    suffix = randomizeAffix(AxeAffixes.SUFFIXES, selectedAffixes);
                    this.suffixes.add(suffix);
                    break;
                case "SHORTSWORD":
                    suffix = randomizeAffix(ShortswordAffixes.SUFFIXES, selectedAffixes);
                    this.suffixes.add(suffix);
                    break;
                case "POLEARM":
                    break;
            }
            return suffix;
        }
        if (this instanceof CraftableArmor){
            //do something
        }
        log("Could not generate random suffix");
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
    protected int matchItemLevel(List<Integer> ilvlRanges){
        if (ilvlRanges.get(0) > this.ilvl){
            log("Invalid suffix at current item level.");
            return -1;
        }
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
    private <T extends Enum<T> & TableAcessInterface> boolean prefixCanBeSelected(String prefix, T prefixTable){
        List<Integer> sortedLevelRanges = new ArrayList<>(prefixTable.getAffix(prefix).keySet());
        if (sortedLevelRanges.contains(this.ilvl)){
            return true;
        }
        sortedLevelRanges.add(this.ilvl);

        sortedLevelRanges.sort(Comparator.naturalOrder());
        return (this.ilvl >= sortedLevelRanges.get(1));
    }
    private <T extends Enum<T> & TableAcessInterface> boolean suffixCanBeSelected(String suffix, T suffixTable){
        List<Integer> sortedLevelRanges = new ArrayList<>(suffixTable.getAffix(suffix).keySet());

        if (sortedLevelRanges.contains(this.ilvl)){
            return true;
        }
        sortedLevelRanges.add(this.ilvl);

        sortedLevelRanges.sort(Comparator.naturalOrder());
        return (this.ilvl >= sortedLevelRanges.get(1));
    }
}