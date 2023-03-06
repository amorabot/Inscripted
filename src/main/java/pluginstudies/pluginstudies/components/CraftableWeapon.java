package pluginstudies.pluginstudies.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pluginstudies.pluginstudies.PluginStudies;

import java.util.*;

import static pluginstudies.pluginstudies.utils.Utils.log;

public class CraftableWeapon extends CraftableItem{

    private Map<Integer, int[]> baseDamageMap = new HashMap<>();
    private int[] baseDmg = new int[2]; // vai conter valores do tipo {4, 15} como range de dano

    public CraftableWeapon(int ilvl, String weaponType){
        this.identified = false;
        if (ilvl > 0){
            this.ilvl = ilvl;
        } else {
            this.ilvl = 1;
        }
        switch (weaponType){
            case "AXE":
                this.itemType = Material.WOODEN_AXE;
                mapAxeBases();
                break;
            case "POLEARM":
                this.itemType = Material.STONE_SHOVEL;
                break;
            case "SHORTSWORD":
                this.itemType = Material.WOODEN_SWORD;
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
    private int matchItemLevel(Set<Integer> ilvlRanges){
        int match = 0;
        List<Integer> list = new ArrayList<>();
        for (int ilvl : ilvlRanges){
            list.add(ilvl);
        }
        if (!(list.contains(this.ilvl))){
            list.add(this.ilvl);
        } else { //é um match
            match = this.ilvl;
            return match;
        }
        list.sort(Comparator.naturalOrder()); //ordenando a lista
        match = list.get(list.indexOf(this.ilvl)-1);
        return match;
    }
    public int[] getBaseDmg(){
        return this.baseDmg;
    }
}
