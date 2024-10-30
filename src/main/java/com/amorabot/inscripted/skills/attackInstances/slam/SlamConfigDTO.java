package com.amorabot.inscripted.skills.attackInstances.slam;

import com.amorabot.inscripted.skills.attackInstances.slash.SlashConfigDTO;

public record SlamConfigDTO(SlashConfigDTO slashAnimationData,
                            boolean rightHanded, double slashOffsetPhase, double handHeightReduction,
                            double impactRadius, int delayToImpact, int animationDuration) {
}
