package com.amorabot.inscripted.skill.type;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.item.structure.Weapon.RangeCategory;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.player.profile.parsing.StatPool;
import com.amorabot.inscripted.skill.casting.CastSource;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

@Getter
public abstract class Attack extends Skillcast.Simple {
    protected final AttackData attackData;

    public Attack(UUID playerID, Skills skillUsed, CastSource castSource,WeaponAttackSpeeds weaponSpeed) {
        super(playerID, skillUsed, castSource,weaponSpeed);
        if (!skillUsed.isAttackSkill()){
            Utils.error("Invalid base attack skill (" + skillUsed.name() + "). Attack configuration not set.");
            this.attackData = null;
            return;
        }
        this.attackData = new AttackData(playerID,skillUsed,PlayerDataContainer.getDataContainerFor(playerID).getGlobalStats());
    }


    public static class Basic extends Attack{
        private final double itemUsageCD;
        private final PotionEffect swingEffect;

        public Basic(UUID playerID, Skills skillUsed, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
            super(playerID, skillUsed, castSource, weaponSpeed);
            this.itemUsageCD = weaponSpeed.getItemUsageCooldown();
            this.swingEffect = weaponSpeed.getSwingAnimationBuff();
        }

        @Override
        public void register(){
            final int cooldownReduction = getBaseCooldownMod();
            int usageCooldown = (int) (itemUsageCD * ( (100 - cooldownReduction)/100D ));
            
            // Apply cooldown to weapon-specific material based on range category
            Material cooldownMaterial = getWeaponCooldownMaterial();
            getPlayer().setCooldown(cooldownMaterial, usageCooldown);
            
            if (swingEffect != null) {
                swingEffect.apply(getPlayer());
            }
        }

        /**
         * Fetches the player's cooldown reduction stat from their global stats.
         * @return cooldown reduction percentage (positive values reduce cooldown)
         */
        public int getBaseCooldownMod() {
            try {
                StatPool globalStats = PlayerDataContainer.getDataContainerFor(getPlayerID()).getGlobalStats();
                double[] statValues = globalStats.calculateStatValue(Stats.COOLDOWN_REDUCTION);
                
                // Check if stat values exist and are valid
                if (statValues == null || statValues.length == 0) {
                    return 0; // No cooldown reduction
                }
                
                // Use the first value from the stat array (most stats use [0] for the main value)
                return (int) statValues[0];
            } catch (Exception e) {
                // Silently handle the case where player has no cooldown reduction equipment
                // This is normal for players without cooldown reduction gear
                return 0;
            }
        }

        /**
         * Determines the correct material for weapon cooldowns based on weapon range category.
         * @return Material.SHEARS for melee weapons, Material.BOW for ranged weapons
         */
        private Material getWeaponCooldownMaterial() {
            try {
                // Get equipped weapon's range category from AttackData
                // Since we have the weapon speed, we can infer the range from weapon type
                // For now, we'll use a simple approach based on the skill used
                Skills skill = getCastData().getCastingContext().getSkillUsed();
                
                // Map skills to their weapon ranges
                return switch (skill) {
                    case BASIC_AXE_SLASH, BASIC_SWORD_SLASH, BASIC_DAGGER_SLASH, BASIC_MACE_SLAM -> Material.SHEARS;
                    case BASIC_BOW_SHOT, BASIC_WAND_ATTACK -> Material.BOW;
                    default -> Material.SHEARS; // Default to melee
                };
            } catch (Exception e) {
                Utils.error("Failed to determine weapon cooldown material, defaulting to SHEARS");
                return Material.SHEARS;
            }
        }
//
//        @Override
//        protected void taskRoutine(Player player) {
//            getCastData().getCastingContext().getSkillUsed().getSkillRoutine().accept(this);
//        }
    }
}
