package com.amorabot.inscripted.skill.routine.slam;

import com.amorabot.inscripted.skill.routine.slash.SlashConfig;

public record SlamConfigDTO(SlashConfig slashAnimationData,
                            boolean rightHanded, double slashOffsetPhase, double handHeightReduction,
                            double impactRadius, int delayToImpact, int animationDuration) {
}
