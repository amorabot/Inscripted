//package com.amorabot.inscripted.skill;
//
//import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
//import lombok.Getter;
//import net.kyori.adventure.text.Component;
//import org.bukkit.entity.Player;
//
//
//@Getter
//public enum PlayerAbilities {
//    FIST(new int[5], new float[]{1F,1,1,1,1},0, AbilityTypes.NEUTRAL, AbilityTags.MELEE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
//            caster.sendMessage(Component.text("casTING a fist"));
//        }
//    },
//
//    BASIC_AXE_SLASH(new int[5], new float[]{1.1F,1,1,1,1},0, AbilityTypes.BASIC_ATTACK, AbilityTags.MELEE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            standardAxeSlashBy(caster, this);
//        }
//    },
//    BASIC_SWORD_SLASH(new int[5], new float[]{1,1,1,1,1},0, AbilityTypes.BASIC_ATTACK, AbilityTags.MELEE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            standardSwordSlashBy(caster,this);
//        }
//    },
//    BASIC_BOW_ATTACK(new int[5], new float[]{1,1,1,1,1},0, AbilityTypes.BASIC_ATTACK, AbilityTags.PROJECTILE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            standardBowAttackBy(caster, this, SteeringBehaviors.STRAIGHT_LINE, 1);
//        }
//    },
//    BASIC_DAGGER_SLASH(new int[5], new float[]{1,1,1,1,1},0, AbilityTypes.BASIC_ATTACK, AbilityTags.MELEE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            standardDaggerSlashBy(caster,this);
//        }
//    },
//    BASIC_WAND_ATTACK(new int[5], new float[]{1,1,1,1,1},0,AbilityTypes.BASIC_ATTACK, AbilityTags.PROJECTILE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            standardWandAttackBy(caster, this, SteeringBehaviors.ARRIVE, 1);
//        }
//    },
//    BASIC_MACE_SLAM(new int[5], new float[]{1,1,1,1,1},0,AbilityTypes.BASIC_ATTACK, AbilityTags.MELEE, AbilityTags.AOE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            standardMaceSlamBy(caster,this);
////            newMaceBasicAttackFor(caster, this);
//        }
//    },
//
//
//    //TODO: when abilityCDR is implemented, calculate the resulting cd modifier within a #getCDR(Profile, AtkSpeed)
//
//    CHARGE(null, null, 10,
//            AbilityTypes.MOVEMENT,
//            AbilityTags.NONE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
////                marauderMovement(caster);
////                return;
////            }
////            PlayerAbilities.invalidAbilityCast(caster);
//        }
//    },
//    LEAP(null, null, 3,
//            AbilityTypes.MOVEMENT,
//            AbilityTags.NONE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
////                gladiatorMovement(caster);
////                return;
////            }
////            PlayerAbilities.invalidAbilityCast(caster);
//        }
//    },
//    ACROBATICS(null, null, 5,
//            AbilityTypes.MOVEMENT,
//            AbilityTags.NONE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
////                mercenaryMovement(caster);
////                return;
////            }
////            PlayerAbilities.invalidAbilityCast(caster);
//        }
//    },
//    VANISH(null, null, 12,
//            AbilityTypes.MOVEMENT,
//            AbilityTags.NONE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
////                rogueMovement(caster);
////                return;
////            }
////            PlayerAbilities.invalidAbilityCast(caster);
//        }
//    },
//    BLINK(null, null, 7,
//            AbilityTypes.MOVEMENT,
//            AbilityTags.NONE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
////                sorcererMovement(caster);
////                return;
////            }
////            PlayerAbilities.invalidAbilityCast(caster);
//        }
//    },
//    TECTONIC_PULL(null, null, 7,
//            AbilityTypes.MOVEMENT,
//            AbilityTags.NONE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
////            if (GlobalCooldownManager.skillcastBy(caster.getUniqueId(),this, attackSpeed.getAbilityCooldownModifier())){
////                templarMovement(caster);
////                return;
////            }
////            PlayerAbilities.invalidAbilityCast(caster);
//        }
//    },
//
//
//
//
//
//    PERMAFROST_PASSIVE(null, null, 0,
//            AbilityTypes.ITEM,
//            AbilityTags.SPELL, AbilityTags.AOE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
//            int period = 60; //Ticks
//            int animationSteps = 3;
////            int taskID = ItemAuras.activatePermafrost(caster, period, animationSteps);
////            PlayerPassivesManager.addKeystonePassive(caster.getUniqueId(), Keystones.PERMAFROST, taskID);
//        }
//    },
//    THUNDERSTRUCK_PASSIVE(new int[]{0,0,50,0,0}, new float[]{0,0,0.15F,0,0}, 0,
//            AbilityTypes.ITEM,
//            AbilityTags.SPELL, AbilityTags.AOE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
//            int period = 30; //Ticks
////            int taskID = ItemAuras.activateThunderstruck(caster, period);
////            PlayerPassivesManager.addKeystonePassive(caster.getUniqueId(), Keystones.THUNDERSTRUCK, taskID);
//        }
//    },
//    WINDS_OF_CHANGE_PASSIVE(new int[5], new float[5], 0,
//            AbilityTypes.ITEM,
//            AbilityTags.SPELL, AbilityTags.NONE) {
//        @Override
//        public void cast(Player caster, WeaponAttackSpeeds attackSpeed) {
//            int period = 20*60; //Ticks
////            int taskID = ItemAuras.activateWindsOfChangeFor(caster, period);
////            PlayerPassivesManager.addKeystonePassive(caster.getUniqueId(), Keystones.WINDS_OF_CHANGE, taskID);
//        }
//    };
//
//
//    //TODO: turn into a config file
//    private final AbilityTypes type;
//    private final AbilityTags[] tags;
//    private final int[] abilityBaseDamage;
//    private final float[] multipliers;
//    private final long cooldownInSeconds;
//    //Energy cost
//    //Energy cost multiplier
//
//    PlayerAbilities(int[] baseDamage, float[] multipliers, long cooldownInSeconds, AbilityTypes type, AbilityTags... tags){
//        this.type = type;
//        this.tags = tags;
//        this.abilityBaseDamage = baseDamage;
//        this.multipliers = multipliers;
//        this.cooldownInSeconds = cooldownInSeconds;
//    }
//
//    public abstract void cast(Player caster, WeaponAttackSpeeds attackSpeed);
//
//    public int[] scaleDamage(int[] baseDamage){
//        int[] scaledDamage = baseDamage.clone();
//        for (int i = 0; i < baseDamage.length; i++){
//            scaledDamage[i] = (int) ((scaledDamage[i]) * multipliers[i]) + abilityBaseDamage[i];
//        }
//        return scaledDamage;
//    }
//
////    public static PlayerAbilities mapBaseAbility(WeaponTypes weapon, AbilityTypes type, int variant){
////        switch (weapon){
////            case AXE -> {
////                switch (type){
////                    case BASIC_ATTACK -> {
////                        //Implement variants later
////                        return BASIC_AXE_SLASH;
////                    }
////                    case MOVEMENT -> {
////                        return CHARGE;
////                    }
////                }
////            }
////            case SWORD -> {
////                switch (type){
////                    case BASIC_ATTACK -> {
////                        return BASIC_SWORD_SLASH;
////                    }
////                    case MOVEMENT -> {
////                        return LEAP;
////                    }
////                }
////            }
////            case BOW -> {
////                switch (type){
////                    case BASIC_ATTACK -> {
////                        return BASIC_BOW_ATTACK;
////                    }
////                    case MOVEMENT -> {
////                        return ACROBATICS;
////                    }
////                }
////            }
////            case DAGGER -> {
////                switch (type){
////                    case BASIC_ATTACK -> {
////                        return BASIC_DAGGER_SLASH;
////                    }
////                    case MOVEMENT -> {
////                        return VANISH;
////                    }
////                }
////            }
////            case WAND -> {
////                switch (type){
////                    case BASIC_ATTACK -> {
////                        return BASIC_WAND_ATTACK;
////                    }
////                    case MOVEMENT -> {
////                        return BLINK;
////                    }
////                }
////            }
////            case MACE -> {
////                switch (type){
////                    case BASIC_ATTACK -> {
////                        return BASIC_MACE_SLAM;
////                    }
////                    case MOVEMENT -> {
////                        return TECTONIC_PULL;
////                    }
////                }
////            }
////        }
////        //If there's no match, return null;
////        return null;
////    }
//
//
//
//}
