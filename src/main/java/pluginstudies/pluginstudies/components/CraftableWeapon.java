package pluginstudies.pluginstudies.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
                pickAxeBaseType();
                mapAxeBases();
                break;
            case "POLEARM":
                this.material = Material.STONE_SHOVEL;
                break;
            case "SHORTSWORD":
                this.material = Material.WOODEN_SWORD;
                break;
        }
    }

    public ItemStack generate(PluginStudies plugin, int rarity){
        setBaseDmg();
//        log("dano base é: " + baseDmg[0] + "/" + baseDmg[1]);
        return generateItem(plugin, rarity);
    }
    public void mapAxeBases(){
        baseDamageMap.put(1, new int[]{6,11});
        baseDamageMap.put(11, new int[]{11,21});
        baseDamageMap.put(30, new int[]{27,50});
        baseDamageMap.put(45, new int[]{35,65});
        baseDamageMap.put(60, new int[]{38,114});
        baseDamageMap.put(80, new int[]{60,150});
    }
    private void setBaseDmg(){
        Set<Integer> levelRanges = baseDamageMap.keySet();
        int match = matchItemLevel(levelRanges);
        baseDmg = baseDamageMap.get(match);
    }
    public int[] getBaseDmg(){
        return this.baseDmg;
    }
    public String getWeaponType(){
        return this.weaponType;
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
}
