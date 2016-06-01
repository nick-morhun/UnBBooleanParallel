package unbboolean.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.vecmath.Point3d;

import unbboolean.gui.save.CSGFilter;
import unbboolean.gui.save.ObjFilter;
import unbboolean.gui.save.SaveSolid;
import unbboolean.gui.scenegraph.SceneGraphManager;
import unbboolean.solids.CSGSolid;

/**
 * UnBBoolean's main frame. 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class UnBBooleanFrame extends JFrame implements ActionListener
{
	/** item to save a solid */
	private JMenuItem saveMenuItem;
	/** item to load a solid */
	private JMenuItem loadMenuItem;
	/** item to finish the program */
	private JMenuItem exitMenuItem;
	/** item to show a help page */
	private JMenuItem helpMenuItem;
	/** item to show information about the application */
	private JMenuItem aboutMenuItem;
	/** check box to change to wireframe view */
	private JCheckBox wireframeViewCheckBox;
	/** panel where the canvas is */
	private JPanel canvasPanel;
	/** manager of the scene graph where the solids are */
	private SceneGraphManager sceneGraphManager;
	/** panel where the options panels are set */
	private JTabbedPane optionsPanel;
	/** panel to edit solids structures */
	private CSGPanel csgPanel;
	/** dialog window to load solids*/
	private JFileChooser solidLoader;
	/** dialog window to save solids*/
	private JFileChooser solidSaver;
	/** progress monitor of the boolean operations */
	private J3DBoolProgressMonitor monitor;
	
	/** Constructs a UnBBoolean object with the initial configuration. */					
	public UnBBooleanFrame()
	{
		setTitle("UnBBoolean v1.1");
		Container contentPane = this.getContentPane();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//center screen
		setSize(800,600);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(screenSize.width > 800 && screenSize.height > 600)
		{
			setLocation((screenSize.width - 800) / 2, (screenSize.height - 600) / 2);
		}

		//lines to work out the conflict between swing and canvas3d
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
						
		//1 - MENU_BAR
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(this);
		saveMenuItem.setEnabled(false);
		loadMenuItem = new JMenuItem("Load");
		loadMenuItem.addActionListener(this);
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(this);
		JMenu optionsMenu = new JMenu("Options");
		JMenu helpMenu = new JMenu("Help");
		helpMenuItem = new JMenuItem("Help");
		helpMenuItem.addActionListener(this);
		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(this);
		
		wireframeViewCheckBox = new JCheckBox("Wireframe View");
		wireframeViewCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				sceneGraphManager.setWireFrameView(wireframeViewCheckBox.isSelected());				
			}
		});

		//menu hierarchy
		setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		fileMenu.add(saveMenuItem);
		fileMenu.add(loadMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
		menuBar.add(optionsMenu);
		optionsMenu.add(wireframeViewCheckBox);
		menuBar.add(helpMenu);
		helpMenu.add(helpMenuItem);
		helpMenu.add(aboutMenuItem);
		
		//2 - MAIN_PANEL
		JSplitPane splitPanel = new JSplitPane();
		splitPanel.setDividerLocation(200);
		splitPanel.setDividerSize(5);
		splitPanel.setEnabled(false);
		contentPane.add(splitPanel);
						
		//2.1 - CANVAS_PANEL
		GraphicsDevice screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); 
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D(); 
		GraphicsConfiguration gc = screenDevice.getBestConfiguration(template);
		Canvas3D canvas = new Canvas3D(gc);
		canvas.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent evt)
			{
				int keycode = evt.getKeyCode();
				if(keycode==KeyEvent.VK_DELETE)
				{
					if(!csgPanel.isMoveMode())
					{
						sceneGraphManager.removeSelectedSolids();
						csgPanel.deselectSolids();
					}
				}
			}
		});
		canvasPanel = new JPanel();
		canvasPanel.setLayout(new BorderLayout());
		canvasPanel.setBackground(new Color(0,0,0));
		canvasPanel.add(canvas);
		splitPanel.setRightComponent(canvasPanel);
		canvasPanel.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				canvasPanel.getComponent(0).setSize(canvasPanel.getSize());
			}
		});
								
		//2.2 - OPTIONS_PANEL
		optionsPanel = new JTabbedPane();
		splitPanel.setLeftComponent(optionsPanel);
		
		csgPanel = new CSGPanel(this);
		sceneGraphManager = new SceneGraphManager(canvas, csgPanel);
		PrimitivesPanel primitivesPanel = new PrimitivesPanel(this, sceneGraphManager);
			
		optionsPanel.addTab("Primitives", primitivesPanel);
		optionsPanel.addTab("CSG Trees", csgPanel);
		
		solidLoader = new JFileChooser();
		solidLoader.setFileFilter(new CSGFilter());
		solidLoader.setCurrentDirectory(new File("."));
		solidLoader.setDialogTitle("Open...");
		
		solidSaver = new JFileChooser();
		solidSaver.addChoosableFileFilter(new ObjFilter());
		solidSaver.addChoosableFileFilter(new CSGFilter());
		solidSaver.removeChoosableFileFilter(solidSaver.getAcceptAllFileFilter());
		solidSaver.setCurrentDirectory(new File("."));
		solidSaver.setDialogTitle("Save...");
	}
	
	/** 
	 * Method called when an action item is selected.
	 * 
	 * @param e action event
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source==loadMenuItem)
		{
			loadFile();
		}
		else if(source==saveMenuItem)
		{
			saveFile();
		}
		else if (source==exitMenuItem)
		{
			System.exit(0);
		}
		else if(source==helpMenuItem)
		{
			showHelpPage();
		}
		else if(source==aboutMenuItem)
		{
			showAboutDialog();			
		}
	}
	
	/** Loads a solid. */
	private void loadFile() 
	{
		int returnVal = solidLoader.showOpenDialog(this); 
		if (returnVal==JFileChooser.APPROVE_OPTION)
		{
			try
			{
				File selectedFile = solidLoader.getSelectedFile();
				
				final SaveSolid saveSolid;
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedFile.getAbsolutePath()));
				saveSolid = (SaveSolid)in.readObject();
				in.close();

				//execute boolean operations showing the progress
				monitor = new J3DBoolProgressMonitor(saveSolid.getNumberOfOperations())
				{
					public void executeBooleanOperations() 
					{
						CSGSolid solid = saveSolid.getSolid(monitor);
						if(solid != null)
						{
							csgPanel.selectSolid(solid);
							sceneGraphManager.addSolid(solid);
						}
					}
				};
				monitor.start();
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog (this,"file not found.","Error",JOptionPane.ERROR_MESSAGE);
			} 
			catch (Exception e)
			{
				JOptionPane.showMessageDialog (this,"Error, load aborted.","Error",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	/** Saves a solid. */
	private void saveFile()
	{
		CSGSolid solid = csgPanel.getSelectedSolid();
		int returnVal = solidSaver.showSaveDialog(this); 
		if (returnVal==JFileChooser.APPROVE_OPTION)
		{
			try
			{
				File selectedFile = solidSaver.getSelectedFile();
				String name = selectedFile.getName();
				if(solidSaver.getFileFilter() instanceof CSGFilter)
				{
					if(!(name.substring(name.length()-4,name.length()).equals(".csg")))
					{
						selectedFile = new File(selectedFile.getAbsolutePath()+".csg");
					}
					SaveSolid saveSolid = SaveSolid.getSaveSolid(solid);
			
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(selectedFile.getAbsolutePath()));
					out.writeObject(saveSolid);
					out.close();
				}
				else
				{
					if(!(name.substring(name.length()-4,name.length()).equals(".obj")))
					{
						selectedFile = new File(selectedFile.getAbsolutePath()+".obj");
					}
					
					Point3d[] vertices = solid.getVertices();
					int[] indices = solid.getIndices();
					
					BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
					
					for(int i=0;i<vertices.length;i++)
					{
						writer.write("v "+vertices[i].x+" "+vertices[i].y+" "+vertices[i].z+"\n");
					}
					for(int i=0;i<indices.length;i=i+3)
					{
						writer.write("f "+(indices[i]+1)+" "+(indices[i+1]+1)+" "+(indices[i+2]+1)+"\n");
					}
					
					writer.close();
				}
				
				JOptionPane.showMessageDialog (this,"File saved successfully.","Message",JOptionPane.INFORMATION_MESSAGE);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog (this,"Error, save aborted.","Error",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
 		}
	}
	
	/** Show help page*/
	private void showHelpPage()
	{
		new HelpDialog().setVisible(true);
		
//		try
//		{
//			Desktop.getDesktop().browse(new URI("help.html"));
//		}
//		catch(Exception e)
//		{
//			JOptionPane.showMessageDialog(this, "Error. Please try to open the file " + System.getProperty("user.dir") + File.separator + "help.html by yourself.", "Error", JOptionPane.ERROR_MESSAGE);
//		}
	}
	
	/** show information about de application (author, home page...) */	
	private void showAboutDialog()
	{
		Box aboutPanel = Box.createVerticalBox();
				
		JLabel labelTitulo = new JLabel("UnBBoolean v1.1");
		labelTitulo.setFont(new Font(null, Font.BOLD, 16));
		aboutPanel.add(labelTitulo);
		
		aboutPanel.add(Box.createVerticalStrut(5));
		
		aboutPanel.add(new JLabel("Author: Danilo Balby (danbalby@yahoo.com)"));
		
		Box linkPanel = Box.createHorizontalBox();
		linkPanel.setAlignmentX(Box.LEFT_ALIGNMENT);
		linkPanel.add(new JLabel("Homepage: "));
		linkPanel.add(new LinkButton("http://unbboolean.sourceforge.net/"));
		aboutPanel.add(linkPanel);
		
		JOptionPane.showMessageDialog(this, aboutPanel, "About", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Sets the availability of the save option.
	 * 
	 * @param b availability of the save option 
	 */
	public void setSaveEnabled(boolean b)
	{
		saveMenuItem.setEnabled(b);
	}
	
	/** Shows the csg panel. */
	public void showCSGPanel()
	{
		optionsPanel.setSelectedIndex(1);
	}
	
	/**
	 * Gets the used scene graph manager.
	 * 
	 * @return scene graph manager used
	 */
	public SceneGraphManager getSceneGraphManager()
	{
		return sceneGraphManager;
	}
}