package unbboolean.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.swing.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import unbboolean.Text;

import unbboolean.gui.save.CSGFilter;
import unbboolean.gui.save.CSGsceneFilter;
import unbboolean.gui.save.ObjFilter;
import unbboolean.gui.save.SaveSolid;
import unbboolean.gui.scenegraph.SceneGraphManager;
import unbboolean.j3dbool.BooleanModeller;
import unbboolean.solids.CSGSolid;

/**
 * UnBBoolean's main frame.
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com) (modified)
 */
public class UnBBooleanFrame extends JFrame implements ActionListener {

    /**
     * item to save a solid
     */
    private JMenuItem saveMenuItem;
    /**
     * item to load a solid
     */
    private JMenuItem loadMenuItem;
    /**
     * item to finish the program
     */
    private JMenuItem exitMenuItem;
    /**
     * item to show a help page
     */
    private JMenuItem helpMenuItem;
    /**
     * item to show information about the application
     */
    private JMenuItem aboutMenuItem;
    /**
     * check box to change to wireframe view
     */
    private JCheckBox wireframeViewCheckBox;
    /**
     * check box to change to moving objects with mouse
     */
    private JCheckBox mouseTranslateCheckBox;
    private JCheckBox testCheckBox;
    private JMenuItem resetMenuItem;
    private ButtonGroup booleansBG;
    private JRadioButtonMenuItem v1100RadioButton;
    private JRadioButtonMenuItem v1240RadioButton;
    /**
     * panel where the canvas is
     */
    private JPanel canvasPanel;
    /**
     * manager of the scene graph where the solids are
     */
    private SceneGraphManager sceneGraphManager;
    /**
     * panel where the options panels are set
     */
    private JTabbedPane optionsPanel;
    /**
     * panel to edit solids structures
     */
    private CSGPanel csgPanel;
    /**
     * dialog window to load solids
     */
    private JFileChooser solidLoader;
    /**
     * dialog window to save solids
     */
    private JFileChooser solidSaver;
    /**
     * progress monitor of the boolean operations
     */
    private J3DBoolProgressMonitor monitor;
    /**
     * item to save a scene
     */
    private JMenuItem saveAllMenuItem;
    private boolean testMode = false;

    /**
     * Constructs a UnBBoolean object with the initial configuration.
     */
    public UnBBooleanFrame() {
        int width = 900;
        int height = 740;

        setTitle(Text.NAME + " " + Text.VERSION);
        Container contentPane = this.getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // center screen
        setSize(width, height);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.width > width && screenSize.height > height) {
            setLocation((screenSize.width - width) / 2,
                    (screenSize.height - height) / 2);
        }

        Dimension minimumSize = new Dimension(width / 2, height);
        this.setMinimumSize(minimumSize);

        // lines to work out the conflict between swing and canvas3d
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

        // 1 - MENU_BAR
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        loadMenuItem = new JMenuItem("Load");
        loadMenuItem.addActionListener(this);
        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(this);
        saveAllMenuItem = new JMenuItem("Save All");
        saveAllMenuItem.addActionListener(this);
        saveMenuItem.setEnabled(false);
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(this);
        JMenu optionsMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");
        helpMenuItem = new JMenuItem("Help");
        helpMenuItem.addActionListener(this);
        aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(this);

        wireframeViewCheckBox = new JCheckBox("Wireframe View");
        wireframeViewCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                sceneGraphManager.setWireFrameView(wireframeViewCheckBox.isSelected());
            }
        });

        mouseTranslateCheckBox = new JCheckBox("Translate With Mouse");
        mouseTranslateCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                sceneGraphManager.setMouseTranslate(mouseTranslateCheckBox.isSelected());
            }
        });

        testCheckBox = new JCheckBox("Test");
        testCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                testMode = testCheckBox.isSelected();
            }
        });

        resetMenuItem = new JMenuItem("Reset View");
        resetMenuItem.addActionListener(this);

        // menu hierarchy
        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAllMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuBar.add(optionsMenu);
        optionsMenu.add(mouseTranslateCheckBox);
        optionsMenu.add(wireframeViewCheckBox);
        optionsMenu.add(resetMenuItem);
        menuBar.add(helpMenu);
        helpMenu.add(helpMenuItem);
        helpMenu.add(aboutMenuItem);

        JMenu booleansMenu = new JMenu("Booleans");
        try {
            // Test if PJL is available.
            ClassLoader.getSystemClassLoader().loadClass("edu.rit.pj.PJProperties");

            booleansBG = new ButtonGroup();
            v1100RadioButton = new JRadioButtonMenuItem("Combine with 1 thread");
            v1100RadioButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    if (v1100RadioButton.isSelected()) {
                        BooleanModeller.UsedVersion = 1100;
                    }
                }
            });
            booleansBG.add(v1100RadioButton);
            booleansMenu.add(v1100RadioButton);

            v1240RadioButton = new JRadioButtonMenuItem("Combine with v.1.24.0");
            v1240RadioButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    if (v1240RadioButton.isSelected()) {
                        BooleanModeller.UsedVersion = 1240;
                    }
                }
            });
            booleansBG.add(v1240RadioButton);
            booleansMenu.add(v1240RadioButton);
            v1240RadioButton.setSelected(true);
            BooleanModeller.UsedVersion = 1240;
        } catch (ClassNotFoundException ex) {
            booleansMenu.setEnabled(false);
            Logger.getLogger(UnBBooleanFrame.class.getName()).log(Level.SEVERE,
                    "Can not load Parallel Java Library!", ex);
            System.out.println("Can not load Parallel Java Library!");
            System.out.println("classpath is " + System.getProperty("java.class.path"));
        }

        optionsMenu.add(booleansMenu);
        optionsMenu.add(testCheckBox);

        // 2 - MAIN_PANEL
        JSplitPane splitPanel = new JSplitPane();
        splitPanel.setDividerLocation(250);
        splitPanel.setDividerSize(5);
        splitPanel.setEnabled(false);
        contentPane.add(splitPanel);

        // 2.1 - CANVAS_PANEL
        GraphicsDevice screenDevice =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        GraphicsConfiguration gc = screenDevice.getBestConfiguration(template);
        Canvas3D canvas = new Canvas3D(gc);
        canvas.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent evt) {
                int keycode = evt.getKeyCode();
                if (keycode == KeyEvent.VK_DELETE) {
                    if (!csgPanel.isMoveMode()) {
                        sceneGraphManager.removeSelectedSolids();
                        csgPanel.deselectSolids();
                    }
                }
            }
        });
        canvasPanel = new JPanel();
        canvasPanel.setLayout(new BorderLayout());
        canvasPanel.setBackground(new Color(0, 0, 0));
        canvasPanel.add(canvas);
        splitPanel.setRightComponent(canvasPanel);
        canvasPanel.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                canvasPanel.getComponent(0).setSize(canvasPanel.getSize());
            }
        });

        // 2.2 - OPTIONS_PANEL
        optionsPanel = new JTabbedPane();
        splitPanel.setLeftComponent(optionsPanel);

        csgPanel = new CSGPanel(this);
        sceneGraphManager = new SceneGraphManager(canvas, csgPanel);
        PrimitivesPanel primitivesPanel = new PrimitivesPanel(this,
                sceneGraphManager);

        // NOTE: my, to be removed
        //wireframeViewCheckBox.doClick();
        //testCheckBox.doClick();
        //mouseTranslateCheckBox.doClick();

        optionsPanel.addTab("Primitives", primitivesPanel);
        optionsPanel.addTab("CSG Trees", csgPanel);

        minimumSize = new Dimension(250, height);
        optionsPanel.setMinimumSize(minimumSize);

        solidLoader = new JFileChooser();
        solidLoader.setFileFilter(new CSGsceneFilter());
        solidLoader.addChoosableFileFilter(new CSGFilter());
        solidLoader.setCurrentDirectory(new File("."));
        solidLoader.setDialogTitle("Open...");
    }

    /**
     * Method called when an action item is selected.
     *
     * @param e action event
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == loadMenuItem) {
            loadFile();
        } else if (source == saveMenuItem) {
            saveFile();
        } else if (source == saveAllMenuItem) {
            saveScene();
        } else if (source == exitMenuItem) {
            System.exit(0);
        } else if (source == resetMenuItem) {
            sceneGraphManager.resetView();
        } else if (source == helpMenuItem) {
            showHelpPage();
        } else if (source == aboutMenuItem) {
            showAboutDialog();
        }
    }

    /**
     * Loads a solid.
     */
    private void loadFile() {
        int returnVal = solidLoader.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            File selectedFile = solidLoader.getSelectedFile();
            String name = selectedFile.getPath();
            name = name.substring(name.length() - 9, name.length());
            try (ObjectInputStream in =
                            new ObjectInputStream(
                            new FileInputStream(selectedFile.getAbsolutePath()))) {
                Object first = in.readObject();
                // load entire scene
                if (name.equals(".csgscene") || first instanceof Integer) {
                    Integer count = (Integer) first;

                    for (int i = 0; i < count; i++) {
                        final SaveSolid saveSolid = (SaveSolid) in.readObject();

                        // execute boolean operations showing the progress
                        monitor = new J3DBoolProgressMonitor(
                                saveSolid.getNumberOfOperations()) {

                            public void executeBooleanOperations() {
                                System.out.println();
                                System.out.println("Loading from file...");
                                long time = -System.currentTimeMillis();

                                CSGSolid solid = saveSolid.getSolid(monitor);
                                if (solid == null) {
                                    System.out.println("An error occured.");
                                    return;
                                }
                                time += System.currentTimeMillis();
                                System.out.println("Total construction time " + time + " ms");
                                System.out.println();
                                try {
                                    //csgPanel.selectSolid(solid);
                                    solid.setRotation((Vector3d) in.readObject());
                                    solid.setTranslation((Vector3d) in.readObject());
                                    sceneGraphManager.addSolid(solid);
                                } catch (IOException | ClassNotFoundException ex) {
                                    Logger.getLogger(
                                            UnBBooleanFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        };
                        monitor.start();
                    }
                    // load single csg model
                } else {
                    final SaveSolid saveSolid = (SaveSolid) first;
                    int numOps = saveSolid.getNumberOfOperations();
                    if (testMode) {
                        numOps *= 10;
                    }
                    // execute boolean operations showing the progress
                    monitor = new J3DBoolProgressMonitor(numOps) {

                        public void executeBooleanOperations() {
                            System.out.println();
                            CSGSolid solid = null;
                            System.out.println("Loading from file...");
                            if (testMode) {
                                long time = -System.currentTimeMillis();

                                for (int i = 0; i < 10; i++) {
                                    solid = saveSolid.getSolid(monitor);
                                }
                                time += System.currentTimeMillis();
                                System.out.println("Total construction time " + time + " ms");
                                System.out.println();

                            } else {
                                long time = -System.currentTimeMillis();
                                solid = saveSolid.getSolid(monitor);
                                time += System.currentTimeMillis();
                                System.out.println("Total construction time " + time + " ms");
                                System.out.println();
                            }
                            if (solid != null) {
                                csgPanel.selectSolid(solid);
                                sceneGraphManager.addSolid(solid);
                            }
                        }
                    };
                    monitor.start();
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "file not found.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(
                    UnBBooleanFrame.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error, load aborted.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(
                    UnBBooleanFrame.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Saves a solid.
     */
    private void saveFile() {
        solidSaver = new JFileChooser();
        solidSaver.addChoosableFileFilter(new ObjFilter());
        solidSaver.removeChoosableFileFilter(solidSaver.getAcceptAllFileFilter());
        solidSaver.setCurrentDirectory(new File("."));
        solidSaver.setDialogTitle("Save...");
        solidSaver.setFileFilter(new CSGFilter());

        CSGSolid solid = csgPanel.getSelectedSolid();
        int returnVal = solidSaver.showSaveDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            File selectedFile = solidSaver.getSelectedFile();
            String name = selectedFile.getPath();
            if (solidSaver.getFileFilter() instanceof CSGFilter) {
                // dangerous name
                if (!(name.substring(name.length() - 4, name.length()).equals(".csg"))) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".csg");
                }
                SaveSolid saveSolid = SaveSolid.getSaveSolid(solid);
                try (ObjectOutputStream out = new ObjectOutputStream(
                                new FileOutputStream(selectedFile.getAbsolutePath()))) {
                    out.writeObject(saveSolid);
                }
            } else {
                if (!(name.substring(name.length() - 4, name.length()).equals(".obj"))) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".obj");
                }

                Point3d[] vertices = solid.getVertices();
                int[] indices = solid.getIndices();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                                selectedFile))) {
                    for (int i = 0; i < vertices.length; i++) {
                        writer.write("v " + vertices[i].x + " " + vertices[i].y
                                + " " + vertices[i].z + "\n");
                    }
                    for (int i = 0; i < indices.length; i = i + 3) {
                        writer.write("f " + (indices[i] + 1) + " "
                                + (indices[i + 1] + 1) + " "
                                + (indices[i + 2] + 1) + "\n");
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "File saved successfully.",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Error, save aborted.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(
                    UnBBooleanFrame.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Show help page
     */
    private void showHelpPage() {
        new HelpDialog().setVisible(true);
    }

    /**
     * show information about de application (author, home page...)
     */
    private void showAboutDialog() {
        Box aboutPanel = Box.createVerticalBox();

        JLabel labelTitulo = new JLabel("UnBBoolean " + Text.VERSION);
        labelTitulo.setFont(new Font(null, Font.BOLD, 16));
        aboutPanel.add(labelTitulo);

        aboutPanel.add(Box.createVerticalStrut(5));

        aboutPanel.add(new JLabel("Author: Danilo Balby (danbalby@yahoo.com)"));
        aboutPanel.add(new JLabel("        "));
        aboutPanel.add(new JLabel("Upgraded: Mykola Morhun (nickmnorgunn@gmail.com)"));
        aboutPanel.add(new JLabel("        "));

        Box linkPanel = Box.createHorizontalBox();
        linkPanel.setAlignmentX(Box.LEFT_ALIGNMENT);
        linkPanel.add(new JLabel("Original homepage: "));
        linkPanel.add(new LinkButton("http://unbboolean.sourceforge.net/"));
        aboutPanel.add(linkPanel);

        JOptionPane.showMessageDialog(this, aboutPanel, "About",
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Sets the availability of the save option.
     *
     * @param b availability of the save option
     */
    public void setSaveEnabled(boolean b) {
        saveMenuItem.setEnabled(b);
    }

    /**
     * Shows the csg panel.
     */
    public void showCSGPanel() {
        optionsPanel.setSelectedIndex(1);
    }

    /**
     * Gets the used scene graph manager.
     *
     * @return scene graph manager used
     */
    public SceneGraphManager getSceneGraphManager() {
        return sceneGraphManager;
    }

    /**
     * Saves all solids to a file.
     */
    private void saveScene() {
        solidSaver = new JFileChooser();
        solidSaver.removeChoosableFileFilter(solidSaver.getAcceptAllFileFilter());
        solidSaver.setCurrentDirectory(new File("."));
        solidSaver.setDialogTitle("Save...");
        solidSaver.setFileFilter(new CSGsceneFilter());

        ArrayList<CSGSolid> solids = sceneGraphManager.getAllSolids();
        int returnVal = solidSaver.showSaveDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            File selectedFile = solidSaver.getSelectedFile();
            String name = selectedFile.getPath();
            name = name.substring(name.length() - 9, name.length());

            if (!(name.equals(".csgscene"))) {
                selectedFile = new File(selectedFile.getAbsolutePath()
                        + ".csgscene");
            }
            SaveSolid saveSolid;
            try (ObjectOutputStream out = new ObjectOutputStream(
                            new FileOutputStream(selectedFile.getAbsolutePath()))) {
                Integer count = solids.size();
                out.writeObject(count);
                for (CSGSolid s : solids) {
                    saveSolid = SaveSolid.getSaveSolid(s);
                    out.writeObject(saveSolid);
                    out.writeObject(s.getRotation());
                    out.writeObject(s.getTranslation());
                }
            }

            JOptionPane.showMessageDialog(this, "File saved successfully.",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Error, save aborted.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(
                    UnBBooleanFrame.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
