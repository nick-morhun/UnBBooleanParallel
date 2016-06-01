package unbboolean.gui.solidpanels;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.vecmath.Color3f;
import unbboolean.solids.BoxSolid;
import unbboolean.solids.PrimitiveSolid;

/**
 * Panel responsible to show box features
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com) (modified)
 */
public class BoxPanel extends SolidPanel {

    /**
     * box length
     */
    private JSpinner lengthField;
    /**
     * box width
     */
    private JSpinner widthField;
    /**
     * box height
     */
    private JSpinner heightField;

    private BoxSolid theBox;

    /**
     * Constructs a default BoxPanel object
     */
    public BoxPanel() {
        this(8, 8, 8);
    }

    /**
     * Construct a panel setting the initial values
     *
     * @param length box length
     * @param width box width
     * @param height box height
     */
    public BoxPanel(double length, double width, double height) {
        name = "box";
        currentName = name + cont;

        setBorder(new TitledBorder(border, "Box Properties"));

        // Dimension fields
        lengthField = new JSpinner(new SpinnerNumberModel(length, 0.1, 100, 0.1));
        Dimension fieldDimension = new Dimension(60,
                lengthField.getPreferredSize().height);
        lengthField.setPreferredSize(fieldDimension);
        widthField = new JSpinner(new SpinnerNumberModel(width, 0.1, 100, 0.1));
        widthField.setPreferredSize(fieldDimension);
        heightField = new JSpinner(new SpinnerNumberModel(height, 0.1, 100, 0.1));
        heightField.setPreferredSize(fieldDimension);

        JLabel colorLabel = new JLabel("color:"),
            lengthLabel = new JLabel("length:"),
            widthLabel = new JLabel("width:"),
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
                    .addComponent(lengthLabel)
                    .addComponent(heightLabel)
                    .addComponent(widthLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 29, 29)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(colorButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lengthField)
                    .addComponent(heightField)
                    .addComponent(widthField))
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
                    .addComponent(lengthLabel)
                    .addComponent(lengthField))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(heightLabel)
                    .addComponent(heightField))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(widthLabel)
                    .addComponent(widthField))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rotatePanel))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(translatePanel))
                .addContainerGap(6, Short.MAX_VALUE)));
    }

    /**
     * Gets the selected length
     *
     * @return the selected length
     */
    public double getSelectedLength() {
        return ((Double) lengthField.getValue()).doubleValue();
    }

    /**
     * Gets the selected width
     *
     * @return the selected width
     */
    public double getSelectedWidth() {
        return ((Double) widthField.getValue()).doubleValue();
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
     * Sets the panel values from the solid
     *
     * @param solid used to set the panel values
     */
    public void setValues(PrimitiveSolid solid) {
        super.setValues(solid);
        if (solid instanceof BoxSolid) {
            theBox = (BoxSolid) solid;
            name = solid.getName();
            setBorder(new TitledBorder(border, name + " Properties"));
            colorButton.setBackground((solid.getColors()[0]).get());
            lengthField.setValue(new Double(theBox.getLength()));
            widthField.setValue(new Double(theBox.getWidth()));
            heightField.setValue(new Double(theBox.getHeight()));
        }
    }

    /**
     * Gets the solid based on this panel values
     *
     * @return solid based on this panel values
     */
    public PrimitiveSolid getSolid() {
        double height, width, length;
        height = getSelectedHeight();
        length = getSelectedLength();
        width = getSelectedWidth();
        Color3f color = new Color3f(getColor());

        if (theBox == null ||
                (height != theBox.getHeight() || width != theBox.getWidth()
                || length != theBox.getLength())) {
            theBox = new BoxSolid(name, length, height, width, color);
        }
        else {
            theBox.setColor(color);
            // Prevent removal
            theBox.light();
        }

        theBox.setRotation(getSelectedRotation());
        theBox.setTranslation(getSelectedTranslation());
        //scale = getSelectedScale();

        // Set defaults for the new object
        BoxSolid b = theBox;
        theBox = null;
        rotateXField.setValue(0d);
        rotateYField.setValue(0d);
        rotateZField.setValue(0d);
        translateXField.setValue(0d);
        translateYField.setValue(0d);
        translateZField.setValue(0d);
        return b;
    }
}