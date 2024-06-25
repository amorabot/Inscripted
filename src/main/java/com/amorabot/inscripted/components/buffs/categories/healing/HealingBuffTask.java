package com.amorabot.inscripted.components.buffs.categories.healing;

import com.amorabot.inscripted.components.Buff;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.managers.JSONProfileManager;
import com.amorabot.inscripted.managers.PlayerBuffManager;
import com.amorabot.inscripted.tasks.CombatHologramsDepleter;
import com.amorabot.inscripted.utils.ColorUtils;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.entity.Player;

public class HealingBuffTask extends Buff {

    private final Buffs buff;

    private final int totalTicks;
    private final int healingTick;
    private final Player caster;
    private final Player target;


    private int ticks = 0;
    private int skips = 0;


    public HealingBuffTask(Buffs buff, int finalHealingTick, Player caster, Player target){
        if (!buff.isHealingBuff()){this.buff = null;}
        else {this.buff = buff;}
        this.healingTick = finalHealingTick;
        this.caster = caster;
        this.target = target;
        this.totalTicks = ((Healing)buff.getBuffAnnotationData()).timesApplied();
    }



    @Override
    public void run() {
        boolean skip = !target.isOnline();
        if (skip){
            skips++;
            if (skips >= 30){ //Maximum skips threshold for cancelling after a player quits
                Utils.log("Cancelling after quit!");
                this.cancel();
            }
            return;
        }

        if (ticks>=totalTicks){
            expire();
            return;
        }
        Profile targetProfile = JSONProfileManager.getProfile(target.getUniqueId());
        boolean targetBleeding = PlayerBuffManager.hasActiveBuff(Buffs.BLEED, target);
        int amountHealed = targetProfile.getHealthComponent().healHealth(healingTick, targetBleeding, target, targetProfile.getKeystones());

        buff.effectOn(target);
        CombatHologramsDepleter.getInstance().instantiateRegenHologram(target.getLocation(), "&2"+amountHealed);
        debugHealing(caster, target, amountHealed);

        ticks++;
    }



    @Override
    public void expire() {
        //Whatever
        Utils.log(buff+" expired for " + target.getName()+"!");
        PlayerBuffManager.removeBuffFrom(target, buff);

        this.cancel();
    }

    public int getTotalRemainingHealing(){
        int ticksRemaining = totalTicks - ticks;
        return healingTick * ticksRemaining;
    }

    public void debugHealing(Player caster, Player target, int healedAmount){
        DefenceTypes HPDefinitions = DefenceTypes.HEALTH;
        String healString = ColorUtils.translateColorCodes(HPDefinitions.getTextColorTag() + "+"+healedAmount+" "+ HPDefinitions.getSpecialChar()+" ");
        Utils.msgPlayer(caster, "&2&l-> "+ healString + ("&f("+target.getName()+")"));
//        Utils.msgPlayer(target, "&2&l<- "+ healString + ("&f("+caster+")"));
    }
}
