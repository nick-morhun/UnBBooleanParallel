package unbboolean.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import unbboolean.gui.scenegraph.SceneGraphManager;
import unbboolean.gui.scenegraph.SolidsSelectionListener;
import unbboolean.gui.solidpanels.CompoundSolidPanel;
import unbboolean.gui.solidpanels.InvalidBooleanOperationException;
import unbboolean.gui.solidpanels.NewCompoundSolidPanel;
import unbboolean.gui.solidpanels.PrimitivePanelsManager;
import unbboolean.gui.solidpanels.SolidPanel;
import unbboolean.solids.Solid;
import unbboolean.solids.CSGSolid;
import unbboolean.solids.CompoundSolid;
import unbboolean.solids.PrimitiveSolid;

/**
 * Panel to show CSG Trees relative to solids and ways to change and combine
 * them.
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com) (modified)
 */
public class CSGPanel extends JPanel implements ActionListener, SolidsSelectionListener {

    /**
     * says if rightClickListener is enabled
     */
    private boolean isRightClickEnabled = false;
    /**
     * says if the move mode is on
     */
    private boolean moveMode = false;
    /**
     * default border to the tree panel
     */
    private Border border;
    /**
     * the UnBBoolean's main frame
     */
    private UnBBooleanFrame mainFrame;
    /**
     * the panel for compound solids
     */
    private CompoundSolidPanel compoundSolidPanel;
    /**
     * the panel for new compound solids
     */
    private NewCompoundSolidPanel newCompoundSolidPanel;
    /**
     * button to confirm change
     */
    private JButton okButton;
    /**
     * button to stop moving
     */
    private JButton okMoveButton;
    /**
     * tree representing the first selected solid
     */
    private JTree tree1;
    /**
     * tree representing the second selected solid
     */
    private JTree tree2;
    /**
     * listener to be trigged due to the right click on a tree
     */
    private MouseAdapter rightClickListener;
    /**
     * item which copies a primitive if it is selected
     */
    private JMenuItem copyItem;
    /**
     * item which copies a compound solid if it is selected
     */
    private JMenuItem copyNodeItem;
    /**
     * item which sets move mode if it is selected
     */
    private JMenuItem moveItem;
    /**
     * popup shown if a tree leaf is right clicked
     */
    private JPopupMenu leafPopup;
    /**
     * popup shown if a tree node is right clicked
     */
    private JPopupMenu nodePopup;
    /**
     * last primitive set to be move into the move mode
     */
    private Solid moveSolidPart;//PrimitiveSolid moveSolidPart;
    /**
     * backup of the last solid that had a part moved into the move mode
     */
    private CompoundSolid moveSolidBackup;
    /**
     * manages the primitive panels
     */
    private PrimitivePanelsManager panelsManager;
    /**
     * component that is on the center
     */
    private Component centerComponent;
    /**
     * component that is on the south
     */
    private Component southComponent;
    /**
     * progress monitor of the boolean operations
     */
    private J3DBoolProgressMonitor monitor;

    //----------------------------------CONSTRUCTORS---------------------------------//
    /**
     * Constructs an empty CSGPanel
     *
     * @param mainFrame UnBBoolean's main frame, the constructor caller
     */
    public CSGPanel(UnBBooleanFrame mainFrame) {
        setLayout(new BorderLayout());
        this.mainFrame = mainFrame;

        //INITIALIZE ATTRIBUTES

        border = BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151));
        panelsManager = new PrimitivePanelsManager();
        compoundSolidPanel = new CompoundSolidPanel();
        newCompoundSolidPanel = new NewCompoundSolidPanel();

        tree1 = new JTree();
        tree1.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                nodeSelected();
            }
        });
        tree2 = new JTree();

        rightClickListener = new MouseAdapter() {

            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    rightClickEvent(evt.getX(), evt.getY());
                }
            }
        };

        okButton = new JButton("OK");
        okButton.addActionListener(this);

        okMoveButton = new JButton("stop moving");
        okMoveButton.addActionListener(this);

        nodePopup = new JPopupMenu();
        copyNodeItem = new JMenuItem("copy");
        copyNodeItem.addActionListener(this);
        nodePopup.add(copyNodeItem);

        leafPopup = new JPopupMenu();
        copyItem = new JMenuItem("copy");
        copyItem.addActionListener(this);
        moveItem = new JMenuItem("move");
        moveItem.addActionListener(this);
        leafPopup.add(copyItem);
        leafPopup.add(moveItem);
    }

    //--------------------------SOLID_SELECTION_LISTENER----------------------------//
    /**
     * Shows the panel relative to a solid
     *
     * @param solid solid solid used to build the screen
     */
    public void selectSolid(CSGSolid solid) {
        deselectSolids();

        if (!isRightClickEnabled) {
            tree1.addMouseListener(rightClickListener);
            isRightClickEnabled = true;
        }

        JScrollPane treePanel = getTreePanel(solid, tree1);
        Dimension panelSize = new Dimension(
                (int) treePanel.getPreferredSize().getWidth(), (int) (getSize().getHeight()) / 3);
        treePanel.setPreferredSize(panelSize);
        add(treePanel, "North");

        expandTree(tree1);
        ((CSGSolid) tree1.getModel().getRoot()).unlight();

        if (solid instanceof CompoundSolid) {
            compoundSolidPanel.setValues((CompoundSolid) solid);
            solid.setTransformationListener(compoundSolidPanel);
            add(compoundSolidPanel, "Center");
        } else if (solid.getParentSolid() == null) {
            add(panelsManager.getSolidPanel((PrimitiveSolid) solid), "Center");
        }
        add(okButton, "South");

        mainFrame.setSaveEnabled(true);
        mainFrame.showCSGPanel();
        mainFrame.repaint();
    }

    /**
     * Show the panel relative to the combination of two solids
     *
     * @param solid1 first solid used to build the screen
     * @param solid2 second solid used to build the screen
     */
    public void selectSolids(CSGSolid solid1, CSGSolid solid2) {
        removeAll();

        if (isRightClickEnabled) {
            tree1.removeMouseListener(rightClickListener);
            isRightClickEnabled = false;
        }

        JPanel treesPanel = new JPanel(new BorderLayout());
        JScrollPane treePanel1 = getTreePanel(solid1, tree1);
        JScrollPane treePanel2 = getTreePanel(solid2, tree2);
        Dimension panelSize = new Dimension((int) treePanel1.getPreferredSize().getWidth(),
                (int) (getSize().getHeight()) / 4);
        treePanel1.setPreferredSize(panelSize);
        treePanel2.setPreferredSize(panelSize);
        treesPanel.add(treePanel1, "North");
        treesPanel.add(treePanel2, "South");
        add(treesPanel, "North");

        expandTree(tree1);
        expandTree(tree2);
        ((CSGSolid) tree1.getModel().getRoot()).unlight();
        ((CSGSolid) tree2.getModel().getRoot()).unlight();

        newCompoundSolidPanel.setValues(solid1, solid2);
        add(newCompoundSolidPanel, "Center");

        add(okButton, "South");

        mainFrame.setSaveEnabled(false);
        mainFrame.repaint();
    }

    /**
     * Empties the panel
     */
    public void deselectSolids() {
        if (this.getComponentCount() != 0) {
            ((CSGSolid) tree1.getModel().getRoot()).light();
            if (centerComponent == newCompoundSolidPanel) {
                ((CSGSolid) tree2.getModel().getRoot()).light();
            }
            removeAll();
        }
        removeAll();
        mainFrame.setSaveEnabled(false);
        mainFrame.repaint();
    }

    //-------------------------------------EVENTS------------------------------------//
    /**
     * Method called when an action item is selected
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        SceneGraphManager manager = mainFrame.getSceneGraphManager();
        if (e.getSource() == okButton) {
            if (moveMode) {
                applyChangeInMoveMode(manager);
            } else if (centerComponent == newCompoundSolidPanel) {
                System.out.println("Creating Compound Solid...");
                long time = -System.currentTimeMillis();
                applyBooleanOperation(manager);
                time += System.currentTimeMillis();
                System.out.println("Total construction time " + time + " ms");
                System.out.println();
            } else {
                applyChange(manager);
            }
        } else if (e.getSource() == moveItem) {
            setMoveMode(manager);
        } else if (e.getSource() == okMoveButton) {
            unsetMoveMode(manager);
        } else if (e.getSource() == copyItem || e.getSource() == copyNodeItem) {
            copySolid(manager);
        }
    }

    /**
     * Simulates OK button.
     */
    public void pressOK() {
        SceneGraphManager manager = mainFrame.getSceneGraphManager();
        if (moveMode) {
            applyChangeInMoveMode(manager);
        } else if (centerComponent == newCompoundSolidPanel) {
            System.out.println("Creating Compound Solid...");
            long time = -System.currentTimeMillis();
            applyBooleanOperation(manager);
            time += System.currentTimeMillis();
            System.out.println("Total construction time " + time + " ms");
            System.out.println();
        } else {
            applyChange(manager);
        }
    }

    /**
     * Apply changes performed in the move mode
     *
     * @param manager scene graph manager
     */
    private void applyChangeInMoveMode(SceneGraphManager manager) {
        //PrimitiveSolid oldMoveSolid = moveSolidPart;
        moveSolidPart = ((SolidPanel) centerComponent).getSolid();

        //manager.removeSolid(oldMoveSolid);
        //manager.addSolid(moveSolidPart);

        manager.setMoveMode((CSGSolid) moveSolidPart);
        selectSolid((CSGSolid) moveSolidPart);
        moveMode = true;
    }

    /**
     * Apply the selected boolean operation into the two selected solids
     *
     * @param manager scene graph manager
     */
    private void applyBooleanOperation(SceneGraphManager manager) {
        String name = newCompoundSolidPanel.getCurrentName();

        CSGSolid solid1, solid2;
        int operation, selection = newCompoundSolidPanel.getSelected();
        if (selection == CompoundSolidPanel.B_DIFFERENCE_A) {
            solid1 = (CSGSolid) tree2.getModel().getRoot();
            solid2 = (CSGSolid) tree1.getModel().getRoot();
            operation = CompoundSolid.DIFFERENCE;
        } else {
            solid1 = (CSGSolid) tree1.getModel().getRoot();
            solid2 = (CSGSolid) tree2.getModel().getRoot();
            operation = selection;
        }

        try {
            CompoundSolid cs = new CompoundSolid(name, operation, solid1, solid2);
            System.out.println("Result has " + cs.getVertices().length
                    + " vertices, " + cs.getIndices().length / 3 + " faces.");
            manager.addSolid(cs);
            manager.removeSelectedSolids();
            deselectSolids();
        } catch (InvalidBooleanOperationException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "The generated solid is empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Apply changes into the selected solid
     *
     * @param manager scene graph manager
     */
    private void applyChange(final SceneGraphManager manager) {
        final CSGSolid selectedSolid = (CSGSolid) tree1.getLastSelectedPathComponent();

        int numberOfOperations = selectedSolid.getDepth();
        if (selectedSolid instanceof CompoundSolid) {
            numberOfOperations++;
        }

        monitor = new J3DBoolProgressMonitor(numberOfOperations) {

            public void executeBooleanOperations() {
                int row = tree1.getSelectionRows()[0];
                CSGSolid root = (CSGSolid) tree1.getModel().getRoot();
                CSGSolid rootCopy = root.copy();

                try {
                    boolean cancelRequested = false;
                    if (selectedSolid instanceof CompoundSolid) {
                        CompoundSolid newCompound = ((CompoundSolidPanel) centerComponent).getSolid();//(CompoundSolid) selectedSolid;
                        //if (compoundSolidPanel.selectedChanged()) {
                            int selection = compoundSolidPanel.getSelected();
                            if (selection == CompoundSolidPanel.B_DIFFERENCE_A) {
                                cancelRequested = newCompound.setOperationToInverseDifference(monitor);
                            } else {
                                cancelRequested = newCompound.setOperation(selection, monitor);
                            }
                        //}
                        selectSolid(root);
                    } else {
                        CSGSolid newPrimitive = (CSGSolid) ((SolidPanel) centerComponent).getSolid();
                        //newPrimitive.updateLocation(((PrimitiveSolid)selectedSolid).getLocation());

                        if (!(root instanceof CompoundSolid)) {
                            manager.removeSelectedSolids();
                            manager.addSolid(newPrimitive);
                            selectSolid(newPrimitive);
                        } else {
                            CompoundSolid parent = selectedSolid.getParentSolid();
                            System.out.println("CSGPanel.applyChange()");
                            long time;

                            if (tree1.getModel().getIndexOfChild(parent, selectedSolid) == 0) {
                                time = -System.currentTimeMillis();
                                cancelRequested = parent.setOperand1(newPrimitive, monitor);
                                time += System.currentTimeMillis();
                            } else {
                                time = -System.currentTimeMillis();
                                cancelRequested = parent.setOperand2(newPrimitive, monitor);
                                time += System.currentTimeMillis();
                            }

                            System.out.println("Total time is " + time + " ms.");
                            System.out.println();
                            selectSolid(root);
                        }
                    }

                    if (cancelRequested) {
                        manager.removeSolid(root);
                        manager.addSolid(rootCopy);
                        selectSolid(rootCopy);
                    }
                } catch (InvalidBooleanOperationException e) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "An intermediate solid is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    manager.removeSolid(root);
                    manager.addSolid(rootCopy);
                    selectSolid(rootCopy);
                }
                tree1.setSelectionRow(row);
            }
        };
        monitor.start();
    }

    /**
     * Sets move mode on
     *
     * @param manager scene graph manager
     */
    private void setMoveMode(SceneGraphManager manager) {
        remove(southComponent);
        tree1.setEnabled(false);

        manager.removeSolid((CSGSolid) tree1.getModel().getRoot());
        for (int i = 0; i < tree1.getRowCount(); i++) {
            CSGSolid solid = (CSGSolid) tree1.getPathForRow(i).getLastPathComponent();
            if (solid instanceof PrimitiveSolid) {
                manager.addSolid(solid);
            }
        }
        moveSolidPart = (PrimitiveSolid) tree1.getLastSelectedPathComponent();
        moveSolidBackup = (CompoundSolid) ((CompoundSolid) tree1.getModel().getRoot()).copy();
        add(panelsManager.getSolidPanel((PrimitiveSolid) moveSolidPart), "Center");

        manager.setMoveMode((PrimitiveSolid) moveSolidPart);
        moveMode = true;

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        //buttonPanel.add(okButton);
        buttonPanel.add(okMoveButton);

        add(buttonPanel, "South");
        mainFrame.repaint();
    }

    /**
     * Sets move mode off
     *
     * @param manager scene graph manager
     */
    private void unsetMoveMode(final SceneGraphManager manager) {
        final PrimitiveSolid solid = (PrimitiveSolid) tree1.getLastSelectedPathComponent();
        int numberOfOperations = solid.getDepth();
        moveSolidPart = ((SolidPanel) centerComponent).getSolid();
        monitor = new J3DBoolProgressMonitor(numberOfOperations) {

            public void executeBooleanOperations() {
                int row = tree1.getSelectionRows()[0];
                CSGSolid root = (CSGSolid) tree1.getModel().getRoot();
                try {
                    boolean cancelRequested = false;
                    if (solid != moveSolidPart) {
                        // add new operand
                        CompoundSolid parent = solid.getParentSolid();
                        if (tree1.getModel().getIndexOfChild(parent, solid) == 0) {
                            cancelRequested = parent.setOperand1((CSGSolid) moveSolidPart, monitor);
                        } else {
                            cancelRequested = parent.setOperand2((CSGSolid) moveSolidPart, monitor);
                        }
                        manager.removeSolid(solid);
                    } else {
                        // same child
                        int y = tree1.getModel().getIndexOfChild(root, solid);
                        //if (solid.rotation != )
                        cancelRequested = solid.updateParents(monitor);
                    }

                    if (cancelRequested) {
                        root = moveSolidBackup;
                    }
                    //time += System.currentTimeMillis();
                    //System.out.println("Total time is " + time + " ms.");
                    //System.out.println();
                } catch (InvalidBooleanOperationException e) {
                    root = moveSolidBackup;
                    JOptionPane.showMessageDialog(mainFrame, "An intermediate solid is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                CSGSolid treeSolid;
                for (int i = 0; i < tree1.getRowCount(); i++) {
                    treeSolid = (CSGSolid) tree1.getPathForRow(i).getLastPathComponent();
                    if (treeSolid instanceof PrimitiveSolid) {
                        manager.removeSolid(treeSolid);
                    }
                }

                manager.addSolid(root);
                manager.unsetMoveMode(root);
                moveMode = false;

                tree1.setEnabled(true);
                remove(southComponent);
                add(okButton, "South");
                selectSolid(root);
                tree1.setSelectionRow(row);
            }
        };
        monitor.start();
    }

    /**
     * Copies the selected solid
     *
     * @param manager scene graph manager
     */
    private void copySolid(SceneGraphManager manager) {
        CSGSolid selectedSolid = (CSGSolid) tree1.getLastSelectedPathComponent();
        CSGSolid clone = selectedSolid.copy();
        selectSolid(clone);
        manager.addSolid(clone);
    }

    /**
     * Performs changes on the screen when a new node is selected
     *
     * @param manager scene graph manager
     */
    private void nodeSelected() {
        //if two solids aren't selected: show the selected solid screen
        if (getComponentCount() != 0 && centerComponent != newCompoundSolidPanel) {
            remove(centerComponent);
            CSGSolid solid = (CSGSolid) tree1.getLastSelectedPathComponent();
            if (solid instanceof CompoundSolid) {
                compoundSolidPanel.setValues((CompoundSolid) solid);
                solid.setTransformationListener(compoundSolidPanel);
                add(compoundSolidPanel, "Center");
            } else if (solid.getParentSolid() == null) {
                add(panelsManager.getSolidPanel((PrimitiveSolid) solid), "Center");
            }

            mainFrame.repaint();
        }
    }

    /**
     * Shows a popup menu when the user right clicks the tree
     *
     * @param manager scene graph manager
     */
    private void rightClickEvent(int x, int y) {
        TreePath path = tree1.getPathForLocation(x, y);
        if (path != null) {
            tree1.setSelectionPath(path);
            CSGSolid solid = (CSGSolid) path.getLastPathComponent();
            if (solid instanceof CompoundSolid || solid == tree1.getModel().getRoot()) {
                nodePopup.show(tree1, x, y);
            } else {
                leafPopup.show(tree1, x, y);
            }
        }
    }

    //--------------------------------------OTHERS-------------------------------------//
    /**
     * Returns the selected solid
     *
     * @return selected solid
     */
    public CSGSolid getSelectedSolid() {
        return (CSGSolid) tree1.getModel().getRoot();
    }

    /**
     * Returns the move mode status
     *
     * @return true if move mode is on, otherwise false
     */
    public boolean isMoveMode() {
        return moveMode;
    }

    /**
     * Adds a component on the screen, according the given constraint
     *
     * @param comp component to be added into the screen
     * @param constraints constraint about where component shall be put (String
     * expected)
     */
    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
        String pos = (String) constraints;
        switch (pos) {
            case "Center":
                centerComponent = comp;
                break;
            case "South":
                southComponent = comp;
                break;
        }
    }

    //-------------------------------------PRIVATES------------------------------------//
    /**
     * Expand all the nodes of the tree
     *
     * @param tree tree to be expanded
     */
    private void expandTree(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandPath(tree.getPathForRow(i));
        }
    }

    /**
     * Assembles a tree panel for a solid structure
     *
     * @param solid whose tree will be shown
     * @param tree tree that will be assembled
     * @return panel assebled
     */
    private JScrollPane getTreePanel(CSGSolid solid, JTree tree) {
        tree.setModel(new CSGTreeModel(solid));
        tree.setSelectionRow(0);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scrollTreePanel = new JScrollPane(tree);
        scrollTreePanel.setBorder(new TitledBorder(border, solid.getName()));
        return scrollTreePanel;
    }
}
