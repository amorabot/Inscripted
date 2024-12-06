package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.utils.Utils;

public class ItemSlotData {

    private int itemHash;
    private boolean ignore;


    public ItemSlotData(){
        this.itemHash = 0;
        this.ignore = true;
    }

    /*
    Return value: Signifies whether the stats for that item
    should be (re)compiled and stored into the PlayerEquipment class
    */
    public void setItemHash(Item newItemData){
        if (newItemData == null){
            this.ignore = true;
            return;
//            return false; // No recompilation
        }
        if (this.itemHash == 0){
            this.itemHash = newItemData.hashCode();
            this.ignore = false;
            return;
//            return true;
        }
        int newHash = newItemData.hashCode();
        if (newHash == itemHash){ //Its the same item, but its already been cached
            Utils.log("New equipped item's hash matches cached hash, should not be ignored anymore!");
            if (ignore){ //If its already cached but set to ignore, simply dont ignore it anymore
                this.ignore = false;
            }
//            return false; // No recompilation (The cached item hasnt changed)
        } else { //If the new item's hash is different, let's simply store it
            this.itemHash = newHash;
            this.ignore = false;
//            return true; // And recompile the stat data
        }
    }

    public int getItemHash(){
        if (ignore){
            return 0;
        }
        return this.itemHash;
    }

    public boolean isIgnorable(){
        return this.ignore;
    }
}
