package unbboolean.gui.save;

import javax.vecmath.Color3f;
import unbboolean.gui.J3DBoolProgressListener;
import unbboolean.solids.CSGSolid;
import unbboolean.solids.PrimitiveSolid;

/**
 * Class representing a primitive solid to be saved
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com) (modified)
 */
public abstract class SavePrimitiveSolid extends SaveSolid {

    /**
     * primitive color
     */
    protected Color3f color;
    private static final long serialVersionUID = 457294504100634355L;

    /**
     * Constructs a SavePrimitiveSolid object based on a PrimitiveSolid object
     *
     * @param solid primitive solid to be saved
     */
    public SavePrimitiveSolid(PrimitiveSolid solid) {
        super(solid);
        color = solid.getColor();
    }

    /**
     * Gets the number of operations used to create the solid (the number of
     * nodes on CSG tree)
     *
     * @return the number of operations used to create the solid
     */
    public int getNumberOfOperations() {
        return 0;
    }

    /**
     * Gets the solid corresponding to this save solid
     *
     * @param listener must be notified when an operation is executed
     * @return the solid corresponding to this save solid
     */
    public CSGSolid getSolid(J3DBoolProgressListener listener) {
        return getSolid();
    }

    /**
     * Gets the solid corresponding to this save solid
     *
     * @return the solid corresponding to this save solid
     */
    public abstract CSGSolid getSolid();
}