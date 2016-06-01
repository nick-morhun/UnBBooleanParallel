package unbboolean.gui.solidpanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

import unbboolean.solids.PrimitiveSolid;

/**
 * Panel responsible to show solid features 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public abstract class SolidPanel extends JPanel 
{
	/** number of solids created based on this panel */
	protected int cont = 1;
	/** default name of the solids to be created */
	protected String name;
	/** name of the last solid created */
	protected String currentName;
	/** button pressed when the user wants to change the solid color */
	protected JButton colorButton;
	/** default border */
	protected Border border;
	/** dialog window used to select a color */
	protected ColorChooserDialog colorDialog;
	
	/** Constructs a default SolidPanel object */
	public SolidPanel()
	{
		colorDialog = new ColorChooserDialog();
		
		//color button
		colorButton = new JButton("   ");
		int buttonHeight = colorButton.getPreferredSize().height;
		colorButton.setPreferredSize(new Dimension(buttonHeight, buttonHeight));
		colorButton.setBackground(colorDialog.getSelectedColor());
		colorButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				colorDialog.show();
				colorButton.setBackground(colorDialog.getSelectedColor());
			}
		});
		
		border = BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151));
	}
	
	/**
	 * Sets the name of the last solid created
	 * 
	 * @param name name of the last solid created
	 */
	public void setCurrentName(String name)
	{
		currentName = name;
	}
	
	/**
	 * Gets the next default name
	 * 
	 * @return the next default name
	 */
	public String getNextName()
	{
		cont++;
		currentName = name+cont;
		return currentName;
	} 
	
	/**
	 * Gets the name of the last solid created
	 * 
	 * @return name of the last solid created
	 */
	public String getCurrentName()
	{
		return currentName;
	}
	
	/**
	 * Gets the last selected color
	 * 
	 * @return last selected color
	 */
	public Color getColor()
	{
		return colorButton.getBackground();
	}
	
	/**
	 * Sets the panel values
	 * 
	 * @param solid used to set the panel values
	 */
	public abstract void setValues(PrimitiveSolid solid);
	
	/**
	 * Gets the solid based on this panel values
	 * 
	 * @return solid based on this panel values
	 */
	public abstract PrimitiveSolid getSolid();
}