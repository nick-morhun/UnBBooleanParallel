package unbboolean.solids;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.vecmath.Color3f;
import unbboolean.gui.J3DBoolProgressListener;
import unbboolean.gui.solidpanels.InvalidBooleanOperationException;

/**
 * Solid class representing a component of a CSG Tree
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com) (modified)
 */
public abstract class CSGSolid extends Solid {

    /**
     * solid name
     */
    protected String name;

    /**
     * parent on a CSGTree
     */
    protected CompoundSolid parent;
    /**
     * if solid must be presented as wireframe or renderized normally
     */
    protected boolean wireframeView = false;

    /**
     * Constructs a default CSGSolid
     */
    public CSGSolid() {
        super();
        name = "solid";
        parent = null;
        defineAppearance();
    }

    // ----------------------------------------GETS-----------------------------------//
    /**
     * Gets the solid name
     *
     * @return solid name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the solid parent
     *
     * @return solid perent
     */
    public CompoundSolid getParentSolid() {
        return parent;
    }

    /**
     * Gets de depth of the solid on the CSG tree. The depth is the distance to
     * the root (the depth of the root is 0)
     *
     * @return depth of the solid on the CSG tree
     */
    public int getDepth() {
        if (parent != null) {
            return parent.getDepth() + 1;
        } else {
            return 0;
        }
    }

    // ----------------------------------------SETS-----------------------------------//
    /**
     * Sets the solid name
     *
     * @param name solid name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the solid parent
     *
     * @param parent solid parent
     */
    public void setParentSolid(CompoundSolid parent) {
        this.parent = parent;
    }

    // ------------------------------------LOCATION------------------------------------//
    /**
     * Updates the parent location - called when the the coordinates were
     * changed
     *
     * @param listener must be notified when an operation is executed
     * @return true if the user has canceled the process, false otherwise
     */
    public boolean updateParents(J3DBoolProgressListener listener)
            throws InvalidBooleanOperationException {
        if (parent != null) {
            return parent.updateItselfAndParents(listener);
        } else {
            return false;
        }
    }

    // ------------------------------------OTHERS---------------------------------//
    /**
     * Light the solid
     */
    public void light() {
        Appearance appearance = new Appearance();
        appearance.setCapability(Appearance.ALLOW_MATERIAL_READ);

        Material material = new Material();
        material.setCapability(Material.ALLOW_COMPONENT_READ);
        material.setDiffuseColor(1, 1, 1);
        material.setAmbientColor(1, 1, 1);
        material.setSpecularColor(0.0f, 0.0f, 0.0f);
        appearance.setMaterial(material);

        if (wireframeView) {
            PolygonAttributes polygonAtt = new PolygonAttributes();
            polygonAtt.setPolygonMode(PolygonAttributes.POLYGON_LINE);
            appearance.setPolygonAttributes(polygonAtt);
        }

        setAppearance(appearance);
    }

    /**
     * Unight the solid
     */
    public void unlight() {
        defineAppearance();
    }

    /**
     * Checks if this solid is lighten
     *
     * @return true if the solid is lighten, false otherwise
     */
    public boolean isLighted() {
        Color3f diffuseColor = new Color3f();
        getAppearance().getMaterial().getDiffuseColor(diffuseColor);
        if (diffuseColor.equals(new Color3f(1, 1, 1))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Defines if solid must be presented as wireframe or renderized normally
     *
     * @param wireframeView true to the solid be presented as wireframe, false
     * to be renderized normally
     */
    public void setWireframeView(boolean wireframeView) {
        this.wireframeView = wireframeView;
        light();
    }

    // ----------------------------------PRIVATES-------------------------------------//
    /**
     * Creates an appearance for the solid
     */
    private void defineAppearance() {
        Appearance appearance = new Appearance();
        appearance.setCapability(Appearance.ALLOW_MATERIAL_READ);

        Material material = new Material();
        material.setCapability(Material.ALLOW_COMPONENT_READ);
        material.setDiffuseColor(0.3f, 0.3f, 0.3f);
        material.setAmbientColor(0.3f, 0.3f, 0.3f);
        material.setSpecularColor(0.0f, 0.0f, 0.0f);
        appearance.setMaterial(material);

        if (wireframeView) {
            PolygonAttributes polygonAtt = new PolygonAttributes();
//            polygonAtt.setCullFace(PolygonAttributes.CULL_NONE);
            polygonAtt.setPolygonMode(PolygonAttributes.POLYGON_LINE);
            appearance.setPolygonAttributes(polygonAtt);
        }

        setAppearance(appearance);
    }

    // ----------------------------UNIMPLEMENTED------------------------------//
    /**
     * Copies the solid
     *
     * @return solid copy
     */
    public abstract CSGSolid copy();
}
