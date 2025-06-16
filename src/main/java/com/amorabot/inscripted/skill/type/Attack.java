package com.amorabot.inscripted.skill.type;

import com.amorabot.inscripted.item.structure.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.AttackData;
import com.amorabot.inscripted.skill.CastSource;
import com.amorabot.inscripted.skill.Skills;
import com.amorabot.inscripted.tasks.base.Skillcast;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.entity.Player;

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
        this.attackData = new AttackData(playerID,skillUsed,PlayerDataContainer.getPlayerEquipment(playerID).getGlobalStatCache());
    }

//    public void test(){
//        new Attack(null,null,WeaponAttackSpeeds.QUICK,new AttackSkillConfig(null,null,null)){
//
//            int test = 0;
//
//
//            @Override
//            public void run(){
//                Attack.this.getAttackData();
//                test+=1;
//            }
//
//            @Override
//            protected void taskRoutine(Player player) {
//
//            }
//
//            @Override
//            public void cast(CastSource source) {
//
//            }
//        }
//    }




    public static class Basic extends Attack{
        private final double itemUsageCD;

        public Basic(UUID playerID, Skills skillUsed, CastSource castSource, WeaponAttackSpeeds weaponSpeed) {
            super(playerID, skillUsed, castSource, weaponSpeed);
            this.itemUsageCD = weaponSpeed.getItemUsageCooldown();
        }

        @Override
        public void register(){
            //TODO: Implement item-usage-cooldown
        }

        @Override
        protected void taskRoutine(Player player) {
            getCastData().getCastingContext().getSkillUsed().getSkillRoutine().accept(this);
        }
    }
}
