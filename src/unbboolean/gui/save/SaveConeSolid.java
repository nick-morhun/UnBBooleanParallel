package unbboolean.gui.save;

import unbboolean.solids.CSGSolid;
import unbboolean.solids.ConeSolid;

/**
 * Class representing a cone solid to be saved
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SaveConeSolid extends SavePrimitiveSolid {

    /**
     * cone ray in X
     */
    private double rayX;
    /**
     * cone ray in Z
     */
    private double rayZ;
    /**
     * cone height
     */
    private double height;

    /**
     * Constructs a SaveConeSolid object based on a ConeSolid object
     *
     * @param solid cone solid to be saved
     */
    public SaveConeSolid(ConeSolid solid) {
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
        ConeSolid cone = new ConeSolid(name, height, rayX, rayZ, color);
        return cone;
    }
}
