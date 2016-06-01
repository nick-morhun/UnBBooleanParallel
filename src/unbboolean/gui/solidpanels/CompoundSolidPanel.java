package unbboolean.gui.solidpanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import unbboolean.solids.CompoundSolid;

/**
 * Panel responsible to show compound solid features 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class CompoundSolidPanel extends JPanel
{
	/** union choice - see getSelected() method */
	public static final int A_UNION_B = CompoundSolid.UNION;
	/** intersection choice - see getSelected() method */
	public static final int A_INTERSECTION_B = CompoundSolid.INTERSECTION;
	/** difference choice - see getSelected() method */
	public static final int A_DIFFERENCE_B = CompoundSolid.DIFFERENCE;
	/** inverse difference choice - see getSelected() method */
	public static final int B_DIFFERENCE_A = 100;
	
	/** button selected when the union choice is required */
	protected JRadioButton unionButton;
	/** button selected when the intersection choice is required */
	protected JRadioButton intersectionButton;
	/** button selected when the difference choice is required */
	protected JRadioButton differenceButton1;
	/** button selected when the inverse difference choice is required */
	protected JRadioButton differenceButton2;
	/** default border */
	private Border border;
	
	/** Constructs a default CompoundSolidPanel object */
	public CompoundSolidPanel()
	{
		this("Solid properties", "A", "B");
	}
	
	/**
	 * Constructs a customized panel
	 * 
	 * @param message message to be shown on the top
	 * @param solid1 name of the first solid into the operations 
	 * @param solid2 name of the second solid into the operations
	 */		
	public CompoundSolidPanel(String message, String solid1, String solid2)
	{
		setLayout(new BorderLayout());
		
		//border
		border = BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151));
		setBorder(new TitledBorder(border,message));
		
		buildOperationsGroup(solid1, solid2);		
	}
	
	/**
	 * Gets the selected option
	 * 
	 * @return selected option: A_UNION_B, A_INTERSECTION_B, A_DIFFERENCE_B or 
	 * B_DIFFERENCE_A
	 */
	public int getSelected()
	{
		if(unionButton.isSelected())
		{
			return A_UNION_B;
		}
		else if(intersectionButton.isSelected())
		{
			return A_INTERSECTION_B;
		}
		else if(differenceButton1.isSelected())
		{
			return A_DIFFERENCE_B;
		}
		else
		{
			return  B_DIFFERENCE_A;
		}
	}
	
	/**
	 * Sets the panel based on a compound solid
	 * 
	 * @param solid solid used to set the panel
	 */
	public void setValues(CompoundSolid solid)
	{
		setBorder(new TitledBorder(border,solid.getName()+" properties"));
		
		String solid1Name = solid.getOperator1().getName();
		String solid2Name = solid.getOperator2().getName();
		
		unionButton.setText(solid1Name+" U "+solid2Name);
		intersectionButton.setText(solid1Name+" \u2229 "+solid2Name);
		differenceButton1.setText(solid1Name+" - "+solid2Name);
		differenceButton2.setText(solid2Name+" - "+solid1Name);
		
		int selection = solid.getOperation();
		if(selection==1)
		{
			unionButton.setSelected(true);
		}
		else if(selection==2)
		{
			intersectionButton.setSelected(true);
		}
		else
		{
			differenceButton1.setSelected(true);
		}
	}
		
	/**
	 * Builds the button group where the operations are grouped to be selected
	 * 
	 * @param solid1 name of the first solid into the operations 
	 * @param solid2 name of the second solid into the operations
	 */
	private void buildOperationsGroup(String solid1, String solid2)
	{
		JPanel operationsPanel = new JPanel(new GridLayout(4,1));
		ButtonGroup operationsGroup = new ButtonGroup();
		
		unionButton = new JRadioButton(solid1+" U "+solid2);
		intersectionButton = new JRadioButton(solid1+" \u2229 "+solid2);
		differenceButton1 = new JRadioButton(solid1+" - "+solid2);
		differenceButton2 = new JRadioButton(solid2+" - "+solid1);
		
		operationsGroup.add(unionButton);
		operationsGroup.add(intersectionButton);
		operationsGroup.add(differenceButton1);
		operationsGroup.add(differenceButton2);
		
		unionButton.setSelected(true);
					
		operationsPanel.add(unionButton);
		operationsPanel.add(intersectionButton);
		operationsPanel.add(differenceButton1);
		operationsPanel.add(differenceButton2);
		
		add(operationsPanel,"Center");
	}
}