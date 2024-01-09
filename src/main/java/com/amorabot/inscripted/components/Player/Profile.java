package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.utils.Utils;

import java.util.UUID;


public class Profile {
    private HealthComponent health;
    private DefenceComponent defences;
    private DamageComponent damage;
    private Attributes attributes;
    private Miscellaneous miscellaneous;
    private PlayerEquipment equipment;
    public Profile(Attributes attributes, PlayerEquipment equipment){
        this.attributes = attributes;
        this.equipment = equipment;
    }
    public Profile(HealthComponent hp, DefenceComponent def, DamageComponent dmg, Attributes att, PlayerEquipment equipment){
        this.health = hp;
        this.defences = def;
        this.damage = dmg;
        this.attributes = att;
        this.miscellaneous = new Miscellaneous();
        this.equipment = equipment;
    }
    public Attributes getAttributes(){
        return this.attributes;
    }
    public Miscellaneous getMiscellaneous() {
        return miscellaneous;
    }
    public PlayerEquipment getEquipmentComponent() {
        return this.equipment;
    }
    public HealthComponent getHealthComponent(){
        return this.health;
    }
    public DefenceComponent getDefenceComponent(){
        return this.defences;
    }
    public DamageComponent getDamageComponent(){
        return this.damage;
    }
    private void updateProfile(UUID profileID){
        //Todo: on every profile update, re-map the players hp
        //player.setHealth(playerProfile.getHealthComponent().getMappedHealth(20));
        //and remove other occurences (make it a static method with player/living entity arg on HealthComponent

        StatCompiler compiler = new StatCompiler(profileID);
        compiler.updateProfile();
    }

    public boolean updateEquipmentSlot(ItemTypes targetSlot, Item itemData, UUID profileID){
        boolean success = getEquipmentComponent().setSlot(targetSlot, itemData);
        if (success){
            //Only call for profile updates after successful calls
            updateProfile(profileID);
        } else {
            Utils.error("Unsuccessful slot equipment call (at Profile class)");
        }
        return success;
    }
    public boolean hasWeaponEquipped(){
        return getEquipmentComponent().getWeaponData() != null;
    }
}
