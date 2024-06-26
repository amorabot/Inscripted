package com.amorabot.inscripted.skills;

import com.amorabot.inscripted.APIs.SoundAPI;
import com.amorabot.inscripted.components.Items.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.managers.PlayerPassivesManager;
import com.amorabot.inscripted.skills.item.ItemPassiveAbilities;
import lombok.Getter;
import org.bukkit.entity.Player;

import static com.amorabot.inscripted.skills.axe.AxeBasicAttacks.newAxeBasicAttackBy;
import static com.amorabot.inscripted.skills.axe.AxeMovement.marauderMovement;
import static com.amorabot.inscripted.skills.bow.BowBasicAttacks.bowBasicAttackBy;
import static com.amorabot.inscripted.skills.bow.BowMovement.mercenaryMovement;
import static com.amorabot.inscripted.skills.dagger.DaggerBasicAttacks.newDaggerBasicAttackBy;
import static com.amorabot.inscripted.skills.dagger.DaggerMovement.rogueMovement;
import static com.amorabot.inscripted.skills.mace.MaceBasicAttacks.newMaceBasicAttackFor;
import static com.amorabot.inscripted.skills.mace.MaceMovement.templarMovement;
import static com.amorabot.inscripted.skills.sword.SwordBasicAttacks.newSwordBasicAttackBy;
import static com.amorabot.inscripted.skills.sword.SwordMovement.gladiatorMovement;
import static com.amorabot.inscripted.skills.wand.WandBasicAttacks.wandBasicAttackBy;
import static com.amorabot.inscripted.skills.wand.WandMovement.sorcererMovement;

@Getter
public enum PlayerAbilities {
    BASIC_AXE_SLASH(new int[5], new float[]{1.1F,1,1,1,1},0, AbilityTypes.BASIC_ATTACK, HitTypes.MELEE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            //Extra skill flare goes here
            newAxeBasicAttackBy(caster, this);
        }
    },
    BASIC_SWORD_SLASH(new int[5], new float[]{1,1,1,1,1},0, AbilityTypes.BASIC_ATTACK, HitTypes.MELEE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            newSwordBasicAttackBy(caster, this);
        }
    },
    BASIC_BOW_ATTACK(new int[5], new float[]{1,1,1,1,1},0, AbilityTypes.BASIC_ATTACK, HitTypes.PROJECTILE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            bowBasicAttackBy(caster, this);
        }
    },
    BASIC_DAGGER_SLASH(new int[5], new float[]{1,1,1,1,1},0, AbilityTypes.BASIC_ATTACK, HitTypes.MELEE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            newDaggerBasicAttackBy(caster, this);
        }
    },
    BASIC_WAND_ATTACK(new int[5], new float[]{1,1,1,1,1},0,AbilityTypes.BASIC_ATTACK, HitTypes.PROJECTILE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            wandBasicAttackBy(caster, this);
        }
    },
    BASIC_MACE_SLAM(new int[5], new float[]{1,1,1,1,1},0,AbilityTypes.BASIC_ATTACK, HitTypes.MELEE,HitTypes.AOE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            newMaceBasicAttackFor(caster, this);
        }
    },


    //TODO: when abilityCDR is implemented, calculate the resulting cd modifier within a #getCDR(Profile, AtkSpeed)

    CHARGE(null, null, 10,
            AbilityTypes.MOVEMENT,
            HitTypes.NONE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
                marauderMovement(caster);
                return;
            }
            PlayerAbilities.invalidAbilityCast(caster);
        }
    },
    LEAP(null, null, 3,
            AbilityTypes.MOVEMENT,
            HitTypes.NONE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
                gladiatorMovement(caster);
                return;
            }
            PlayerAbilities.invalidAbilityCast(caster);
        }
    },
    ACROBATICS(null, null, 5,
            AbilityTypes.MOVEMENT,
            HitTypes.NONE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
                mercenaryMovement(caster);
                return;
            }
            PlayerAbilities.invalidAbilityCast(caster);
        }
    },
    VANISH(null, null, 12,
            AbilityTypes.MOVEMENT,
            HitTypes.NONE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
                rogueMovement(caster);
                return;
            }
            PlayerAbilities.invalidAbilityCast(caster);
        }
    },
    BLINK(null, null, 7,
            AbilityTypes.MOVEMENT,
            HitTypes.NONE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
                sorcererMovement(caster);
                return;
            }
            PlayerAbilities.invalidAbilityCast(caster);
        }
    },
    TECTONIC_PULL(null, null, 7,
            AbilityTypes.MOVEMENT,
            HitTypes.NONE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
                templarMovement(caster);
                return;
            }
            PlayerAbilities.invalidAbilityCast(caster);
        }
    },





    PERMAFROST_PASSIVE(null, null, 0,
            AbilityTypes.ITEM,
            HitTypes.SPELL,HitTypes.AOE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            int period = 60; //Ticks
            int animationSteps = 3;
            int taskID = ItemPassiveAbilities.activatePermafrost(caster, period, animationSteps);
            PlayerPassivesManager.addKeystonePassive(caster.getUniqueId(), Keystones.PERMAFROST, taskID);
        }
    },
    THUNDERSTRUCK_PASSIVE(new int[]{0,0,50,0,0}, new float[]{0,0,0.15F,0,0}, 0,
            AbilityTypes.ITEM,
            HitTypes.SPELL,HitTypes.AOE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            int period = 30; //Ticks
            int taskID = ItemPassiveAbilities.activateThunderstruck(caster, period);
            PlayerPassivesManager.addKeystonePassive(caster.getUniqueId(), Keystones.THUNDERSTRUCK, taskID);
        }
    },
    WINDS_OF_CHANGE_PASSIVE(new int[5], new float[5], 0,
            AbilityTypes.ITEM,
            HitTypes.SPELL,HitTypes.NONE) {
        @Override
        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
            int period = 20*60; //Ticks
            int taskID = ItemPassiveAbilities.activateWindsOfChangeFor(caster, period);
            PlayerPassivesManager.addKeystonePassive(caster.getUniqueId(), Keystones.WINDS_OF_CHANGE, taskID);
        }
    };


    private final AbilityTypes type;
    private final HitTypes[] tags;
    private final int[] abilityBaseDamage;
    private final float[] multipliers;
    private final long cooldownInSeconds;
    //Energy cost
    //Energy cost multiplier

    PlayerAbilities(int[] baseDamage, float[] multipliers, long cooldownInSeconds, AbilityTypes type, HitTypes... tags){
        this.type = type;
        this.tags = tags;
        this.abilityBaseDamage = baseDamage;
        this.multipliers = multipliers;
        this.cooldownInSeconds = cooldownInSeconds;
    }

    public abstract void cast(Player caster, WeaponAttackSpeeds attackSpeed);

    public int[] scaleDamage(int[] baseDamage){
        int[] scaledDamage = baseDamage.clone();
        for (int i = 0; i < baseDamage.length; i++){
            scaledDamage[i] = (int) ((scaledDamage[i]) * multipliers[i]) + abilityBaseDamage[i];
        }
        return scaledDamage;
    }

    public static PlayerAbilities mapBaseAbility(WeaponTypes weapon, AbilityTypes type, int variant){
        switch (weapon){
            case AXE -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        //Implement variants later
                        return BASIC_AXE_SLASH;
                    }
                    case MOVEMENT -> {
                        return CHARGE;
                    }
                }
            }
            case SWORD -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_SWORD_SLASH;
                    }
                    case MOVEMENT -> {
                        return LEAP;
                    }
                }
            }
            case BOW -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_BOW_ATTACK;
                    }
                    case MOVEMENT -> {
                        return ACROBATICS;
                    }
                }
            }
            case DAGGER -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_DAGGER_SLASH;
                    }
                    case MOVEMENT -> {
                        return VANISH;
                    }
                }
            }
            case WAND -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_WAND_ATTACK;
                    }
                    case MOVEMENT -> {
                        return BLINK;
                    }
                }
            }
            case MACE -> {
                switch (type){
                    case BASIC_ATTACK -> {
                        return BASIC_MACE_SLAM;
                    }
                    case MOVEMENT -> {
                        return TECTONIC_PULL;
                    }
                }
            }
        }
        //If there's no match, return null;
        return null;
    }

    private static void invalidAbilityCast(Player caster){
        SoundAPI.playGenericSoundAtLocation(caster, caster.getLocation(), "block.note_block.basedrum", 0.9F, 1.0F);
    }

}
