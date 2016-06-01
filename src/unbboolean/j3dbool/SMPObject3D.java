package unbboolean.j3dbool;

import edu.rit.pj.PJProperties;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import unbboolean.solids.Solid;

/**
 * A class to represent a 3D solid and to apply boolean operations on it
 * in parallelized manner.
 * @author N. Morhun, initial author Danilo Balby Silva Castanheira
 */
public class SMPObject3D extends Object3D implements Cloneable {

    // Overriden in splitFaces()
    private int startIndex;
    private int endIndex;
    /**
     * Create a set of threads
     */
    private static ParallelTeam pt;

    static {
        pt = new ParallelTeam();
    }

    // ----------------------------------CONSTRUCTOR--------------------------//
    /**
     * Constructs a Object3d object based on a solid file.
     *
     * @param solid solid used to construct the Object3d object
     */
    public SMPObject3D(Solid solid) {
        super(solid);
    }

    // -----------------------------------OVERRIDES---------------------------//
    /**
     * Clones the Object3D object
     *
     * @return cloned Object3D object
     */
    public Object clone() {
        try {
            Object3D clone = (SMPObject3D) super.clone();
            return clone;
        } catch (Exception e) {
            return null;
        }
    }

    // -------------------------FACES_SPLITTING_METHODS-----------------------//
    /**
     * Split faces so that none face is intercepted by a face of other object
     *
     * @param object the other object 3d used to make the split
     * @throws Exception
     */
    public void splitFaces(Object3D object) {
        // Start timing.
        final Object3D theObject = object;
        long time = -System.currentTimeMillis();
        long NumFaceCompares;
        int NumFaceSplits;
        int NumFacesBefore = this.getNumFaces();
        int dFacesProcessed;
        int NumFaces;
        List<Boolean> theFlags = FaceSplitter.FacesRemoveFlags;
        int s;

        // If the objects bounds don't overlap...
        if (!this.getBound().overlap(object.getBound())) {
            return;
        }

        try {
            // Create object to execute in one thread.
            ParallelRegion pr = new ParallelRegion() {

                public void run() throws Exception {
                    execute(startIndex, endIndex,
                            new FaceSplitter(vertices, faces,
                            theObject));
                }
            };

            // for each object1 face...
            for (int i = 0; i < (NumFaces = this.getNumFaces());
                    i += dFacesProcessed) {

                dFacesProcessed = NumFaces - i;
                startIndex = i;
                endIndex = startIndex + dFacesProcessed - 1;
                // Start parallel execution.
                pt.execute(pr);

                // Removing old faces.
                s = theFlags.size();
//            System.out.println("\r\nflags: "+theFlags.toString());
                for (int k = NumFaces; k < s; k++) {
                    if (theFlags.get(k)) {
                        faces.remove(k);
                        theFlags.remove(k);
                        k--;
                        s--;
                    }
                }
            }

            NumFaceCompares = FaceSplitter.NumFaceCompares.get();
            NumFaceSplits = FaceSplitter.NumFaceSplits.get();

            // Removing old faces.
            s = theFlags.size();
//            System.out.println("\r\nflags: "+theFlags.toString());
            for (int k = 0, j = 0; k < s; k++, j++) {
                if (theFlags.get(k)) {
                    faces.remove(j);
                    --j;
                }
            }
            theFlags.clear();

            // Stop timing.
            time += System.currentTimeMillis();
            // Print results.
            System.out.println("SMPObject3D.splitFaces() v1.24 " + time + " ms with "
                    + PJProperties.getPjNt() + " threads.");
            System.out.println("Number of faces before: " + NumFacesBefore);
            System.out.println("Face comparisons made: " + NumFaceCompares);
            System.out.println("Face splits made: " + NumFaceSplits);
            System.out.println("Number of faces after: " + faces.size());
        } catch (Exception e) {
            Logger.getLogger(SMPObject3D.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
