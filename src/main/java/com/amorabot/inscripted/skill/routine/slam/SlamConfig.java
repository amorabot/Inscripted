package com.amorabot.inscripted.skill.routine.slam;

import com.amorabot.inscripted.skill.routine.slash.SlashConfig;

public record SlamConfig(SlashConfig slashAnimationData,
                         boolean rightHanded, double slashOffsetPhase, double handHeightReduction,
                         double impactRadius, int delayToImpact, int animationDuration) {
}
