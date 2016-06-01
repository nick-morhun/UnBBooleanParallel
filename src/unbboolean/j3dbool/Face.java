package unbboolean.j3dbool;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

// Face class only
enum PointStatus {

    /**
     * point status if it is up relative to an edge - see linePositionIn_
     * methods
     */
    UP,
    /**
     * point status if it is down relative to an edge - see linePositionIn_
     * methods
     */
    DOWN,
    /**
     * point status if it is on an edge - see linePositionIn_ methods
     */
    ON,
    /**
     * point status if it isn't up, down or on relative to an edge - see
     * linePositionIn_ methods
     */
    NONE
}

enum FaceStatus {

    /**
     * face status if it is still unknown
     */
    UNKNOWN,
    /**
     * face status if it is inside a solid
     */
    INSIDE,
    /**
     * face status if it is outside a solid
     */
    OUTSIDE,
    /**
     * face status if it is coincident with a solid face
     */
    SAME,
    /**
     * face status if it is coincident with a solid face with opposite
     * orientation
     */
    OPPOSITE;

    /**
     * Converts given value to the VertexStatus value
     * @param f FaceStatus constant
     * @return corresponding VertexStatus constant
     */
    public static VertexStatus toVertexStatus(FaceStatus f) {
        VertexStatus v = null;
        if (f == FaceStatus.UNKNOWN) {
            v = VertexStatus.UNKNOWN;
        } else if (f == FaceStatus.INSIDE) {
            v = VertexStatus.INSIDE;
        } else if (f == FaceStatus.OUTSIDE) {
            v = VertexStatus.OUTSIDE;
        }
        return v;
    }
}

/**
 * Representation of a 3D face (triangle).
 *
 * <br><br>See: D. H. Laidlaw, W. B. Trumbore, and J. F. Hughes. "Constructive
 * Solid Geometry for Polyhedral Objects" SIGGRAPH Proceedings, 1986, p.161.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */
public class Face implements Cloneable {

    /**
     * first vertex
     */
    public Vertex v1;
    /**
     * second vertex
     */
    public Vertex v2;
    /**
     * third vertex
     */
    public Vertex v3;
    /**
     * face status relative to a solid
     */
    private FaceStatus status;
    private Bound bound;
    private Vector3d normal;
    /**
     * tolerance value to test equalities
     */
    private static final double TOL = 1e-10f;

    //---------------------------------CONSTRUCTORS---------------------------------//
    /**
     * Constructs a face with unknown status.
     *
     * @param v1 a face vertex
     * @param v2 a face vertex
     * @param v3 a face vertex
     */
    public Face(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        status = FaceStatus.UNKNOWN;

        bound = new Bound(v1.getPosition(), v2.getPosition(), v3.getPosition());
        normal = computeNormal();
    }

    //-----------------------------------OVERRIDES----------------------------------//
    /**
     * Clones the face object
     *
     * @return cloned face object
     */
    public Object clone() {
        try {
            Face clone = (Face) super.clone();
            clone.v1 = (Vertex) v1.clone();
            clone.v2 = (Vertex) v2.clone();
            clone.v3 = (Vertex) v3.clone();
            clone.bound = (Bound) bound.clone();
            clone.status = status;
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Makes a string definition for the Face object
     *
     * @return the string definition
     */
    public String toString() {
        return v1.toString() + "\n" + v2.toString() + "\n" + v3.toString();
    }

    /**
     * Checks if a face is equal to another. To be equal, they have to have
     * equal vertices in the same order
     *
     * @param anObject the other face to be tested
     * @return true if they are equal, false otherwise.
     */
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Face)) {
            return false;
        } else {
            Face face = (Face) anObject;
            boolean cond1 = v1.equals(face.v1) && v2.equals(face.v2) && v3.equals(face.v3);
            boolean cond2 = v1.equals(face.v2) && v2.equals(face.v3) && v3.equals(face.v1);
            boolean cond3 = v1.equals(face.v3) && v2.equals(face.v1) && v3.equals(face.v2);

            return cond1 || cond2 || cond3;
        }
    }

    //-------------------------------------GETS-------------------------------------//
    /**
     * Gets the face bound
     *
     * @return precomputed face bound
     */
    public Bound getBound() {
        return bound;
    }

    /**
     * Gets the face normal
     *
     * @return face normal
     */
    public Vector3d getNormal() {
        return normal;
    }

    /**
     * Gets the face status
     *
     * @return face status - UNKNOWN, INSIDE, OUTSIDE, SAME OR OPPOSITE
     */
    public FaceStatus getStatus() {
        return status;
    }

    /**
     * Gets the face area
     *
     * @return face area
     */
    public double getArea() {
        //area = (a * c * sen(B))/2
        Point3d p1 = v1.getPosition();
        Point3d p2 = v2.getPosition();
        Point3d p3 = v3.getPosition();
        Vector3d xy = new Vector3d(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        Vector3d xz = new Vector3d(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

        double a = p1.distance(p2);
        double c = p1.distance(p3);
        double B = xy.angle(xz);

        return (a * c * Math.sin(B)) / 2d;
    }

    //-------------------------------------OTHERS-----------------------------------//
    /**
     * Invert face direction (normal direction)
     */
    public void invert() {
        Vertex vertexTemp = v2;
        v2 = v1;
        v1 = vertexTemp;
        normal.x = -normal.x;
        normal.y = -normal.y;
        normal.z = -normal.z;
    }

    //------------------------------------CLASSIFIERS-------------------------------//
    /**
     * Classifies the face if one of its vertices are classified as INSIDE or
     * OUTSIDE
     *
     * @return true if the face could be classified, false otherwise
     */
    public boolean simpleClassify() {
        VertexStatus status1 = v1.getStatus();
        VertexStatus status2 = v2.getStatus();
        VertexStatus status3 = v3.getStatus();

        if (status1 == VertexStatus.INSIDE) {
            this.status = FaceStatus.INSIDE;
        } else if (status1 == VertexStatus.OUTSIDE) {
            this.status = FaceStatus.OUTSIDE;

        } else if (status2 == VertexStatus.INSIDE) {
            this.status = FaceStatus.INSIDE;
        } else if (status2 == VertexStatus.OUTSIDE) {
            this.status = FaceStatus.OUTSIDE;

        } else if (status3 == VertexStatus.INSIDE) {
            this.status = FaceStatus.INSIDE;
        } else if (status3 == VertexStatus.OUTSIDE) {
            this.status = FaceStatus.OUTSIDE;

        } else {
            return false;
        }
        return true;
    }

    /**
     * Classifies the face based on the ray trace technique
     *
     * @param object object3d used to compute the face status
     */
    public void rayTraceClassify(Object3D object) {
        //creating a ray starting starting at the face baricenter going to the normal direction
        Point3d p0 = new Point3d();
        p0.x = (v1.x + v2.x + v3.x) / 3d;
        p0.y = (v1.y + v2.y + v3.y) / 3d;
        p0.z = (v1.z + v2.z + v3.z) / 3d;
        Line ray = new Line(getNormal(), p0);

        boolean success;
        double dotProduct, distance;
        Point3d intersectionPoint;
        Face closestFace = null;
        double closestDistance;

        do {
            success = true;
            closestDistance = Double.MAX_VALUE;
            //for each face from the other solid...
            for (int i = 0; i < object.getNumFaces(); i++) {
                Face face = object.getFace(i);
                dotProduct = face.getNormal().dot(ray.getDirection());
                intersectionPoint = ray.computePlaneIntersection(face.getNormal(), face.v1.getPosition());

                //if ray intersects the plane...
                if (intersectionPoint != null) {
                    distance = ray.computePointToPointDistance(intersectionPoint);

                    //if ray lies in plane...
                    if (Math.abs(distance) < TOL && Math.abs(dotProduct) < TOL) {
                        //disturb the ray in order to not lie into another plane
                        ray.perturbDirection();
                        success = false;
                        break;
                    }

                    //if ray starts in plane...
                    if (Math.abs(distance) < TOL && Math.abs(dotProduct) > TOL) {
                        //if ray intersects the face...
                        if (face.hasPoint(intersectionPoint)) {
                            //faces coincide
                            closestFace = face;
                            closestDistance = 0;
                            break;
                        }
                    } //if ray intersects plane...
                    else if (Math.abs(dotProduct) > TOL && distance > TOL) {
                        if (distance < closestDistance) {
                            //if ray intersects the face;
                            if (face.hasPoint(intersectionPoint)) {
                                //this face is the closest face untill now
                                closestDistance = distance;
                                closestFace = face;
                            }
                        }
                    }
                }
            }
        } while (success == false);

        //none face found: outside face
        if (closestFace == null) {
            status = FaceStatus.OUTSIDE;
        } //face found: test dot product
        else {
            dotProduct = closestFace.getNormal().dot(ray.getDirection());

            //distance = 0: coplanar faces
            if (Math.abs(closestDistance) < TOL) {
                if (dotProduct > TOL) {
                    status = FaceStatus.SAME;
                } else if (dotProduct < -TOL) {
                    status = FaceStatus.OPPOSITE;
                }
            } //dot product > 0 (same direction): inside face
            else if (dotProduct > TOL) {
                status = FaceStatus.INSIDE;
            } //dot product < 0 (opposite direction): outside face
            else if (dotProduct < -TOL) {
                status = FaceStatus.OUTSIDE;
            }
        }
    }

    //------------------------------------PRIVATES----------------------------------//
    /**
     * precompute the normal
     *
     * @return the face's normal
     */
    private Vector3d computeNormal() {
        Point3d p1 = v1.getPosition();
        Point3d p2 = v2.getPosition();
        Point3d p3 = v3.getPosition();
        Vector3d xy, xz, normal;

        xy = new Vector3d(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        xz = new Vector3d(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

        normal = new Vector3d();
        normal.cross(xy, xz);
        normal.normalize();

        return normal;
    }

    /**
     * Checks if the the face contains a point
     *
     * @param point to be tested
     * @param true if the face contains the point, false otherwise
     */
    private boolean hasPoint(Point3d point) {
        PointStatus result1, result2, result3;
        boolean hasUp, hasDown, hasOn;
        Vector3d normal = getNormal();

        //if x is constant...
        if (Math.abs(normal.x) > TOL) {
            //tests on the x plane
            result1 = linePositionInX(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInX(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInX(point, v3.getPosition(), v1.getPosition());
        } //if y is constant...
        else if (Math.abs(normal.y) > TOL) {
            //tests on the y plane
            result1 = linePositionInY(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInY(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInY(point, v3.getPosition(), v1.getPosition());
        } else {
            //tests on the z plane
            result1 = linePositionInZ(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInZ(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInZ(point, v3.getPosition(), v1.getPosition());
        }

        //if the point is up and down two lines...
        if (((result1 == PointStatus.UP) || (result2 == PointStatus.UP)
                || (result3 == PointStatus.UP)) && ((result1 == PointStatus.DOWN)
                || (result2 == PointStatus.DOWN) || (result3 == PointStatus.DOWN))) {
            return true;
        } //if the point is on of the lines...
        else if ((result1 == PointStatus.ON) || (result2 == PointStatus.ON)
                || (result3 == PointStatus.ON)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the position of a point relative to a line in the x plane
     *
     * @param point point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */
    private static PointStatus linePositionInX(Point3d point, Point3d pointLine1, Point3d pointLine2) {
        double a, b, z;
        if ((Math.abs(pointLine1.y - pointLine2.y) > TOL) && (((point.y >= pointLine1.y) && (point.y <= pointLine2.y)) || ((point.y <= pointLine1.y) && (point.y >= pointLine2.y)))) {
            a = (pointLine2.z - pointLine1.z) / (pointLine2.y - pointLine1.y);
            b = pointLine1.z - a * pointLine1.y;
            z = a * point.y + b;
            if (z > point.z + TOL) {
                return PointStatus.UP;
            } else if (z < point.z - TOL) {
                return PointStatus.DOWN;
            } else {
                return PointStatus.ON;
            }
        } else {
            return PointStatus.NONE;
        }
    }

    /**
     * Gets the position of a point relative to a line in the y plane
     *
     * @param point point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */
    private static PointStatus linePositionInY(Point3d point, Point3d pointLine1, Point3d pointLine2) {
        double a, b, z;
        if ((Math.abs(pointLine1.x - pointLine2.x) > TOL) && (((point.x >= pointLine1.x) && (point.x <= pointLine2.x)) || ((point.x <= pointLine1.x) && (point.x >= pointLine2.x)))) {
            a = (pointLine2.z - pointLine1.z) / (pointLine2.x - pointLine1.x);
            b = pointLine1.z - a * pointLine1.x;
            z = a * point.x + b;
            if (z > point.z + TOL) {
                return PointStatus.UP;
            } else if (z < point.z - TOL) {
                return PointStatus.DOWN;
            } else {
                return PointStatus.ON;
            }
        } else {
            return PointStatus.NONE;
        }
    }

    /**
     * Gets the position of a point relative to a line in the z plane
     *
     * @param point point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */
    private static PointStatus linePositionInZ(Point3d point, Point3d pointLine1, Point3d pointLine2) {
        double a, b, y;
        if ((Math.abs(pointLine1.x - pointLine2.x) > TOL) && (((point.x >= pointLine1.x) && (point.x <= pointLine2.x)) || ((point.x <= pointLine1.x) && (point.x >= pointLine2.x)))) {
            a = (pointLine2.y - pointLine1.y) / (pointLine2.x - pointLine1.x);
            b = pointLine1.y - a * pointLine1.x;
            y = a * point.x + b;
            if (y > point.y + TOL) {
                return PointStatus.UP;
            } else if (y < point.y - TOL) {
                return PointStatus.DOWN;
            } else {
                return PointStatus.ON;
            }
        } else {
            return PointStatus.NONE;
        }
    }
}