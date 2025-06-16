package com.amorabot.inscripted.skill.attackInstances.slash;

import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public record SlashConfig(BiFunction<SlashConfig,World, Consumer<Vector[]>> defaultRenderer,
                          int segments, int arc, double baseRadius, double initialOffset, double finalOffset,
                          double startingLength, double finalLength, int[] baseColor, double[] skewFactor, float particleSize,
                          double tipPercentage) {
}
