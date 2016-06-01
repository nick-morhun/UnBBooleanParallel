package unbboolean.gui.solidpanels;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import unbboolean.solids.CSGSolid;

/**
 * Panel responsible to show new compound solid features 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class NewCompoundSolidPanel extends CompoundSolidPanel
{
	/** field to insert the new solid name */
	private JTextField solidNameField;
	/** default solid name */
	private String name;
	/** name of the solids created based on this panel values */
	private int cont = 1;
	
	/** Constructs a default NewCompoundSolidPanel object */
	public NewCompoundSolidPanel()
	{
		this("A","B");
	}
	
	/**
	 * Constructs a customized panel
	 * 
 	 * @param solid1 name of the first solid into the operations 
	 * @param solid2 name of the second solid into the operations
	 */		
	public NewCompoundSolidPanel(String solid1, String solid2)
	{
		super("Apply boolean operation", solid1, solid2);
		name = "compound";
				
		solidNameField = new JTextField(name+cont);
		
		JPanel solidNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		solidNamePanel.add(new JLabel("name: "));
		solidNamePanel.add(solidNameField);
		add(solidNamePanel, "North");		
	}
	
	/**
	 * Gets the name of the last solid created
	 * 
	 * @return name of the last solid created
	 */
	public String getCurrentName()
	{
		cont++;
		return solidNameField.getText();
	}
	
	/**
	 * Sets the panel based on two solids
	 * 
	 * @param solid1 name of the first solid into the operations 
	 * @param solid2 name of the second solid into the operations
	 */
	public void setValues(CSGSolid solid1, CSGSolid solid2)
	{
		solidNameField.setText(name+cont);
		
		String solid1Name = solid1.getName();
		String solid2Name = solid2.getName();
		
		unionButton.setText(solid1Name+" U "+solid2Name);
		intersectionButton.setText(solid1Name+" \u2229 "+solid2Name);
		differenceButton1.setText(solid1Name+" - "+solid2Name);
		differenceButton2.setText(solid2Name+" - "+solid1Name);
	}
}