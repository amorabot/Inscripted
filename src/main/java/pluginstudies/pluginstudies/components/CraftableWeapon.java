package pluginstudies.pluginstudies.components;

import org.bukkit.Material;

public class CraftableWeapon extends CraftableItem{
    private int[] baseDmg = new int[2]; // vai conter valores do tipo {4, 15} como range de dano

    public CraftableWeapon(int ilvl){
        this.identified = false;
        this.ilvl = ilvl;
        this.itemType = Material.WOODEN_AXE;
    }
}
