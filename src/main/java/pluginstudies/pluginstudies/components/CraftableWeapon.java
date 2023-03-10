package pluginstudies.pluginstudies.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pluginstudies.pluginstudies.Crafting.ItemBaseImplicits;
import pluginstudies.pluginstudies.PluginStudies;

import java.util.*;

public class CraftableWeapon extends CraftableItem{

    private Map<Integer, int[]> baseDamageMap = new HashMap<>();
    private String weaponType;
    private int[] baseDmg = new int[2]; // vai conter valores do tipo {4, 15} como range de dano

    public CraftableWeapon(int ilvl, String weaponType){
        this.identified = false;
        if (ilvl > 0){
            this.ilvl = ilvl;
        } else {
            this.ilvl = 1;
        }
        this.weaponType = weaponType;
        switch (weaponType){
            case "AXE":
                implicit = ItemBaseImplicits.AXE.getBasicImplicit();
                pickAxeBaseType();
                mapAxeBases();
                break;
            case "POLEARM":
                implicit = ItemBaseImplicits.POLEARM.getBasicImplicit();
                break;
            case "SHORTSWORD":
                implicit = ItemBaseImplicits.SHORTSWORD.getBasicImplicit();
                pickSwordBaseType();
                mapSwordBases();
                break;
        }
    }

    public ItemStack generate(PluginStudies plugin, int rarity){
        setBaseDmg();
        return generateItem(plugin, rarity);
    }
    private void setBaseDmg(){
        Set<Integer> levelRanges = baseDamageMap.keySet();
        List<Integer> sortedLevelRanges = new ArrayList<>();
        sortedLevelRanges.addAll(levelRanges);
        sortedLevelRanges.sort(Comparator.naturalOrder());
        int match = matchItemLevel(sortedLevelRanges);
        baseDmg = baseDamageMap.get(match);
    }
    public int[] getBaseDmg(){
        return this.baseDmg;
    }
    public String getWeaponType(){
        return this.weaponType;
    }
    public void mapAxeBases(){
        baseDamageMap.put(1, new int[]{6,11});
        baseDamageMap.put(11, new int[]{11,21});
        baseDamageMap.put(30, new int[]{27,50});
        baseDamageMap.put(45, new int[]{35,65});
        baseDamageMap.put(60, new int[]{38,114});
        baseDamageMap.put(80, new int[]{60,150});
    }
    private void pickAxeBaseType(){
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
    public void mapSwordBases(){
        baseDamageMap.put(1, new int[]{4,9});
        baseDamageMap.put(11, new int[]{5,22});
        baseDamageMap.put(30, new int[]{15,21});
        baseDamageMap.put(45, new int[]{19,54});
        baseDamageMap.put(60, new int[]{36,58});
        baseDamageMap.put(80, new int[]{50,95});
    }
    private void pickSwordBaseType(){
        if (ilvl <= 10){
            this.material = Material.WOODEN_SWORD;
        } else if (ilvl <= 25) {
            this.material = Material.STONE_SWORD;
        } else if (ilvl <= 45) {
            this.material = Material.IRON_SWORD;
        } else if (ilvl <= 75) {
            this.material = Material.DIAMOND_SWORD;
        } else {
            this.material = Material.GOLDEN_SWORD;
        }
    }
}
