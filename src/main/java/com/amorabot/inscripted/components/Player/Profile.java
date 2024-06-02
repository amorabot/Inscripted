package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class Profile {
    private HealthComponent health;
    private DefenceComponent defences;
    private DamageComponent damage;
    @Getter
    private Attributes attributes;
    @Getter
    private Stats stats;
    private PlayerEquipment equipment;

    @Setter
    private Set<Keystones> keystones;

    public Profile(Attributes attributes, PlayerEquipment equipment){
        this.attributes = attributes;
        this.equipment = equipment;
    }
    public Profile(HealthComponent hp, DefenceComponent def, DamageComponent dmg, Attributes att, PlayerEquipment equipment){
        this.health = hp;
        this.defences = def;
        this.damage = dmg;
        this.attributes = att;
        this.stats = new Stats();
        this.equipment = equipment;
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
        StatCompiler compiler = new StatCompiler(profileID);
        compiler.updateProfile();

        Player player = Bukkit.getPlayer(profileID);
        assert player != null;
        mapPlayerHearts(player);
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

    public boolean mapPlayerHearts(Player player){
        assert player != null;
        player.setAbsorptionAmount(getHealthComponent().getMappedWard(20));
        double updatedHearts = getHealthComponent().getMappedHealth(20);
        player.setHealth(updatedHearts);
        return updatedHearts == 0;
    }

    public boolean hasKeystone(Keystones keystone){
        return this.keystones.contains(keystone);
    }
}
