package com.amorabot.inscripted.skill.type;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
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
            final int cooldownReduction = getBaseCooldownMod(); //TODO: Fetch Cooldown Reduction stat
            int usageCooldown = (int) (itemUsageCD * ( (100 + cooldownReduction)/100D ));
            getPlayer().setCooldown(Material.SHEARS,usageCooldown);
            getPlayer().setCooldown(Material.BOW,usageCooldown);
            if (swingEffect != null) {
                swingEffect.apply(getPlayer());
            }
        }
//
//        @Override
//        protected void taskRoutine(Player player) {
//            getCastData().getCastingContext().getSkillUsed().getSkillRoutine().accept(this);
//        }
    }
}
