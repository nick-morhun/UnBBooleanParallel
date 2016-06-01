package unbboolean.gui.solidpanels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.vecmath.*;

import unbboolean.solids.*;

/**
 * Panel responsible to show sphere features
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class CylinderPanel extends SolidPanel {

    /**
     * cylinder ray in X
     */
    private JSpinner rayXField;
    /**
     * cylinder ray in Z
     */
    private JSpinner rayZField;
    /**
     * cylinder height
     */
    private JSpinner heightField;
    private CylinderSolid theCylinder;

    /**
     * Constructs a default CylinderPanel object
     */
    public CylinderPanel() {
        this(5, 5, 10);
    }

    /**
     * Constructs a panel setting the initial values
     *
     * @param rayX cylinder ray in X
     * @param rayZ cylinder ray in Z
     * @param height cylinder height
     */
    public CylinderPanel(double rayX, double rayZ, double height) {
        name = "cylinder";
        currentName = name + cont;

        setBorder(new TitledBorder(border, "Cylinder Properties"));

        // Dimension fields
        rayXField = new JSpinner(new SpinnerNumberModel(rayX, 0.1, 100, 0.1));
        Dimension fieldDimension = new Dimension(60, rayXField.getPreferredSize().height);
        rayXField.setPreferredSize(fieldDimension);
        rayZField = new JSpinner(new SpinnerNumberModel(rayZ, 0.1, 100, 0.1));
        rayZField.setPreferredSize(fieldDimension);
        heightField = new JSpinner(new SpinnerNumberModel(height, 0.1, 100, 0.1));
        heightField.setPreferredSize(fieldDimension);

        JLabel colorLabel = new JLabel("color:"),
                rayXLabel = new JLabel("ray in X:"),
                rayZLabel = new JLabel("ray in Z:"),
                heightLabel = new JLabel("height:");

        // Build this panel layout.
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(colorLabel)
                    .addComponent(rayXLabel)
                    .addComponent(rayZLabel)
                    .addComponent(heightLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 29, 29)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(colorButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(rayXField)
                    .addComponent(rayZField)
                    .addComponent(heightField))
                .addGap(80, 80, 80))
            // Transforms
            .addGroup(layout.createSequentialGroup()
                .addComponent(rotatePanel))
            .addGroup(layout.createSequentialGroup()
                .addComponent(translatePanel))
            .addGap(10, 10, 10));

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(colorLabel)
                    .addComponent(colorButton))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rayXLabel)
                    .addComponent(rayXField))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rayZLabel)
                    .addComponent(rayZField))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(heightLabel)
                    .addComponent(heightField))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rotatePanel))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(translatePanel))
                .addContainerGap(6, Short.MAX_VALUE)));
    }

    /**
     * Gets the selected ray in X
     *
     * @return the selected ray in X
     */
    public double getSelectedRayX() {
        return ((Double) rayXField.getValue()).doubleValue();
    }

    /**
     * Gets the selected ray in Z
     *
     * @return the selected ray in Z
     */
    public double getSelectedRayZ() {
        return ((Double) rayZField.getValue()).doubleValue();
    }

    /**
     * Gets the selected height
     *
     * @return the selected height
     */
    public double getSelectedHeight() {
        return ((Double) heightField.getValue()).doubleValue();
    }

    /**
     * Sets the panel values
     *
     * @param solid used to set the panel values
     */
    public void setValues(PrimitiveSolid solid) {
        super.setValues(solid);
        if (solid instanceof CylinderSolid) {
            theCylinder = (CylinderSolid) solid;
            name = theCylinder.getName();
            setBorder(new TitledBorder(border, name + " Properties"));
            colorButton.setBackground((theCylinder.getColors()[0]).get());
            rayXField.setValue(new Double(theCylinder.getRayX()));
            rayZField.setValue(new Double(theCylinder.getRayZ()));
            heightField.setValue(new Double(theCylinder.getHeight()));
        }
    }

    /**
     * Gets the solid based on this panel values
     *
     * @return solid based on this panel values
     */
    public PrimitiveSolid getSolid() {
        double height, rayX, rayZ;
        height = getSelectedHeight();
        rayX = getSelectedRayX();
        rayZ = getSelectedRayZ();
        Color3f color = new Color3f(getColor());

        if (theCylinder == null
                || (height != theCylinder.getHeight() || rayX != theCylinder.getRayX()
                || rayZ != theCylinder.getRayZ())) {
            theCylinder = new CylinderSolid(name, height, rayX, rayZ, color);
        } else {
            theCylinder.setColor(color);
            // Prevent removal
            theCylinder.light();
        }

        theCylinder.setRotation(getSelectedRotation());
        theCylinder.setTranslation(getSelectedTranslation());
        //scale = getSelectedScale();

        // Set defaults for the new object
        CylinderSolid c = theCylinder;
        theCylinder = null;
        rotateXField.setValue(0d);
        rotateYField.setValue(0d);
        rotateZField.setValue(0d);
        translateXField.setValue(0d);
        translateYField.setValue(0d);
        translateZField.setValue(0d);
        return c;
    }
}