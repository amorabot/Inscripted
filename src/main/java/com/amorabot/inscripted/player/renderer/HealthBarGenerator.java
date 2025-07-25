package com.amorabot.inscripted.player.renderer;

import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.component.HealthComponent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static com.amorabot.inscripted.item.render.CustomUnicodeTable.*;

public class HealthBarGenerator {
    private static final int SEGMENTS = 5;
    private static final String stdOffset = M1.getUnicode();
    private static final String soulTailOffset = P2.getUnicode()+M16.getUnicode()+M64.getUnicode();
    public static Component getHealthBarSegmentsFor(Player player){
        HealthComponent playerHP = PlayerDataContainer.getProfile(player.getUniqueId()).getHealthComponent();
        String soul = getSoulBar(playerHP);
        String hp = getHealthBar(playerHP);
        return Component.text(getHealthFrame()+soul+hp+"\n\n");
    }

    private static String getHealthFrame(){
        return HP_FRAME.getUnicode() + M16.getUnicode() + M64.getUnicode();
    }

    public static String getHealthBar(HealthComponent playerHealth){
        StringBuilder hpBuilder = new StringBuilder();
        if (playerHealth.getMaxHealth() == 0){ //Wtf
            return ("\uE000").repeat(5);
        }
        if (playerHealth.getHealth() == playerHealth.getMaxHealth()){ //Full health
            return (HP_FULL_HEAD.getUnicode()+stdOffset) + (HP_FULL_MID.getUnicode()+stdOffset).repeat(3) + (HP_FULL_TAIL.getUnicode()+stdOffset);
        }
        int hpPercentage = playerHealth.getCurrentHealthPercentage();
        int validSegments = hpPercentage/20;
        int remainder = hpPercentage%20;
        boolean isHalf = (remainder<=10);
        if (validSegments==0 && remainder>0){ //Up to half segment
            String singleSegment;
            if (isHalf){
                singleSegment = (HP_HALF_HEAD.getUnicode()+stdOffset);
            } else {
                singleSegment = (HP_FULL_HEAD.getUnicode()+stdOffset);
            }
            hpBuilder.append(singleSegment);
            hpBuilder.append((NO_HP_MID.getUnicode()+stdOffset).repeat(3));
            hpBuilder.append(NO_HP_TAIL.getUnicode());
            return hpBuilder.toString();
        }
        String fullHead = HP_FULL_HEAD.getUnicode()+stdOffset;
        hpBuilder.append(fullHead);
        int segmentsToRender = validSegments-1;
        for (int i = 0; i < SEGMENTS-1; i++) {
            boolean filled = i<=segmentsToRender;
            boolean tail = i==(SEGMENTS-2);
            boolean lastValid = i==segmentsToRender;
            if (filled){
                if (lastValid){
                    hpBuilder.append(getSegment(false,tail,true,isHalf));
                    continue;
                }
                hpBuilder.append(getSegment(false,tail,true,false));
                continue;
            }
            hpBuilder.append(getSegment(false,tail, false,false));
            continue;
        }
        return hpBuilder.toString();
    }

    public static String getSoulBar(HealthComponent playerHealth){
        StringBuilder soulBuilder = new StringBuilder();
        if (playerHealth.getMaxSoul() == 0){ //No soul
            return (NO_SOUL_HEAD.getUnicode()+stdOffset) + (NO_SOUL_MID.getUnicode()+stdOffset).repeat(3) + (NO_SOUL_TAIL.getUnicode()+soulTailOffset);
        }
        if (playerHealth.getMaxSoul() == playerHealth.getSoul()){ //Full soul
            return (SOUL_FULL_HEAD.getUnicode()+stdOffset) + (SOUL_FULL_MID.getUnicode()+stdOffset).repeat(3) + (SOUL_FULL_TAIL.getUnicode()+soulTailOffset);
        }
        //In-between
        /*
        82 -> 4 | 37 -> 1  | 20 -> 1
        82 -> 2 | 37 -> 17 | 20 -> 0
           3, 0 | 0, 1/2   | 0, 0

        */

        int soulPercentage = playerHealth.getCurrentSoulPercentage();
        int validSegments = soulPercentage/20; //1 Segment can fit 20% intervals
        int remainder = soulPercentage%20;
        boolean isHalf = (remainder<=10);

        if (validSegments==0 && remainder>0){ //Up to half segment
            String singleSegment;
            if (isHalf){
                singleSegment = (SOUL_HALF_HEAD.getUnicode()+stdOffset);
            } else {
                singleSegment = (SOUL_FULL_HEAD.getUnicode()+stdOffset);
            }
            soulBuilder.append(singleSegment);
            String mid = NO_SOUL_MID.getUnicode()+stdOffset;
            soulBuilder.append(mid.repeat(3));
            String emptyTail = NO_SOUL_TAIL.getUnicode()+soulTailOffset;
            soulBuilder.append(emptyTail);
            return soulBuilder.toString();
        }
        //At least HEAD is complete
        String fullHead = SOUL_FULL_HEAD.getUnicode()+stdOffset;
        soulBuilder.append(fullHead);
        int segmentsToRender = validSegments-1;
        //Render everything until tail
        for (int i = 0; i < SEGMENTS-1; i++) {
            boolean filled = i<=segmentsToRender;
            boolean tail = i==(SEGMENTS-2); // X 0 1 2 3(5-2), 3==tail
            boolean lastValid = i==segmentsToRender;
            if (filled){
                if (lastValid){
                    soulBuilder.append(getSegment(true,tail,true,isHalf));
                    continue;
                }
                soulBuilder.append(getSegment(true,tail,true,false));
                continue;
            }
            soulBuilder.append(getSegment(true,tail, false,false));
            continue;
        }
        return soulBuilder.toString();
    }
    private static String getSegment(boolean isSoul, boolean tail, boolean filled, boolean half){
        if (tail){
            if (!filled){
                if (!isSoul) return NO_HP_TAIL.getUnicode();
                return NO_SOUL_TAIL.getUnicode()+soulTailOffset;
            }
            if (half){
                if (!isSoul) return HP_HALF_TAIL.getUnicode();
                return SOUL_HALF_TAIL.getUnicode()+soulTailOffset;
            }
            if (!isSoul) return HP_FULL_TAIL.getUnicode();
            return SOUL_FULL_TAIL.getUnicode()+soulTailOffset;
        }
        if (!filled){
            if (!isSoul) return NO_HP_MID.getUnicode()+stdOffset;
            return NO_SOUL_MID.getUnicode()+stdOffset;
        }
        if (half){
            if (!isSoul) return HP_HALF_MID.getUnicode()+stdOffset;
            return SOUL_HALF_MID.getUnicode()+stdOffset;
        }
        if (!isSoul) return HP_FULL_MID.getUnicode()+stdOffset;
        return SOUL_FULL_MID.getUnicode()+stdOffset;
    }
}
