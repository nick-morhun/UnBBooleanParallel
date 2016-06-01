package unbboolean.gui.solidpanels;

import unbboolean.solids.*;

/**
 * Manage the use of primitive panels
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com) (modified)
 */
public class PrimitivePanelsManager {

    /**
     * panel for the box
     */
    private BoxPanel boxPanel;
    /**
     * panel for the sphere
     */
    private SpherePanel spherePanel;
    /**
     * panel for the cone
     */
    private ConePanel conePanel;
    /**
     * panel for the cylinder
     */
    private CylinderPanel cylinderPanel;
    /**
     * primitives list
     */
    private static final String[] primitivesList = {"box", "sphere", "cone", "cylinder"};

    /**
     * Constructs a default PrimitivePanelsManager object
     */
    public PrimitivePanelsManager() {
        boxPanel = new BoxPanel();
        spherePanel = new SpherePanel();
        conePanel = new ConePanel();
        cylinderPanel = new CylinderPanel();
    }

    /**
     * Gets the solid panel relative to a solid
     *
     * @param solid solid whose panel is required
     * @return solid panel required
     */
    public SolidPanel getSolidPanel(PrimitiveSolid solid) {
        if (solid instanceof BoxSolid) {
            boxPanel.setValues(solid);
            solid.setTransformationListener(boxPanel);
            return boxPanel;
        } else if (solid instanceof SphereSolid) {
            spherePanel.setValues(solid);
            solid.setTransformationListener(spherePanel);
            return spherePanel;
        } else if (solid instanceof ConeSolid) {
            conePanel.setValues(solid);
            solid.setTransformationListener(conePanel);
            return conePanel;
        } else if (solid instanceof CylinderSolid) {
            cylinderPanel.setValues(solid);
            solid.setTransformationListener(cylinderPanel);
            return cylinderPanel;
        } else {
            return null;
        }
    }

    /**
     * Gets the solid panel relative to a solid
     *
     * @param solid string defining a solid whose panel is required
     * @return solid panel required
     */
    public SolidPanel getSolidPanel(String solid) {
        switch (solid) {
            case "box":
                return boxPanel;
            case "sphere":
                return spherePanel;
            case "cone":
                return conePanel;
            case "cylinder":
                return cylinderPanel;
            default:
                return null;
        }
    }

    /**
     * Gets the solid panel relative to a solid
     *
     * @param pos position on the list of the solid whose panel is required
     * @return solid panel required
     */
    public SolidPanel getSolidPanel(int pos) {
        if (pos == 0) {
            return boxPanel;
        } else if (pos == 1) {
            return spherePanel;
        } else if (pos == 2) {
            return conePanel;
        } else if (pos == 3) {
            return cylinderPanel;
        } else {
            return null;
        }
    }

    /**
     * Gets the list of primitives
     *
     * @return the list of primitives
     */
    public String[] getPrimitivesList() {
        return primitivesList;
    }
}