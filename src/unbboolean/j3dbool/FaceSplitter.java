package unbboolean.j3dbool;

import edu.rit.pj.IntegerForLoop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import unbboolean.Tolerance;

/**
 * A class to represent a face-splitting thread.
 * @author N. Morhun, some methods by Danilo Balby Silva Castanheira
 */
public class FaceSplitter extends IntegerForLoop {

    /**
     * faces remove flags
     */
    public static List<Boolean> FacesRemoveFlags;
    /**
     * total face compares count
     */
    public static AtomicLong NumFaceCompares;
    /**
     * total face splits count
     */
    public static AtomicInteger NumFaceSplits;
    /**
     * solid vertices
     */
    private List<Vertex> vertices;
    /**
     * solid faces
     */
    private List<Face> faces;
    /**
     * tolerance value to test equalities
     */
    //private final double TOL = 1e-10f;
    private Object3D otherObject;
    /**
     * an index of the last inserted vertex
     */
    private int insertionIdx;

    static {
        // Init common vars.
        FacesRemoveFlags = Collections.synchronizedList(
                new ArrayList<Boolean>());
        NumFaceCompares = new AtomicLong();
        NumFaceSplits = new AtomicInteger();
    }

    /**
     *
     * @param Vertices
     * @param Faces
     * @param object
     */
    public FaceSplitter(ArrayList<Vertex> Vertices, ArrayList<Face> Faces,
            Object3D object) {
        //System.out.println("created");
        vertices = Vertices;//Collections.synchronizedList(Vertices);
        faces = Faces;//Collections.synchronizedList(Faces);
        otherObject = object;

        synchronized (FacesRemoveFlags) {
            // If a new process started
            if (FacesRemoveFlags.isEmpty()) {
                NumFaceCompares.set(0l);
                NumFaceSplits.set(0);
                // Init remove flags.
                int size = faces.size();
                for (int i = 0; i < size; i++) {
                    FacesRemoveFlags.add(Boolean.FALSE);
                }
            }
        }
    }

    /**
     * Runs parallel loop.
     *
     * @param idx
     * @param notUsed
     */
    public void run(int idx, int endIdx) throws InterruptedException {
        byte ThreadNumFaceSplits = 0;
        long ThreadNumFaceCompares = 0;
        Line line;
        Face face2;
        Segment segment1, segment2;
        double distFace1Vert1, distFace1Vert2, distFace1Vert3, distFace2Vert1,
                distFace2Vert2, distFace2Vert3;
        int signFace1Vert1, signFace1Vert2, signFace1Vert3, signFace2Vert1,
                signFace2Vert2, signFace2Vert3;
        int NumFacesOther = otherObject.getNumFaces();

        // for given range...
        for (int face1idx = idx; face1idx <= endIdx; face1idx++) {
            Face face1 = getFace(face1idx);
            //System.out.println("face " + face1idx + " of " + faces.size());

            // if object1 face bound and object2 bound don't overlap...
            if (!face1.getBound().overlap(otherObject.getBound())) {
                continue;
            }

            // for each object2 face...
            for (int j = 0; j < NumFacesOther; ++j) {
                face2 = otherObject.getFace(j);

                // if object1 face bound and object2 face bound don't overlap...
                if (!face1.getBound().overlap(face2.getBound())) {
                    continue;
                }

                ++ThreadNumFaceCompares;
                // PART I - DO TWO POLIGONS INTERSECT?
                // POSSIBLE RESULTS: INTERSECT, NOT_INTERSECT, COPLANAR

                // distance from the face1 vertices to the face2 plane
                distFace1Vert1 = computeDistance(face1.v1, face2);
                distFace1Vert2 = computeDistance(face1.v2, face2);
                distFace1Vert3 = computeDistance(face1.v3, face2);

                // distances signs from the face1 vertices to the face2 plane
                signFace1Vert1 = (distFace1Vert1 > Tolerance.TOL ? 1
                        : (distFace1Vert1 < -Tolerance.TOL ? -1 : 0));
                signFace1Vert2 = (distFace1Vert2 > Tolerance.TOL ? 1
                        : (distFace1Vert2 < -Tolerance.TOL ? -1 : 0));
                signFace1Vert3 = (distFace1Vert3 > Tolerance.TOL ? 1
                        : (distFace1Vert3 < -Tolerance.TOL ? -1 : 0));

                // if all the signs are zero, the planes are coplanar
                // if all the signs are positive or negative, the
                // planes do not intersect

                // if the signs are equal...
                if (signFace1Vert1 == signFace1Vert2
                        && signFace1Vert2 == signFace1Vert3) {
                    continue;
                }
                // distance from the face2 vertices to the face1 plane
                distFace2Vert1 = computeDistance(face2.v1, face1);
                distFace2Vert2 = computeDistance(face2.v2, face1);
                distFace2Vert3 = computeDistance(face2.v3, face1);

                // distances signs from the face2 vertices to the face1 plane
                signFace2Vert1 = (distFace2Vert1 > Tolerance.TOL ? 1
                        : (distFace2Vert1 < -Tolerance.TOL ? -1 : 0));
                signFace2Vert2 = (distFace2Vert2 > Tolerance.TOL ? 1
                        : (distFace2Vert2 < -Tolerance.TOL ? -1 : 0));
                signFace2Vert3 = (distFace2Vert3 > Tolerance.TOL ? 1
                        : (distFace2Vert3 < -Tolerance.TOL ? -1 : 0));

                // if the signs are equal...
                if (signFace2Vert1 == signFace2Vert2
                        && signFace2Vert2 == signFace2Vert3) {
                    continue;
                }

                line = new Line(face1, face2);

                // intersection of the face1 and the plane of face2
                segment1 = new Segment(line, face1,
                        signFace1Vert1, signFace1Vert2,
                        signFace1Vert3);

                // intersection of the face2 and the plane of face1
                segment2 = new Segment(line, face2,
                        signFace2Vert1, signFace2Vert2,
                        signFace2Vert3);

                // if the two segments don't intersect...
                if (!segment1.intersect(segment2)) {
                    continue;
                }

                // PART II - SUBDIVIDING NON-COPLANAR POLYGONS

                // if the face in the position isn't the same, there was a break
                if (splitFace(face1idx, segment1, segment2)) {
                    // if the generated solid is equal the origin...
                    if (--insertionIdx > face1idx && getFace(insertionIdx).equals(face1)) {
                        //faces.remove(insertionIdx);
                        FacesRemoveFlags.set(insertionIdx, Boolean.TRUE);
                        FacesRemoveFlags.set(face1idx, Boolean.FALSE);
                        //FacesRemoveFlags.remove(insertionIdx);
                        System.out.println("face split reverted!");
                    } else {
                        ThreadNumFaceSplits++;
                        //System.out.println("split after " + j + " steps");
                        break;
                    }
                }
            }
        }
        if (ThreadNumFaceSplits != 0) {
            NumFaceSplits.addAndGet(ThreadNumFaceSplits);   // 1 or 0
        }
        if (ThreadNumFaceCompares != 0) {
            NumFaceCompares.addAndGet(ThreadNumFaceCompares);
        }
    }

    // --------------------------------------GETS------------------------------------//
    /**
     * Gets a face reference for a given position
     *
     * @param index required face position
     * @return face reference , null if the position is invalid
     */
    private Face getFace(int index) {
        try {
            return faces.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    // ------------------------------------ADDS----------------------------------------//
    /**
     * Method used to add a face properly for internal methods
     *
     * @param v1 a face vertex
     * @param v2 a face vertex
     * @param v3 a face vertex
     */
    private void addFace(Vertex v1, Vertex v2, Vertex v3) throws InterruptedException {
        if (!(v1.equals(v2) || v1.equals(v3) || v2.equals(v3))) {
            Face face = new Face(v1, v2, v3);
            if (face.getArea() > Tolerance.TOL) {// && !face.equals(getFace(facePos))) {
                synchronized (faces) {
                    faces.add(face);
                    //System.out.println("face added:");
                    //System.out.println(face.toString());
                    insertionIdx = faces.size();
                }
                FacesRemoveFlags.add(Boolean.FALSE);
            }
        }
    }

    /**
     * Method used to add a vertex properly for internal methods
     *
     * @param pos vertex position
     * @param color vertex color
     * @param status vertex status
     * @return the vertex inserted (if a similar vertex already exists, this is
     * returned)
     */
    private Vertex addVertex(Point3d pos, Color3f color, VertexStatus status) {
        int i, size;
        // if already there is an equal vertex, it is not inserted
        Vertex vertex = new Vertex(pos, color, status);
        synchronized (vertices) {
            size = vertices.size();
            for (i = size - 1; i > -1; i--) {
                if (vertex.equals(vertices.get(i))) {
                    break;
                }
            }
            if (i == -1) {
                vertices.add(vertex);
                return vertex;
            } else {
                vertex = vertices.get(i);
                vertex.setStatus(status);
                return vertex;
            }
        }
    }

    // -------------------------FACES_SPLITTING_METHODS------------------------------//
    /**
     * Computes closest distance from a vertex to a plane
     *
     * @param vertex vertex used to compute the distance
     * @param face face representing the plane where it is contained
     * @return the closest distance from the vertex to the plane
     */
    private double computeDistance(Vertex vertex, Face face) {
        Vector3d normal = face.getNormal();
        double a = normal.x;
        double b = normal.y;
        double c = normal.z;
        double d = -(a * face.v1.x + b * face.v1.y + c * face.v1.z);
        return a * vertex.x + b * vertex.y + c * vertex.z + d;
    }

    /**
     * Split an individual face
     *
     * @param facePos face position on the array of faces
     * @param segment1 segment representing the intersection of the face with
     * the plane of another face
     * @return segment2 segment representing the intersection of other face with
     * the plane of the current face plane
     */
    private boolean splitFace(int facePos, Segment segment1, Segment segment2)
            throws InterruptedException {
        Point3d startPos, endPos;
        int startType, endType, middleType;
        double startDist, endDist;

        Face face;
        Vertex startVertex = segment1.getStartVertex();
        Vertex endVertex = segment1.getEndVertex();

        // starting point: deeper starting point
        if (segment2.getStartDistance() > segment1.getStartDistance() + Tolerance.TOL) {
            startDist = segment2.getStartDistance();
            startType = segment1.getIntermediateType();
            startPos = segment2.getStartPosition();
        } else {
            startDist = segment1.getStartDistance();
            startType = segment1.getStartType();
            startPos = segment1.getStartPosition();
        }

        // ending point: deepest ending point
        if (segment2.getEndDistance() < segment1.getEndDistance() - Tolerance.TOL) {
            endDist = segment2.getEndDistance();
            endType = segment1.getIntermediateType();
            endPos = segment2.getEndPosition();
        } else {
            endDist = segment1.getEndDistance();
            endType = segment1.getEndType();
            endPos = segment1.getEndPosition();
        }
        middleType = segment1.getIntermediateType();

        // set vertex to BOUNDARY if it is start type
        if (startType == Segment.VERTEX) {
            startVertex.setStatus(VertexStatus.BOUNDARY);
        }

        // set vertex to BOUNDARY if it is end type
        if (endType == Segment.VERTEX) {
            endVertex.setStatus(VertexStatus.BOUNDARY);
        }

        // VERTEX-_______-VERTEX
        if (startType == Segment.VERTEX && endType == Segment.VERTEX) {
            return false;
        } // ______-EDGE-______
        else if (middleType == Segment.EDGE) {
            // gets the edge
            face = getFace(facePos);
            int splitEdge;
            if ((startVertex == face.v1 && endVertex == face.v2)
                    || (startVertex == face.v2 && endVertex == face.v1)) {
                splitEdge = 1;
            } else if ((startVertex == face.v2 && endVertex == face.v3)
                    || (startVertex == face.v3 && endVertex == face.v2)) {
                splitEdge = 2;
            } else {
                splitEdge = 3;
            }

            // VERTEX-EDGE-EDGE
            if (startType == Segment.VERTEX) {
                breakFaceInTwo(facePos, endPos, splitEdge);
            } // EDGE-EDGE-VERTEX
            else if (endType == Segment.VERTEX) {
                breakFaceInTwo(facePos, startPos, splitEdge);
            } // EDGE-EDGE-EDGE
            else if (startDist == endDist) {
                breakFaceInTwo(facePos, endPos, splitEdge);
            } else {
                if ((startVertex == face.v1 && endVertex == face.v2)
                        || (startVertex == face.v2 && endVertex == face.v3)
                        || (startVertex == face.v3 && endVertex == face.v1)) {
                    breakFaceInThree(facePos, startPos, endPos, splitEdge);
                } else {
                    breakFaceInThree(facePos, endPos, startPos, splitEdge);
                }
            }
            return true;
        } // ______-FACE-______
        // VERTEX-FACE-EDGE
        else if (startType == Segment.VERTEX && endType == Segment.EDGE) {
            breakFaceInTwo(facePos, endPos, endVertex);
            return true;
        } // EDGE-FACE-VERTEX
        else if (startType == Segment.EDGE && endType == Segment.VERTEX) {
            breakFaceInTwo(facePos, startPos, startVertex);
            return true;
        } // VERTEX-FACE-FACE
        else if (startType == Segment.VERTEX && endType == Segment.FACE) {
            breakFaceInThree(facePos, endPos, startVertex);
            return true;
        } // FACE-FACE-VERTEX
        else if (startType == Segment.FACE && endType == Segment.VERTEX) {
            breakFaceInThree(facePos, startPos, endVertex);
            return true;
        } // EDGE-FACE-EDGE
        else if (startType == Segment.EDGE && endType == Segment.EDGE) {
            breakFaceInThree(facePos, startPos, endPos, startVertex, endVertex);
            return true;
        } // EDGE-FACE-FACE
        else if (startType == Segment.EDGE && endType == Segment.FACE) {
            breakFaceInFour(facePos, startPos, endPos, startVertex);
            return true;
        } // FACE-FACE-EDGE
        else if (startType == Segment.FACE && endType == Segment.EDGE) {
            breakFaceInFour(facePos, endPos, startPos, endVertex);
            return true;
        } // FACE-FACE-FACE
        else if (startType == Segment.FACE && endType == Segment.FACE) {
            Vector3d segmentVector = new Vector3d(startPos.x - endPos.x,
                    startPos.y - endPos.y, startPos.z - endPos.z);

            // if the intersection segment is a point only...
            if (Math.abs(segmentVector.x) < Tolerance.TOL
                    && Math.abs(segmentVector.y) < Tolerance.TOL
                    && Math.abs(segmentVector.z) < Tolerance.TOL) {
                breakFaceInThree(facePos, startPos);
                return true;
            }

            // gets the vertex more lined with the intersection segment
            int linedVertex;
            Point3d linedVertexPos;
            face = getFace(facePos);
            Vector3d vertexVector = new Vector3d(endPos.x - face.v1.x, endPos.y
                    - face.v1.y, endPos.z - face.v1.z);
            vertexVector.normalize();
            double dot1 = Math.abs(segmentVector.dot(vertexVector));
            vertexVector = new Vector3d(endPos.x - face.v2.x, endPos.y
                    - face.v2.y, endPos.z - face.v2.z);
            vertexVector.normalize();
            double dot2 = Math.abs(segmentVector.dot(vertexVector));
            vertexVector = new Vector3d(endPos.x - face.v3.x, endPos.y
                    - face.v3.y, endPos.z - face.v3.z);
            vertexVector.normalize();
            double dot3 = Math.abs(segmentVector.dot(vertexVector));
            if (dot1 > dot2 && dot1 > dot3) {
                linedVertex = 1;
                linedVertexPos = face.v1.getPosition();
            } else if (dot2 > dot3 && dot2 > dot1) {
                linedVertex = 2;
                linedVertexPos = face.v2.getPosition();
            } else {
                linedVertex = 3;
                linedVertexPos = face.v3.getPosition();
            }

            // Now find which of the intersection endpoints is nearest to that
            // vertex.
            if (linedVertexPos.distance(startPos) > linedVertexPos.distance(endPos)) {
                breakFaceInFive(facePos, startPos, endPos, linedVertex);
            } else {
                breakFaceInFive(facePos, endPos, startPos, linedVertex);
            }
            return true;
        }
        return false;
    }

    /**
     * Face breaker for VERTEX-EDGE-EDGE / EDGE-EDGE-VERTEX
     *
     * @param facePos face position on the faces array
     * @param newPos new vertex position
     * @param edge that will be split
     */
    private void breakFaceInTwo(int facePos, Point3d newPos, int splitEdge)
            throws InterruptedException {
        Face face = faces.get(facePos);
        FacesRemoveFlags.set(facePos, Boolean.TRUE);

        Vertex vertex = addVertex(newPos, face.v1.getColor(), VertexStatus.BOUNDARY);

        if (splitEdge == 1) {
            addFace(face.v1, vertex, face.v3);
            addFace(vertex, face.v2, face.v3);
        } else if (splitEdge == 2) {
            addFace(face.v2, vertex, face.v1);
            addFace(vertex, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex, face.v2);
            addFace(vertex, face.v1, face.v2);
        }
    }

    /**
     * Face breaker for VERTEX-FACE-EDGE / EDGE-FACE-VERTEX
     *
     * @param facePos face position on the faces array
     * @param newPos new vertex position
     * @param endVertex vertex used for splitting
     */
    private void breakFaceInTwo(int facePos, Point3d newPos, Vertex endVertex)
            throws InterruptedException {
        Face face = faces.get(facePos);
        FacesRemoveFlags.set(facePos, Boolean.TRUE);

        Vertex vertex =
                addVertex(newPos, face.v1.getColor(), VertexStatus.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, vertex, face.v3);
            addFace(vertex, face.v2, face.v3);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, vertex, face.v1);
            addFace(vertex, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex, face.v2);
            addFace(vertex, face.v1, face.v2);
        }
    }

    /**
     * Face breaker for EDGE-EDGE-EDGE
     *
     * @param facePos face position on the faces array
     * @param newPos1 new vertex position
     * @param newPos2 new vertex position
     * @param splitEdge edge that will be split
     */
    private void breakFaceInThree(int facePos, Point3d newPos1,
            Point3d newPos2, int splitEdge) throws InterruptedException {
        Face face = faces.get(facePos);
        FacesRemoveFlags.set(facePos, Boolean.TRUE);

        Vertex vertex1 = addVertex(newPos1, face.v1.getColor(), VertexStatus.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, face.v1.getColor(), VertexStatus.BOUNDARY);

        if (splitEdge == 1) {
            addFace(face.v1, vertex1, face.v3);
            addFace(vertex1, vertex2, face.v3);
            addFace(vertex2, face.v2, face.v3);
        } else if (splitEdge == 2) {
            addFace(face.v2, vertex1, face.v1);
            addFace(vertex1, vertex2, face.v1);
            addFace(vertex2, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex1, face.v2);
            addFace(vertex1, vertex2, face.v2);
            addFace(vertex2, face.v1, face.v2);
        }
    }

    /**
     * Face breaker for VERTEX-FACE-FACE / FACE-FACE-VERTEX
     *
     * @param facePos face position on the faces array
     * @param newPos new vertex position
     * @param endVertex vertex used for the split
     */
    private void breakFaceInThree(int facePos, Point3d newPos, Vertex endVertex)
            throws InterruptedException {
        Face face = faces.get(facePos);
        FacesRemoveFlags.set(facePos, Boolean.TRUE);

        Vertex vertex = addVertex(newPos, face.v1.getColor(), VertexStatus.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, face.v2, vertex);
            addFace(face.v2, face.v3, vertex);
            addFace(face.v3, face.v1, vertex);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, face.v3, vertex);
            addFace(face.v3, face.v1, vertex);
            addFace(face.v1, face.v2, vertex);
        } else {
            addFace(face.v3, face.v1, vertex);
            addFace(face.v1, face.v2, vertex);
            addFace(face.v2, face.v3, vertex);
        }
    }

    /**
     * Face breaker for EDGE-FACE-EDGE
     *
     * @param facePos face position on the faces array
     * @param newPos1 new vertex position
     * @param newPos2 new vertex position
     * @param startVertex vertex used the new faces creation
     * @param endVertex vertex used for the new faces creation
     */
    private void breakFaceInThree(int facePos, Point3d newPos1,
            Point3d newPos2, Vertex startVertex, Vertex endVertex)
            throws InterruptedException {
        Face face = faces.get(facePos);
        FacesRemoveFlags.set(facePos, Boolean.TRUE);

        Vertex vertex1 = addVertex(newPos1, face.v1.getColor(), VertexStatus.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, face.v1.getColor(), VertexStatus.BOUNDARY);

        if (startVertex.equals(face.v1) && endVertex.equals(face.v2)) {
            addFace(face.v1, vertex1, vertex2);
            addFace(face.v1, vertex2, face.v3);
            addFace(vertex1, face.v2, vertex2);
        } else if (startVertex.equals(face.v2) && endVertex.equals(face.v1)) {
            addFace(face.v1, vertex2, vertex1);
            addFace(face.v1, vertex1, face.v3);
            addFace(vertex2, face.v2, vertex1);
        } else if (startVertex.equals(face.v2) && endVertex.equals(face.v3)) {
            addFace(face.v2, vertex1, vertex2);
            addFace(face.v2, vertex2, face.v1);
            addFace(vertex1, face.v3, vertex2);
        } else if (startVertex.equals(face.v3) && endVertex.equals(face.v2)) {
            addFace(face.v2, vertex2, vertex1);
            addFace(face.v2, vertex1, face.v1);
            addFace(vertex2, face.v3, vertex1);
        } else if (startVertex.equals(face.v3) && endVertex.equals(face.v1)) {
            addFace(face.v3, vertex1, vertex2);
            addFace(face.v3, vertex2, face.v2);
            addFace(vertex1, face.v1, vertex2);
        } else {
            addFace(face.v3, vertex2, vertex1);
            addFace(face.v3, vertex1, face.v2);
            addFace(vertex2, face.v1, vertex1);
        }
    }

    /**
     * Face breaker for FACE-FACE-FACE (a point only)
     *
     * @param facePos face position on the faces array
     * @param newPos new vertex position
     */
    private void breakFaceInThree(int facePos, Point3d newPos)
            throws InterruptedException {
        Face face = faces.get(facePos);
        FacesRemoveFlags.set(facePos, Boolean.TRUE);

        Vertex vertex = addVertex(newPos, face.v1.getColor(), VertexStatus.BOUNDARY);

        addFace(face.v1, face.v2, vertex);
        addFace(face.v2, face.v3, vertex);
        addFace(face.v3, face.v1, vertex);
    }

    /**
     * Face breaker for EDGE-FACE-FACE / FACE-FACE-EDGE
     *
     * @param facePos face position on the faces array
     * @param newPos1 new vertex position
     * @param newPos2 new vertex position
     * @param endVertex vertex used for the split
     */
    private void breakFaceInFour(int facePos, Point3d newPos1, Point3d newPos2,
            Vertex endVertex) throws InterruptedException {
        Face face = faces.get(facePos);
        FacesRemoveFlags.set(facePos, Boolean.TRUE);

        Vertex vertex1 = addVertex(newPos1, face.v1.getColor(), VertexStatus.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, face.v1.getColor(), VertexStatus.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, vertex1, vertex2);
            addFace(vertex1, face.v2, vertex2);
            addFace(face.v2, face.v3, vertex2);
            addFace(face.v3, face.v1, vertex2);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, vertex1, vertex2);
            addFace(vertex1, face.v3, vertex2);
            addFace(face.v3, face.v1, vertex2);
            addFace(face.v1, face.v2, vertex2);
        } else {
            addFace(face.v3, vertex1, vertex2);
            addFace(vertex1, face.v1, vertex2);
            addFace(face.v1, face.v2, vertex2);
            addFace(face.v2, face.v3, vertex2);
        }
    }

    /**
     * Face breaker for FACE-FACE-FACE
     *
     * @param facePos face position on the faces array
     * @param newPos1 new vertex position
     * @param newPos2 new vertex position
     * @param linedVertex what vertex is more lined with the interersection
     * found
     */
    private void breakFaceInFive(int facePos, Point3d newPos1, Point3d newPos2,
            int linedVertex) throws InterruptedException {
        Face face = faces.get(facePos);
        FacesRemoveFlags.set(facePos, Boolean.TRUE);

        Vertex vertex1 = addVertex(newPos1, face.v1.getColor(), VertexStatus.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, face.v1.getColor(), VertexStatus.BOUNDARY);

        if (linedVertex == 1) {
            addFace(face.v2, face.v3, vertex1);
            addFace(face.v2, vertex1, vertex2);
            addFace(face.v3, vertex2, vertex1);
            addFace(face.v2, vertex2, face.v1);
            addFace(face.v3, face.v1, vertex2);
        } else if (linedVertex == 2) {
            addFace(face.v3, face.v1, vertex1);
            addFace(face.v3, vertex1, vertex2);
            addFace(face.v1, vertex2, vertex1);
            addFace(face.v3, vertex2, face.v2);
            addFace(face.v1, face.v2, vertex2);
        } else {
            addFace(face.v1, face.v2, vertex1);
            addFace(face.v1, vertex1, vertex2);
            addFace(face.v2, vertex2, vertex1);
            addFace(face.v1, vertex2, face.v3);
            addFace(face.v2, face.v3, vertex2);
        }
    }
}
