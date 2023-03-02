package pluginstudies.pluginstudies.components;

import org.bukkit.Material;

public class CraftableWeapon extends CraftableItem{
    private int[] baseDmg = new int[2]; // vai conter valores do tipo {4, 15} como range de dano

    public CraftableWeapon(int ilvl, int rarity){
        this.identified = false;
        this.ilvl = ilvl;
        this.rarity = rarity;
        this.itemType = Material.WOODEN_AXE;
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
}
