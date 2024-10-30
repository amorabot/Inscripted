package com.amorabot.inscripted.skills.attackInstances.slash;

public record SlashConfigDTO(int segments, int arc, double baseRadius, double initialOffset, double finalOffset,
                             double startingLength, double finalLength, int[] baseColor, double[] skewFactor, float particleSize,
                             double tipPercentage) {
}
