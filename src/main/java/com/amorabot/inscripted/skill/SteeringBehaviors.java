package com.amorabot.inscripted.skill;

import com.amorabot.inscripted.skill.routine.projectile.Projectile;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.util.Vector;

import static com.amorabot.inscripted.utils.Utils.limitVector;

@Getter
public enum SteeringBehaviors {
    STRAIGHT_LINE(false) {
        @Override
        public void steer(Projectile proj) {
            proj.applyForce(proj.getBaseAcceleration());
        }
    },
    SEEK(true) {
        @Override
        public void steer(Projectile proj) {
            Vector desiredVelocity = limitVector(proj.getTarget().clone().subtract(proj.getOrigin()), proj.getMaxSpeed());
            Vector steeringForce = desiredVelocity.subtract(proj.getVelocity());
            proj.applyForce(steeringForce);
        }
    },
    ARRIVE(true) {
        @Override
        public void steer(Projectile proj) {
            float arriveRadius = 10;
            double dist = proj.getOrigin().clone().distance(proj.getTarget());
            Vector desiredVelocity;
            if (dist < arriveRadius){
                final double minSpeedPercentage = 0.3;
                double newMaxSpeed = (proj.getMaxSpeed() * minSpeedPercentage) + Utils.getParametricValue(0, (1-minSpeedPercentage)* proj.getMaxSpeed(), dist/arriveRadius);
                desiredVelocity = proj.getTarget().clone().subtract(proj.getOrigin()).normalize().multiply(newMaxSpeed);
            } else {
                desiredVelocity = limitVector(proj.getTarget().clone().subtract(proj.getOrigin()), proj.getMaxSpeed());
            }

            Vector steeringForce = desiredVelocity.subtract(proj.getVelocity());
            proj.applyForce(steeringForce);
        }
    };

    final boolean singleTarget;

    SteeringBehaviors(boolean singleTarget){
        this.singleTarget = singleTarget;
    }
    public abstract void steer(Projectile proj);
}
