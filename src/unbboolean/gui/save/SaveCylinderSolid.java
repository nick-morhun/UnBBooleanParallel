package unbboolean.gui.save;

import unbboolean.solids.CSGSolid;
import unbboolean.solids.CylinderSolid;

/**
 * Class representing a cylinder solid to be saved
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SaveCylinderSolid extends SavePrimitiveSolid {

    /**
     * cylinder ray in X
     */
    private double rayX;
    /**
     * cylinder ray in Z
     */
    private double rayZ;
    /**
     * cylinder height
     */
    private double height;

    /**
     * Constructs a SaveCylinderSolid object based on a CylinderSolid object
     *
     * @param solid cylinder solid to be saved
     */
    public SaveCylinderSolid(CylinderSolid solid) {
        super(solid);
        rayX = solid.getRayX();
        rayZ = solid.getRayZ();
        height = solid.getHeight();
    }

    /**
     * Gets the solid corresponding to this save solid
     *
     * @return the solid corresponding to this save solid
     */
    public CSGSolid getSolid() {
        CylinderSolid cylinder = new CylinderSolid(name, height, rayX, rayZ, color);
        return cylinder;
    }
}