package com.amorabot.inscripted.skill.routine.projectile;

import com.amorabot.inscripted.tasks.base.Skillcast;

import java.util.function.Consumer;
import java.util.function.Function;

public record ProjectileConfig(boolean hasGravity, boolean ignoreBlocks, boolean destroyOnContact,
                               double maxSpeed, double maxForce, double detectionRange,
                               Consumer<Projectile> trail, Function<Projectile, Boolean> collisionDetection, Consumer<Projectile> impactRoutine) {
}
