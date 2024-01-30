package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.Items.Abstract.Item;

public class EquipmentSlot {

    private Item itemData;
    private boolean ignore;


    public EquipmentSlot(){
        resetSlot();
    }

    public void resetSlot(){
        this.itemData = null;
        this.ignore = true;
    }

    public void setItemData(Item newItemData){
        if (this.itemData == null){
            this.itemData = newItemData;
            this.ignore = false;
        }
        if (newItemData == null){
            this.ignore = true;
            return;
        }
        if (newItemData.hashCode() == itemData.hashCode()){ //Its the same item, but its already cached from previously
            if (ignore){ //If its already cached but set to ignore, simply dont ignore it anymore
                this.ignore = false;
            }
        } else { //If the new item's hash is different, lets store it
            this.itemData = newItemData;
            this.ignore = false;
        }
    }

    public Item getItemData(){
        if (ignore){
            return null;
        }
        return this.itemData;
    }

    public boolean isIgnorable(){
        return this.ignore;
    }
}
