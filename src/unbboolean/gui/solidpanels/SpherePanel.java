package unbboolean.gui.solidpanels;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.vecmath.Color3f;
import unbboolean.solids.PrimitiveSolid;
import unbboolean.solids.SphereSolid;

/**
 * Panel responsible to show sphere features
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SpherePanel extends SolidPanel {

    /**
     * sphere ray in X
     */
    private JSpinner rayXField;
    /**
     * sphere ray in Y
     */
    private JSpinner rayYField;
    /**
     * sphere ray in Z
     */
    private JSpinner rayZField;

    SphereSolid theSphere;

    /**
     * Constructs a default SpherePanel object
     */
    public SpherePanel() {
        this(5, 5, 5);
    }

    /**
     * Constructs a panel setting the initial values
     *
     * @param rayX sphere ray in X
     * @param rayY sphere ray in Y
     * @param rayZ sphere ray in Z
     */
    public SpherePanel(double rayX, double rayY, double rayZ) {
        name = "sphere";
        currentName = name + cont;

        setBorder(new TitledBorder(border, "Sphere Properties"));

        // Dimension fields
        rayXField = new JSpinner(new SpinnerNumberModel(rayX, 0.1, 100, 0.1));
        Dimension fieldDimension = new Dimension(60, rayXField.getPreferredSize().height);
        rayXField.setPreferredSize(fieldDimension);
        rayYField = new JSpinner(new SpinnerNumberModel(rayY, 0.1, 100, 0.1));
        rayYField.setPreferredSize(fieldDimension);
        rayZField = new JSpinner(new SpinnerNumberModel(rayZ, 0.1, 100, 0.1));
        rayZField.setPreferredSize(fieldDimension);

        JLabel colorLabel = new JLabel("color:"),
            rayXLabel = new JLabel("ray in X:"),
            rayYLabel = new JLabel("ray in Y:"),
            rayZLabel = new JLabel("ray in Z:");

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
                    .addComponent(rayYLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 29, 29)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(colorButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(rayXField)
                    .addComponent(rayYField)
                    .addComponent(rayZField))
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
                    .addComponent(rayYField))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rayYLabel)
                    .addComponent(rayZField))
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
     * Gets the selected ray in Y
     *
     * @return the selected ray in Y
     */
    public double getSelectedRayY() {
        return ((Double) rayYField.getValue()).doubleValue();
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
     * Sets the panel values
     *
     * @param solid used to set the panel values
     */
    public void setValues(PrimitiveSolid solid) {
        super.setValues(solid);
        if (solid instanceof SphereSolid) {
            theSphere = (SphereSolid) solid;
            name = theSphere.getName();
            setBorder(new TitledBorder(border, name + " Properties"));
            colorButton.setBackground((theSphere.getColors()[0]).get());
            rayXField.setValue(new Double(theSphere.getRayX()));
            rayYField.setValue(new Double(theSphere.getRayY()));
            rayZField.setValue(new Double(theSphere.getRayZ()));
        }
    }

    /**
     * Gets the solid based on this panel values
     *
     * @return solid based on this panel values
     */
    public PrimitiveSolid getSolid() {
        double rayX, rayY, rayZ;
        rayX = getSelectedRayX();
        rayY = getSelectedRayY();
        rayZ = getSelectedRayZ();
        Color3f color = new Color3f(getColor());

        if (theSphere == null ||
                (rayX != theSphere.getRayX() || rayY != theSphere.getRayY()
                || rayZ != theSphere.getRayZ())) {
            theSphere = new SphereSolid(name, rayX, rayY, rayZ, color);
        }
        else {
            theSphere.setColor(color);
            // Prevent removal
            theSphere.light();
        }

        theSphere.setRotation(getSelectedRotation());
        theSphere.setTranslation(getSelectedTranslation());
        //scale = getSelectedScale();

        // Set defaults for the new object
        SphereSolid s = theSphere;
        theSphere = null;
        rotateXField.setValue(0d);
        rotateYField.setValue(0d);
        rotateZField.setValue(0d);
        translateXField.setValue(0d);
        translateYField.setValue(0d);
        translateZField.setValue(0d);
        return s;
    }
}