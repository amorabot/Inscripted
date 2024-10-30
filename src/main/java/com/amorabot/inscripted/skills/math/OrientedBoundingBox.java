package com.amorabot.inscripted.skills.math;

import com.amorabot.inscripted.skills.ParticlePlotter;
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrientedBoundingBox {

    private final Vector center = new Vector();
    private final Vector[] orientation;
    private final double[]  extents = new double[3];
    private final Vector[] corners;

    public OrientedBoundingBox(Vector[] points, Vector[] orientationAxes){
        this.orientation = orientationAxes;

        for (int i = 0; i<orientationAxes.length; i++){
            //Projection values for current axis
            double[] currentAxisProjections = LinalgMath.getProjectionExtents(points, orientationAxes[i]);
            //Lets find the center of the OBB by incrementing its position per-axis
            double minProj = currentAxisProjections[0];
            double maxProj = currentAxisProjections[1];

            double centerDist = (maxProj+minProj)/2;
            double axisExtent = Math.max((maxProj-minProj)/2, 0.05);
            Vector currentAxisCenter = orientationAxes[i].clone().multiply(centerDist);
            this.center.add(currentAxisCenter);
            this.extents[i] = axisExtent;
        }

        this.corners = generateCorners();
    }

    private Vector getAxisExtent(int axisIndex){
        return this.orientation[axisIndex].clone().multiply(this.extents[axisIndex]);
    }
    private Vector getCorner(Vector u,Vector v,Vector w){
        return this.center.clone().add(u).add(v).add(w);
    }

    private Vector[] generateCorners(){
        Vector uExtent = getAxisExtent(0);
        Vector vExtent = getAxisExtent(1);
        Vector wExtent = getAxisExtent(2);

        return new Vector[]{
                getCorner(uExtent.clone().multiply(-1),vExtent,wExtent.clone().multiply(-1)),
                getCorner(uExtent,vExtent,wExtent.clone().multiply(-1)),
                getCorner(uExtent,vExtent.clone().multiply(-1),wExtent.clone().multiply(-1)),
                getCorner(uExtent.clone().multiply(-1),vExtent.clone().multiply(-1),wExtent.clone().multiply(-1)),
                getCorner(uExtent.clone().multiply(-1),vExtent,wExtent),
                getCorner(uExtent,vExtent,wExtent),
                getCorner(uExtent,vExtent.clone().multiply(-1),wExtent),
                getCorner(uExtent.clone().multiply(-1),vExtent.clone().multiply(-1),wExtent)
        };
    }
    public Vector getMin(){
        return corners[3];
    }
    public Vector getMax(){
        return corners[5];
    }

    public void expandDirectional(int axisIndex, boolean forward, double amount){
        Vector selectedAxis = orientation[axisIndex].clone();
        if (!forward){selectedAxis.multiply(-1);}
        List<Vector> selectedCorners = new ArrayList<>();
        for (Vector corner : corners){
            double dot = selectedAxis.dot(corner.clone().subtract(center));
            if (dot>0){selectedCorners.add(corner);}
        }
        Vector expansionVector = selectedAxis.clone().multiply(amount);
        for (Vector selectedCorner : selectedCorners){
            selectedCorner.add(expansionVector);
        }
    }
    public void expandFromCenter(int axisIndex, double amount){
        expandDirectional(axisIndex, true, amount/2);
        expandDirectional(axisIndex, false, amount/2);
    }
    public void expandFromCenter(double amount){
        expandFromCenter(0, amount/2);
        expandFromCenter(1, amount/2);
        expandFromCenter(2, amount/2);
    }

    public boolean intersects(OrientedBoundingBox obb){
        //Define all axes (3 base dir + 3 base dir + 9 crossProd ([a0, b0],[a0, b1],[a0, b2],[a1, b0],[a1, b1],...) = 15 axes
        Vector[] separationAxes = getSeparationAxes(obb.getOrientation());

        return !separated(obb.getOrientation(), obb.getCorners(), separationAxes);
    }
    private boolean separated(Vector[] testedOrientation, Vector[] testedCorners, Vector[] remainingSeparationAxes){
        //3 from this OBB's orientation vectors
        for (Vector orientationAxisA : orientation){
            if (LinalgMath.areSeparated(getCorners(),testedCorners,orientationAxisA)){return true;}
        }
        //3 from the tested OBB orientation
        for (Vector orientationAxisB : testedOrientation){
            if (LinalgMath.areSeparated(getCorners(),testedCorners,orientationAxisB)){return true;}
        }
        //9 from Cross Products between orientations (Edge-Edge collision plane normals)
        for (Vector axis : remainingSeparationAxes){
            if (LinalgMath.areSeparated(getCorners(),testedCorners,axis)){return true;}
        }
        return false;
    }

    public boolean intersects(BoundingBox aabb){
        Vector[] defaultAxes = new Vector[]{new Vector(1,0,0),new Vector(0,1,0),new Vector(0,0,1)};
        Vector[] separationAxes = getSeparationAxes(defaultAxes);
        Vector[] aabbCorners = new Vector[]{
                aabb.getMax(),
                new Vector(aabb.getMaxX(), aabb.getMaxY(), aabb.getMinZ()),
                new Vector(aabb.getMinX(), aabb.getMaxY(), aabb.getMinZ()),
                new Vector(aabb.getMaxX(), aabb.getMaxY(), aabb.getMaxZ()),
                aabb.getMin(),
                new Vector(aabb.getMinX(), aabb.getMinY(), aabb.getMaxZ()),
                new Vector(aabb.getMaxX(), aabb.getMinY(), aabb.getMaxZ()),
                new Vector(aabb.getMaxX(), aabb.getMinY(), aabb.getMinZ()),
        };

        return !separated(defaultAxes, aabbCorners, separationAxes);
    }

    private Vector[] getSeparationAxes(Vector[] testedDrientation){
        Vector[] separationAxes = new Vector[9];
        for (int i = 0; i<testedDrientation.length; i++){
            for (int j = 0; j<testedDrientation.length; j++){
                Vector currentEdgeNormal = getOrientation()[i].clone().crossProduct(testedDrientation[j]);
                int index = (i*3) + j;
                separationAxes[index] = currentEdgeNormal;
            }
        }
        return separationAxes;
    }

    public void render(World world){
        Vector[] topVertices = new Vector[] {corners[4], corners[5] , corners[6], corners[7]};
        Vector[] botVertices = new Vector[] {corners[0] , corners[1] , corners[2], corners[3]};

        for (int i = 0; i < topVertices.length; i++){

            ParticlePlotter.lerpParticlesBetween(topVertices[i], topVertices[(i + 1) % topVertices.length], 0.2f, Particle.FLAME, world);
            ParticlePlotter.lerpParticlesBetween(botVertices[i], botVertices[(i + 1) % botVertices.length], 0.2f, Particle.FLAME, world);
            ParticlePlotter.lerpParticlesBetween(topVertices[i], botVertices[i], 0.2f, Particle.FLAME, world);
        }
    }
}
