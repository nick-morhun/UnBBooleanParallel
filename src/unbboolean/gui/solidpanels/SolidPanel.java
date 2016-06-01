package unbboolean.gui.solidpanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.vecmath.Vector3d;
import unbboolean.solids.Solid;
import unbboolean.solids.PrimitiveSolid;
import unbboolean.solids.SolidTransformationListener;

/**
 * Panel responsible to show solid features
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com) (modified)
 */
public abstract class SolidPanel extends JPanel implements SolidTransformationListener {

    /**
     * number of solids created based on this panel
     */
    protected int cont = 1;
    /**
     * default name of the solids to be created
     */
    protected String name;
    /**
     * name of the last solid created
     */
    protected String currentName;
    /**
     * button pressed when the user wants to change the solid color
     */
    protected JButton colorButton;
    /**
     * default border
     */
    protected Border border;
    /**
     * dialog window used to select a color
     */
    protected ColorChooserDialog colorDialog;
    /**
     * rotation
     */
    protected JSpinner rotateXField;
    protected JSpinner rotateYField;
    protected JSpinner rotateZField;
    /**
     * translation
     */
    protected JSpinner translateXField;
    protected JSpinner translateYField;
    protected JSpinner translateZField;
    /**
     * scale
     */
    protected JSpinner scaleXField;
    protected JSpinner scaleYField;
    protected JSpinner scaleZField;
    protected JPanel rotatePanel;
    protected JPanel translatePanel;

    /**
     * Constructs a default SolidPanel object
     */
    public SolidPanel() {
        colorDialog = new ColorChooserDialog();

        //color button
        colorButton = new JButton("   ");
        int buttonHeight = colorButton.getPreferredSize().height;
        colorButton.setPreferredSize(new Dimension(buttonHeight, buttonHeight));
        colorButton.setBackground(colorDialog.getSelectedColor());
        colorButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                colorDialog.setVisible(true);//show();
                colorButton.setBackground(colorDialog.getSelectedColor());
            }
        });

        // Transforms fields
        rotateXField = new JSpinner(new SpinnerNumberModel(0, -180.0, 180.0, 1.0));
        Dimension fieldDimension = new Dimension(60,
                rotateXField.getPreferredSize().height);
        rotateXField.setMaximumSize(fieldDimension);
        rotateYField = new JSpinner(new SpinnerNumberModel(0, -180.0, 180.0, 1.0));
        rotateYField.setMaximumSize(fieldDimension);
        rotateZField = new JSpinner(new SpinnerNumberModel(0, -180.0, 180.0, 1.0));
        rotateZField.setMaximumSize(fieldDimension);

        translateXField = new JSpinner(new SpinnerNumberModel(0, -100.0, 100.0, 0.1));
        translateXField.setMaximumSize(fieldDimension);
        translateYField = new JSpinner(new SpinnerNumberModel(0, -100.0, 100.0, 0.1));
        translateYField.setMaximumSize(fieldDimension);
        translateZField = new JSpinner(new SpinnerNumberModel(0, -100.0, 100.0, 0.1));
        translateZField.setMaximumSize(fieldDimension);

        border = BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151));

        rotatePanel = new JPanel();
        rotatePanel.setBorder(new TitledBorder(border, "Rotate"));

        translatePanel = new JPanel();
        translatePanel.setBorder(new TitledBorder(border, "Move"));

        JLabel xLabel = new JLabel("X"),
            yLabel = new JLabel("Y"),
            zLabel = new JLabel("Z"),
            xLabel1 = new JLabel("X"),
            yLabel1 = new JLabel("Y"),
            zLabel1 = new JLabel("Z");

        // Build rotatePanel layout.
        GroupLayout layout = new GroupLayout(rotatePanel);
        rotatePanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(xLabel)
                    .addComponent(rotateXField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(yLabel)
                    .addComponent(rotateYField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(zLabel)
                    .addComponent(rotateZField))
                .addGap(5, 5, 5))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xLabel)
                    .addComponent(yLabel)
                    .addComponent(zLabel))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rotateXField)
                    .addComponent(rotateYField)
                    .addComponent(rotateZField))
                .addContainerGap(5, 5)));

        // Build translatePanel layout.
        layout = new GroupLayout(translatePanel);
        translatePanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(xLabel1)
                    .addComponent(translateXField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(yLabel1)
                    .addComponent(translateYField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(zLabel1)
                    .addComponent(translateZField))
                .addGap(5, 5, 5))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xLabel1)
                    .addComponent(yLabel1)
                    .addComponent(zLabel1))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(translateXField)
                    .addComponent(translateYField)
                    .addComponent(translateZField))
                .addContainerGap(5, 5)));
    }

    /**
     * Sets the name of the last solid created
     *
     * @param name name of the last solid created
     */
    public void setCurrentName(String name) {
        currentName = name;
    }

    /**
     * Gets the next default name
     *
     * @return the next default name
     */
    public String getNextName() {
        cont++;
        currentName = name + cont;
        return currentName;
    }

    /**
     * Gets the name of the last solid created
     *
     * @return name of the last solid created
     */
    public String getCurrentName() {
        return currentName;
    }

    /**
     * Gets the last selected color
     *
     * @return last selected color
     */
    public Color getColor() {
        return colorButton.getBackground();
    }

    /**
     * Gets the selected rotation vector in radians
     *
     * @return the selected rotation vector
     */
    public Vector3d getSelectedRotation() {
        Vector3d v = new Vector3d();
        v.x = Math.PI / 180.0 * ((Double)rotateXField.getValue()).doubleValue();
        v.y = Math.PI / 180.0 * ((Double)rotateYField.getValue()).doubleValue();
        v.z = Math.PI / 180.0 * ((Double)rotateZField.getValue()).doubleValue();
        return v;
    }

    /**
     * Gets the selected scale vector
     *
     * @return the selected scale vector
     */
    public Vector3d getSelectedScale() {
        Vector3d v = new Vector3d();
        v.x = ((Double) scaleXField.getValue()).doubleValue();
        v.y = ((Double) scaleYField.getValue()).doubleValue();
        v.z = ((Double) scaleZField.getValue()).doubleValue();
        return v;
    }

    /**
     * Gets the selected translation vector
     *
     * @return the selected translation vector
     */
    public Vector3d getSelectedTranslation() {
        Vector3d v = new Vector3d();
        v.x = ((Double) translateXField.getValue()).doubleValue();
        v.y = ((Double) translateYField.getValue()).doubleValue();
        v.z = ((Double) translateZField.getValue()).doubleValue();
        return v;
    }

    /**
     * Sets the panel transformation values
     *
     * @param solid used to set the panel values
     */
    public void setValues(Solid solid) {
        rotateSolid(solid);
        translateSolid(solid);
    }

    /**
     * A solid was rotated
     *
     * @param solid rotated solid
     */
    public void rotateSolid(Solid solid) {
        if (solid != null) {
            rotateXField.setValue(Math.toDegrees(solid.getRotation().x));
            rotateYField.setValue(Math.toDegrees(solid.getRotation().y));
            rotateZField.setValue(Math.toDegrees(solid.getRotation().z));
        }
    }

    /**
     * A solid was translated
     *
     * @param solid translated solid
     */
    public void translateSolid(Solid solid) {
        if (solid != null) {
            translateXField.setValue(new Double(solid.getTranslation().x));
            translateYField.setValue(new Double(solid.getTranslation().y));
            translateZField.setValue(new Double(solid.getTranslation().z));
        }
    }

    /**
     * Gets the solid based on this panel values
     *
     * @return solid based on this panel values
     */
    public abstract Solid getSolid();
}