package com.amorabot.inscripted.components.Player;

import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTimes;
import com.amorabot.inscripted.components.Items.modifiers.unique.TriggerTypes;
import com.amorabot.inscripted.components.Player.stats.PlayerEquipment;
import com.amorabot.inscripted.components.Player.stats.StatCompiler;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class Profile {
    //TODO: Make a getAsTextComponent() for all profile components, to summarize player data
    private HealthComponent health;
    private DefenceComponent defences;
    private DamageComponent damage;
    @Getter
    private Attributes attributes;
    @Getter
    private StatsComponent statsComponent;
    private PlayerEquipment equipment;

    @Getter
    @Setter
    private Set<Keystones> keystones = new HashSet<>();
    @Getter
    @Setter
    private Set<Effects> effects = new HashSet<>();

    public Profile(Attributes attributes, PlayerEquipment equipment){
        this.attributes = attributes;
        this.equipment = equipment;
    }
    public Profile(HealthComponent hp, DefenceComponent def, DamageComponent dmg, Attributes att, PlayerEquipment equipment){
        this.health = hp;
        this.defences = def;
        this.damage = dmg;
        this.attributes = att;
        this.statsComponent = new StatsComponent();
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
        StatCompiler.updateProfile(profileID);

        Player player = Bukkit.getPlayer(profileID);
        assert player != null;
        HealthComponent.updateHeartContainers(player,getHealthComponent());
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
        PlayerEquipment playerEquipment = getEquipmentComponent();
        ItemSlotData weaponSlot = playerEquipment.getSlot(ItemTypes.WEAPON);
        return ((weaponSlot.getItemHash()!=0) && (!weaponSlot.isIgnorable()));
    }

    public boolean hasKeystone(Keystones keystone){
        return keystones.contains(keystone);
    }
    public void notify(TriggerTypes effectTrigger, TriggerTimes timing, LivingEntity caster, LivingEntity target, int[] hit){
        for (Effects effect : this.effects){
            if (!effect.getTrigger().equals(effectTrigger) || !effect.getTiming().equals(timing)){continue;} //If there isn't a match: ignore
            Utils.log("Checking "+effectTrigger+" effects. Current: " + effect);
            effect.check(caster,target, hit);
        }
    }
}
