package com.amorabot.inscripted.skill.attackInstances.slam;

import com.amorabot.inscripted.skill.attackInstances.slash.SlashConfig;

public record SlamConfigDTO(SlashConfig slashAnimationData,
                            boolean rightHanded, double slashOffsetPhase, double handHeightReduction,
                            double impactRadius, int delayToImpact, int animationDuration) {
}
