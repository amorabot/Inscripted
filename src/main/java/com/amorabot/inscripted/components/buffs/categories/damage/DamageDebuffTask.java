package com.amorabot.inscripted.components.buffs.categories.damage;

import com.amorabot.inscripted.APIs.damageAPI.DamageRouter;
import com.amorabot.inscripted.APIs.damageAPI.DamageSource;
import com.amorabot.inscripted.components.Buff;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.entity.Player;

import java.util.Objects;

public class DamageDebuffTask extends Buff {

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

        if ((ticks>=totalTicks) || JSONProfileManager.getProfile(defender.getUniqueId()).getHealthComponent().getCurrentHealth() == 0){
            expire();
            return;
        }

//        float hurtAnimationOffset = (float)(Utils.getRandomOffset() * 90);
//        defender.sendHurtAnimation(hurtAnimationOffset);
        defender.damage(0.01);
        boolean killed = DamageRouter.damagePlayer(defender, damage, false, selfDamage, attacker, DamageSource.DOT);
//        if (killed){
//            expire();
//            return;
//        }
        buff.effectOn(defender);
        ticks++;
    }

    public void expire(){
        //Whatever
        Utils.log(buff+" expired for " + defender.getName()+"!");
        PlayerBuffManager.removeBuffFrom(defender, buff);

        this.cancel();
    }

    public int getTotalRemainingDamage(){
        int tickDamage = damage[buff.ordinal()];
        int ticksRemaining = totalTicks - ticks;
        return tickDamage * ticksRemaining;
    }
}
