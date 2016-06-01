package unbboolean.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import unbboolean.gui.scenegraph.SceneGraphManager;
import unbboolean.gui.solidpanels.PrimitivePanelsManager;
import unbboolean.gui.solidpanels.SolidPanel;
import unbboolean.solids.PrimitiveSolid;

/**
 * Panel used to create new primitives.
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class PrimitivesPanel extends JPanel implements ActionListener {

    /**
     * button selected to create a new primitive
     */
    private JButton primitiveButton;
    /**
     * list of primitives
     */
    private JComboBox<String> primitivesList;
    /**
     * panel in the center
     */
    private SolidPanel centerPanel;
    /**
     * manager of the scene graph that contains the solid
     */
    private SceneGraphManager sceneGraphManager;
    /**
     * UnBBoolean's main frame
     */
    private JFrame mainFrame;
    /**
     * field where the name of the new primitive is inserted
     */
    private JTextField primitiveNameField;
    /**
     * manager of the primitives panels
     */
    private PrimitivePanelsManager panelsManager;

    /**
     * Construct a PrimitivesPanel object with default layout
     *
     * @param mainFrame UnBBoolean's main frame
     * @param sceneGraphManager manager of the scene graph that contains the
     * solid
     */
    public PrimitivesPanel(JFrame mainFrame, SceneGraphManager sceneGraphManager) {
        setLayout(new BorderLayout());

        this.mainFrame = mainFrame;
        this.sceneGraphManager = sceneGraphManager;

        //general primitives panel
        JPanel generalPrimitivesPanel = new JPanel();
        Border defaultBorder = BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151));
        generalPrimitivesPanel.setBorder(new TitledBorder(defaultBorder, "Primitive"));
        //primitive name
        primitiveNameField = new JTextField(6);

        panelsManager = new PrimitivePanelsManager();

        //primitives list
        String[] primitivesNames = panelsManager.getPrimitivesList();
        primitivesList = new JComboBox<>(primitivesNames);
        primitivesList.addActionListener(this);

        //confirm button
        primitiveButton = new JButton("OK");
        primitiveButton.addActionListener(this);

        //primitives panel hierarchy
        generalPrimitivesPanel.add(primitiveNameField);
        generalPrimitivesPanel.add(primitivesList);
        add(generalPrimitivesPanel, "North");
        centerPanel = panelsManager.getSolidPanel(0);
        add(centerPanel, "Center");
        primitiveNameField.setText(centerPanel.getCurrentName());
        add(primitiveButton, "South");
    }

    /**
     * Method called when an action item is selected.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == primitivesList) {
            changePrimitive();
        } else if (source == primitiveButton) {
            addPrimitive();
        }
    }

    /**
     * Changes the primitive panel shown.
     */
    public void changePrimitive() {
        SolidPanel panel = centerPanel;
        panel.setCurrentName(primitiveNameField.getText());
        remove(centerPanel);

        String option = (String) primitivesList.getSelectedItem();
        centerPanel = panelsManager.getSolidPanel(option);
        add(centerPanel, "Center");
        primitiveNameField.setText(centerPanel.getCurrentName());
        mainFrame.repaint();
    }

    /**
     * Creates a new primitive and adds it into the screen
     */
    public void addPrimitive() {
        PrimitiveSolid newSolid = (PrimitiveSolid)centerPanel.getSolid();
        newSolid.setName(primitiveNameField.getText());
        sceneGraphManager.addSolid(newSolid);
        primitiveNameField.setText(centerPanel.getNextName());
    }
}