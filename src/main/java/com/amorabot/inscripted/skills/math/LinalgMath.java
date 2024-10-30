package com.amorabot.inscripted.skills.math;

import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class LinalgMath {

    private static double sen(double rads){
        return Math.sin(rads);
    }
    private static double cos(double rads){
        return Math.cos(rads);
    }
    private static double tan(double rads){
        return Math.tan(rads);
    }

    public static Vector rotateAroundX(Vector vec, double pitchR){
        //X stays the same
        double Y = vec.getY()*cos(pitchR) - vec.getZ()*sen(pitchR);
        double Z = vec.getZ()*cos(pitchR) - vec.getY()*sen(pitchR);

        return vec.setY(Y).setZ(Z);
    }
    public static Vector rotateAroundY(Vector vec, double yawR){
        double X = vec.getX()*cos(yawR) + vec.getZ()*sen(yawR);
        //Y stays the same
        double Z = vec.getZ()*cos(yawR) - vec.getX()*sen(yawR);

        return vec.setX(X).setZ(Z);
    }
    public static Vector rotateAroundZ(Vector vec, double rollR){
        double X = vec.getX()*cos(rollR) - vec.getY()*sen(rollR);
        double Y = vec.getY()*cos(rollR) + vec.getX()*sen(rollR);
        //Z stays the same

        return vec.setX(X).setY(Y);
    }

    //https://math.stackexchange.com/questions/2782529/is-the-maximum-rotation-by-multiplying-a-positive-definite-matrix-is-less-than-9
    //https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula
    public static Vector rotateAroundGenericAxis(Vector normalAxis, Vector vec, double angleR){
        return vec.clone().multiply(cos(angleR))
                .add(normalAxis.getCrossProduct(vec).multiply(sen(angleR)))
                .add(normalAxis.clone().multiply( (normalAxis.dot(vec)*(1-cos(angleR))) ));
    }

    public static Vector[] plotPointsInsideNonAlignedCircle(Vector center, Vector planeNormal, double radius, int numPoints){
        Vector[] plottedPoints = new Vector[numPoints];

        Vector xAxis = planeNormal.clone().crossProduct(new Vector(0,1,0)).normalize();
        Vector yAxis = xAxis.clone().crossProduct(planeNormal);

        for (int n = 0; n<numPoints; n++){
            double intermediateRadius = Utils.getRandomInclusiveValue(0, radius);
            double randomPhase = Utils.getRandomInclusiveValue(0, 359);
            double rad = (randomPhase/180) * Math.PI;
            Vector relativeXPos = xAxis.clone().multiply(intermediateRadius*cos(rad));
            Vector relativeYPos = yAxis.clone().multiply(intermediateRadius*sen(rad));
            Vector randomPoint = center.clone().add(relativeXPos).add(relativeYPos);
            plottedPoints[n] = randomPoint;
        }

        return plottedPoints;
    }

    public static Vector[][] plotSlam(Location slamOrigin,boolean rightHanded, double slashOffsetPhase, double handHeightReduction,
                                      double arc, int segments, double baseRadius, double[] skewFactor, boolean isSprinting,
                                      double startingLength, double lengthDiff,
                                      double initialOffset, double offsetDiff
                                      ){
        final double finalDistFromPlayer = baseRadius + Math.abs(lengthDiff);


        double lateralOffset = (finalDistFromPlayer)/2;

        Vector[] slamSlashOrientation = LinalgMath.getHorizontalOrientation(slamOrigin);

        //Define the lateral offset vector
        Vector lateralOffsetVec = slamSlashOrientation[0].clone().multiply(lateralOffset);
        if (!rightHanded){lateralOffsetVec.multiply(-1);}
        Location offsetSlamOrigin = slamOrigin.clone().add(lateralOffsetVec);

        //Define the target position in front of the player
        Vector targetOffset = slamSlashOrientation[1].clone().multiply(finalDistFromPlayer);
        Location slamTarget = slamOrigin.clone().add(targetOffset);

        Vector[] newSlamOrientation = LinalgMath.getHorizontalOrientation(offsetSlamOrigin.toVector(),slamTarget.toVector());
        //The new orientation basis is defined, but the final slamOrigin must be "finalDistToCenter" away from the target
        //Thus we need to translate the origin to the target and then "finalDistToCenter" in the offsetSlamOrigin direction

        Location finalSlamOrigin = slamTarget.clone().add(newSlamOrientation[1].clone().multiply(-finalDistFromPlayer));

        //small to BIG -> axe shape
        return LinalgMath.plotSlash(
                finalSlamOrigin.subtract(0,handHeightReduction,0),
                newSlamOrientation, skewFactor, true, baseRadius, arc,segments,false, isSprinting,
                startingLength,lengthDiff, initialOffset, offsetDiff, slashOffsetPhase
        );
    }

    public static Vector[][] plotSlash(Location originLoc, Vector[] orientation, double[] skewFactor, boolean vertical, double baseRadius,
                                       double arc, int segments, boolean isMirrored, boolean isSprinting,
                                       double startingLength, double lengthDiff, double initialOffset, double offsetDiff, double phaseOffset){
        Vector[][] points = new Vector[2][segments];


        final double angleStep = ( arc / segments) /180*Math.PI;
        double startingPhase = ((arc/2) + phaseOffset) /180*Math.PI;
        if (isMirrored){
            startingPhase = -startingPhase;
        }

        final Vector playerPos = originLoc.clone().add(0, 1.4, 0).toVector();

        double minDistanceFromOrigin = 0.3;
        if (isSprinting){
            minDistanceFromOrigin+=1.4;
        }

        Vector initialDirection = orientation[1];
        Vector perpendicularAxis;
        if (vertical){
            perpendicularAxis = orientation[2].clone(); //Original Z, If animation needs to be inverted, mult. by -1
        } else {
            perpendicularAxis = orientation[0]; //Original X
        }

        final Vector attackCenter = playerPos.clone().add(initialDirection.clone().multiply(minDistanceFromOrigin));

        double t = startingPhase;
        for (int segmentIndex = 0; segmentIndex < segments; segmentIndex++){
            double animationProgress = ((double) (segmentIndex)/ segments);


            double swipeSize = startingLength + (lengthDiff)*animationProgress;
            double handleOffset = initialOffset + (offsetDiff)*animationProgress;

            if (skewFactor == null){skewFactor = new double[]{1,1};}

            Vector tip =
                    attackCenter.clone()

                            .add( initialDirection.clone().multiply((baseRadius+handleOffset)*Math.cos(t))
                                    .multiply(skewFactor[1]) ) //Skews the initial dir (X)

                            .add( perpendicularAxis.clone().multiply((baseRadius+handleOffset)*Math.sin(t))
                                    .multiply(skewFactor[0]) ); //Skews the perpendic dir (X)

            Vector radialDirection = attackCenter.clone().subtract(tip).normalize();

            Vector handle = tip.clone().add(radialDirection.multiply(swipeSize));

            points[0][segmentIndex] = tip;
            points[1][segmentIndex] = handle;

            if (isMirrored){
                t += angleStep;
                continue;
            }
            t -= angleStep;
        }

        return points;
    }

    public static Vector[] getHorizontalOrientation(Vector origin, Vector target){
        Vector facingDir = target.clone().subtract(origin).normalize();
        Vector normal = new Vector(0, 1, 0);
        Vector perpendicularAxis = facingDir.clone().crossProduct(normal);

        return new Vector[]{perpendicularAxis, facingDir, normal};
    }

    public static Vector[] getHorizontalOrientation(Location originalOrientation){
        Vector facingDir;
        Vector perpendicularAxis;
        Vector normal = new Vector(0, 1, 0);

        double yawRad;
        double yawStdDeg = -originalOrientation.getYaw();
        if (originalOrientation.getYaw()>0){
            yawRad = yawStdDeg /180*Math.PI;
        } else {
            yawRad = (Math.PI+yawStdDeg) /180*Math.PI;
        }
        double xPos = Math.sin(yawRad);
        double zPos = Math.cos(yawRad);
        facingDir = new Vector(xPos, 0, zPos);

        perpendicularAxis = facingDir.clone().crossProduct(normal);

        return new Vector[]{perpendicularAxis, facingDir, normal};
    }

    public static Vector[] defineOrientation(Location playerLoc,
                                             boolean isMirrored, boolean isInverted, boolean isRandomized,
                                             int lowAngleThreshold, double... planeRotation){
        Vector initialDirection;
        Vector perpendicularAxis;
        Vector slashPlaneNormal;

        if (Math.abs(playerLoc.getPitch()+90) <= 2){ //2 degree "facing up" threshold
            initialDirection = new Vector(0, 1, 0);
            //If the player is looking UP, we have their YAW to define a "facing" direction
            double yawRad;
            if (playerLoc.getYaw()>0){
                yawRad = -(playerLoc.getYaw()) /180*Math.PI;
            } else {
                yawRad = (Math.PI-playerLoc.getYaw()) /180*Math.PI;
            }
            //Plane normal and "facing"
            double xPos = Math.sin(yawRad);
            double zPos = Math.cos(yawRad);
            Vector planeNormal = new Vector(xPos, 0, zPos).normalize();
            perpendicularAxis = planeNormal.clone().crossProduct(initialDirection);

            slashPlaneNormal = planeNormal;
        } else {
            initialDirection = playerLoc.getDirection().clone();
            Vector horizontalVec = new Vector(initialDirection.getX(), 0, initialDirection.getZ());
            perpendicularAxis = initialDirection.clone().crossProduct(horizontalVec).normalize();
            slashPlaneNormal = perpendicularAxis.clone().crossProduct(initialDirection);

            //Slash plane randomization when view angle is low
            if (planeRotation!= null && planeRotation.length>0){
                if (playerLoc.getPitch() <=lowAngleThreshold && playerLoc.getPitch() >= -lowAngleThreshold){ //When inside the 30deg view angle, apply the plane rotation
                    double rotationAngle;
                    if (isRandomized && planeRotation.length==2){
                        rotationAngle = Utils.getRandomInclusiveValue(planeRotation[0],planeRotation[1]);
                    } else {
                        rotationAngle = planeRotation[0];
                    }
                    if (isMirrored){rotationAngle = -rotationAngle;}
                    perpendicularAxis = LinalgMath.rotateAroundGenericAxis(initialDirection.clone(),perpendicularAxis.clone(), rotationAngle/180*Math.PI);

                    slashPlaneNormal = perpendicularAxis.clone().crossProduct(initialDirection); //Update and override plane normal
                }
            }
        }

        if (isInverted){perpendicularAxis.multiply(-1);}
        return new Vector[]{perpendicularAxis,initialDirection,slashPlaneNormal};
    }

    public static Vector[] plotNonAlignedCircleBorder(Vector center, Vector planeNormal, double radius, int numPoints){
        Vector[] plottedPoints = new Vector[numPoints];

        Utils.log(planeNormal.toString());
        Vector xAxis = planeNormal.clone().crossProduct(new Vector(0,1,0)).normalize();
        Vector yAxis = xAxis.clone().crossProduct(planeNormal);
        double angleStepR = ((360D /numPoints)/180) * Math.PI;
        for (int n = 0; n<numPoints; n++){
            double currAngle = angleStepR*n;
            Vector relativeXPos = xAxis.clone().multiply(radius*cos(currAngle));
            Vector relativeYPos = yAxis.clone().multiply(radius*sen(currAngle));
            Utils.log("x " + relativeXPos + " y " + relativeYPos);
            Vector currentPoint = center.clone().add(relativeXPos).add(relativeYPos);
            plottedPoints[n] = currentPoint;
        }
        return plottedPoints;
    }

    //Assumes normalized axis
    public static double[] getProjectionExtents(Vector[] points, Vector axis){
        if (axis==null){return new double[2];}

        double minProjection = Integer.MAX_VALUE;
        double maxProjection = Integer.MIN_VALUE;

        for (Vector point : points){
            if (point==null || point.isZero()){return new double[2];}
            double currentDot = axis.dot(point); //Projection value along "axis"
            minProjection = Math.min(minProjection, currentDot);
            maxProjection = Math.max(maxProjection, currentDot);
        }

        return new double[]{minProjection, maxProjection};
    }

    //https://gamedev.stackexchange.com/questions/44500/how-many-and-which-axes-to-use-for-3d-obb-collision-with-sat  => Logic behind 3D S.A.T. with OBB
    public static boolean areSeparated(Vector[] vertsA, Vector[] vertsB, Vector axis){
        if (axis.isZero()){return false;}

        //Get both projections along "axis"
        double[] projectionA = getProjectionExtents(vertsA, axis);
        double[] projectionB = getProjectionExtents(vertsB, axis);

        //Once we have all projections, we can do a simple one-dimensional intersection check (because "axis" is normalized)
        //NOT SEPARATED => Length of Proj A + Length of Proj B is greater than the Length from minimal value to max value
        //----------X-------------------------------O-------X-----------------------------------------------O----------
        //          |                               |       |                                               |
        //        minA----------------------------- | ----maxA                                              |
        //                                        minB-----------------------------------------------------maxB

        //SEPARATED => The length from the minValue to maxValue is greater than the sum of both lengths
        //----------X---------------------------------------X----------O--------------------------------------------------------O----------
        //          |                                       |          |                                                        |
        //        minA------------------------------------maxA         |                                                        |
        //                                                           minB-----------------------------------------------------maxB

        double longSpan = Math.max(projectionA[1],projectionB[1]) - Math.min(projectionA[0],projectionB[0]);
        double sumSpan = (projectionA[1]-projectionA[0]) + (projectionB[1]-projectionB[0]);
        return longSpan >= sumSpan; //Handles when the projections are touching
    }
}
    /*
        sin of the angle between 3d vectors
         | U x V | = | U | * | V | sin(a)
         sin(a) = |UxV| / |U|*|V|


    Parametric 3D circle equation:
    https://math.stackexchange.com/questions/1184038/what-is-the-equation-of-a-general-circle-in-3-d-space
    https://www.youtube.com/watch?v=OYgiI5bK4kE

        >> p + r( cos(t) )v1 + r( sin(t) )v2 ; t real

            v1 & v2 -> Orthonormal basis (Orthogonal and normalized vectors)

            p = center
            r = radius
            v1 = new base's "y" axis
            v2 = new base's "x" axis
            t = angle (rads)
     */