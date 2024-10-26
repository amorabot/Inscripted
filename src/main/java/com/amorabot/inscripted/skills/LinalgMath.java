package com.amorabot.inscripted.skills;

import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Location;
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
        return vec.multiply(cos(angleR))
                .add(normalAxis.getCrossProduct(vec).multiply(sen(angleR)))
                .add(normalAxis.multiply( (normalAxis.dot(vec)*(1-cos(angleR))) ));
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

    public static Vector[] plotNonAlignedCircleBorder(Vector center, Vector planeNormal, double radius, int numPoints){
        Vector[] plottedPoints = new Vector[numPoints];

        Vector xAxis = planeNormal.clone().crossProduct(new Vector(0,1,0)).normalize();
        Vector yAxis = xAxis.clone().crossProduct(planeNormal);
        double angleStepR = ((360D /numPoints)/180) * Math.PI;
        for (int n = 0; n<numPoints; n++){
            double currAngle = angleStepR*n;
            Vector relativeXPos = xAxis.clone().multiply(radius*cos(currAngle));
            Vector relativeYPos = yAxis.clone().multiply(radius*sen(currAngle));
            Vector currentPoint = center.clone().add(relativeXPos).add(relativeYPos);
            plottedPoints[n] = currentPoint;
        }
        return plottedPoints;
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