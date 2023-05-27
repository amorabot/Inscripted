package pluginstudies.pluginstudies.components.CraftingComponents.Items;

import org.bukkit.Material;
import pluginstudies.pluginstudies.Crafting.Interfaces.AffixTableAcessInterface;
import pluginstudies.pluginstudies.Crafting.Weapons.Enums.DamageTypes;
import pluginstudies.pluginstudies.Crafting.Weapons.Modifiers.AxeMods;
import pluginstudies.pluginstudies.Crafting.Weapons.Modifiers.WeaponModifiers;
import pluginstudies.pluginstudies.Crafting.Weapons.Enums.WeaponNames;
import pluginstudies.pluginstudies.Crafting.Weapons.Enums.WeaponTypes;
import pluginstudies.pluginstudies.CustomDataTypes.RPGElementsContainer;

import java.io.Serializable;
import java.util.*;

import static pluginstudies.pluginstudies.Crafting.CraftingUtils.getRandomNumber;
import static pluginstudies.pluginstudies.utils.Utils.log;

public class Weapon implements Serializable, RPGElementsContainer {

    private static final long serialversionUID = 100069L;

    private String name;
    private Material material;
    private Map<DamageTypes, int[]> baseDmg = new HashMap<>();
    private Map<WeaponModifiers, Map<Integer, int[]>> modifierTierMap = new HashMap<>();

    public Weapon(WeaponTypes type, List<WeaponModifiers> prefixes, List<WeaponModifiers> suffixes, int ilvl){
        switch (type){
            case AXE:
                generateTiers(AxeMods.PREFIXES, prefixes, AxeMods.SUFFIXES, suffixes, ilvl);
                getName(type);
                defineItemBase(type, ilvl);
                break;
            case SHORTSWORD:
                break;
        }
    }
    public <E extends Enum<E> & AffixTableAcessInterface> void generateTiers(E prefixTable, List<WeaponModifiers> prefixes, E suffixTable, List<WeaponModifiers> suffixes, int ilvl){
//        Map<Integer, int[]> affixMap = new HashMap<>();

        generateAffixTiers(prefixTable, prefixes, ilvl);
        generateAffixTiers(suffixTable, suffixes, ilvl);

//        return affixMap;
    }
    public <E extends Enum<E> & AffixTableAcessInterface> void generateAffixTiers(E modTable, List<WeaponModifiers> affixes, int ilvl){
//        log("chamando affixgeneration");
//        for (WeaponModifiers affix : affixes){
//            log(affix.toString());
//        }

        for (WeaponModifiers affix : affixes){
            Map<Integer, int[]> affixMap = new HashMap<>(); //map containing tier -> values  for each affix ( 1, [3, 8])

            //--------------SORTING TIERS--------------------
            Map<Integer, int[]> tierMapping = modTable.getModTable(affix); //Getting this affix tier/lvl mapping (11, [2,3,  6,9])
            List<Integer> sortedTierMapping = new ArrayList<>(tierMapping.keySet()); //List of all ilvls that indicate a tier bracket

            sortedTierMapping.sort(Comparator.naturalOrder()); //Sorting the entire set
            int maxTier = getMaximumTier(sortedTierMapping, ilvl);

            int chosenTier = getRandomNumber(0, maxTier); //Tier that will be the key of affixMap
            int tierMapKey = sortedTierMapping.get(chosenTier); //Ilvl in the map corresponding to the chosen tier

            if (affix.hasSingleRange()){ // [#, @]
                int[] tierValues = tierMapping.get(tierMapKey); //Accessing the values associated with the chose tier
                int finalValue = getRandomNumber(tierValues[0], tierValues[1]); //Generating a final set of values based on it
                affixMap.put(chosenTier, new int[]{finalValue}); // Tier, [value1, value2]

                log(affix.toString());
                log(""+chosenTier);
                log(String.valueOf(finalValue));
                modifierTierMap.put(affix, affixMap);
            } else { // [#, @,  !, $]
                int[] tierValues = tierMapping.get(tierMapKey);
                int[] finalValues = new int[2]; //Values associated with that tier
                finalValues[0] = getRandomNumber(tierValues[0], tierValues[1]);
                finalValues[1] = getRandomNumber(tierValues[2], tierValues[3]);
                affixMap.put(chosenTier, finalValues);

                log(affix.toString());
                log(""+chosenTier);
                log(String.valueOf(finalValues[0]), String.valueOf(finalValues[1]));
                modifierTierMap.put(affix, affixMap);
            }
        }
    }

    public int getMaximumTier(List<Integer> sortedIlvlKeySet, int ilvl){
        if (sortedIlvlKeySet.get(0) > ilvl){ //Se o ilvl mais baixo para o mod for maior que ilvl do item
            log("Invalid affix at current item level.");
            return -1;
            //TODO: tratamento de affix indevido para o item level baixo
        }
        List<Integer> aux = new ArrayList<>(sortedIlvlKeySet);

        if (!(aux.contains(ilvl))){ //Se a lista não tiver o item level desejado, adicione
            aux.add(ilvl);
        } else { //Senão, já temos o número desejado
            aux.sort(Comparator.naturalOrder());
            return aux.indexOf(ilvl);
        }

        aux.sort(Comparator.naturalOrder()); //ordenando a lista após a adição

        if (aux.indexOf(ilvl) != 0){ //Se o ilvl não for o primeiro da lista
            return aux.indexOf(ilvl)-1; //retorne o item antes dele (maior tier alcançável)
        }
        return 0; //Se for o ultimo retorne o tier mais basico
    }

    public void getName(WeaponTypes type){
        WeaponNames names = WeaponNames.valueOf(type.toString());
        this.name = names.getRandomName();
    }
    private void defineItemBase(WeaponTypes type, int ilvl){
        switch (type){
            case AXE:
                mapAxeDmg(ilvl);
                mapAxeBase(ilvl);
                break;
            case SHORTSWORD:
                break;
        }
    }
    // -------------AXE-----------------------
    public void mapAxeDmg(int ilvl){
        if (ilvl > 0 && ilvl <= 11){
            baseDmg.put(DamageTypes.PHYSICAL, new int[]{6,11});
        } else if (ilvl <= 30){
            baseDmg.put(DamageTypes.PHYSICAL, new int[]{11,21});
        } else if (ilvl <= 45){
            baseDmg.put(DamageTypes.PHYSICAL, new int[]{27,50});
        } else if (ilvl <= 60){
            baseDmg.put(DamageTypes.PHYSICAL, new int[]{35,65});
        } else if (ilvl <= 80){
            baseDmg.put(DamageTypes.PHYSICAL, new int[]{38,114});
        } else {
            baseDmg.put(DamageTypes.PHYSICAL, new int[]{60,150});
        }
        Set<WeaponModifiers> mods = modifierTierMap.keySet();
        for (WeaponModifiers mod : mods){
            switch (mod){
                case ADDED_FIRE:
                    for (int[] value : modifierTierMap.get(mod).values()){
                        baseDmg.put(DamageTypes.FIRE, value);
                    }
                    break;
                case ADDED_COLD:
                    break;
                case ADDED_LIGHTNING:
                    break;
                case ADDED_ABYSSAL:
                    for (int[] value : modifierTierMap.get(mod).values()){
                        baseDmg.put(DamageTypes.ABYSSAL, value);
                    }
                    break;
            }
        }
    }
    private void mapAxeBase(int ilvl){
        if (ilvl <= 10){
            this.material = Material.WOODEN_AXE;
        } else if (ilvl <= 25) {
            this.material = Material.STONE_AXE;
        } else if (ilvl <= 45) {
            this.material = Material.IRON_AXE;
        } else if (ilvl <= 75) {
            this.material = Material.DIAMOND_AXE;
        } else {
            this.material = Material.GOLDEN_AXE;
        }
    }
    // ---------------------------------------
    public Material getItemMaterial(){
        return this.material;
    }
    public String getName(){
        return name;
    }
    public Map<DamageTypes, int[]> getBaseDmg(){
        return baseDmg;
    }
    public Map<WeaponModifiers, Map<Integer, int[]>> getModifierTierMap() {
        return modifierTierMap;
    }
}
