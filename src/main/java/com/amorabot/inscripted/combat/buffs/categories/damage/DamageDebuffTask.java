package com.amorabot.inscripted.combat.buffs.categories.damage;

import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.combat.damage.DamageRouter;
import com.amorabot.inscripted.combat.damage.DamageSource;
import com.amorabot.inscripted.combat.buffs.BuffTask;
import com.amorabot.inscripted.combat.buffs.Buffs;
//import com.amorabot.inscripted.file.profile.JSONProfileManager;
import com.amorabot.inscripted.combat.buffs.PlayerBuffManager;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Objects;

public class DamageDebuffTask extends BuffTask {

    @Getter
    private final Buffs buff;

    private final int totalTicks;
    private final Player defender;
    private final Player attacker;
    private final int[] damage;
    private final boolean selfDamage;


    private int ticks = 0;
    private int skips = 0;

    public DamageDebuffTask(Buffs buff, int[] dot, Player defender, boolean isSelfDamage, Player attacker){
        if (!buff.isDamageBuff()){
            //Invalid object, then
            this.buff = null;
            this.totalTicks = 0;
            this.defender = null;
            this.attacker = null;
            this.damage = new int[5];
            this.selfDamage = false;
            return;
        }
        this.buff = buff;
        this.totalTicks = ((Damage) Objects.requireNonNull(buff.getBuffAnnotationData())).timesApplied();
        this.defender = defender;
        this.attacker = attacker;
        this.damage = dot;
        this.selfDamage = isSelfDamage;
    }


    @Override
    public void run() {
        boolean skip = !defender.isOnline();
        if (skip){
            skips++;
            if (skips >= 30){ //Time threshold for cancelling after a player quits
                Utils.log("Cancelling after quit!");
                this.cancel();
            }
            return;
        }

        HealthComponent defenderHealth = PlayerDataContainer.getProfile(defender.getUniqueId()).getHealthComponent();
        if ((ticks>=totalTicks) || defenderHealth.getHealth() == 0 || EntityStateManager.isDead(attacker)){
            expire();
            return;
        }

        float hurtAnimationOffset = (float)(Utils.getRandomOffset() * 90);
        defender.sendHurtAnimation(hurtAnimationOffset);
        defender.damage(0.01);
        DamageRouter.damagePlayer(defender,damage,selfDamage,attacker);
        buff.effectOn(defender);
        ticks++;
    }

    public void expire(){
        //Whatever
        Utils.log(buff+" expired for " + defender.getName()+"!");
        PlayerBuffManager.removeBuffFrom(defender.getUniqueId(), buff);

        this.cancel();
    }

    public int getTotalRemainingDamage(){
        int tickDamage = damage[buff.ordinal()];
        int ticksRemaining = totalTicks - ticks;
        return tickDamage * ticksRemaining;
    }
}
