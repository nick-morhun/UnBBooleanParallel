package unbboolean.gui.solidpanels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.vecmath.*;

import unbboolean.solids.*;

/**
 * Panel responsible to show sphere features 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class CylinderPanel extends SolidPanel
{
	/** cylinder ray in X */
	private JSpinner rayFieldX;
	/** cylinder ray in Z */
	private JSpinner rayFieldZ;
	/** cylinder height */
	private JSpinner heightField;
	
	/** Constructs a default CylinderPanel object */
	public CylinderPanel()
	{
		this(5,5,10);
	}
	
	/**
	 * Constructs a panel setting the initial values
	 * 
	 * @param rayX cylinder ray in X
	 * @param rayZ cylinder ray in Z
	 * @param height cylinder height
	 */
	public CylinderPanel(double rayX, double rayZ, double height)
	{
		name = "cylinder";
		currentName = name+cont;
		setLayout(new GridLayout(5,2));
						
		setBorder(new TitledBorder(border,"Cylinder Properties"));
		
		rayFieldX = new JSpinner(new SpinnerNumberModel(rayX,0,100,0.1));
		Dimension fieldDimension = new Dimension(60,rayFieldX.getPreferredSize().height);
		rayFieldX.setPreferredSize(fieldDimension);
		rayFieldZ = new JSpinner(new SpinnerNumberModel(rayZ,0,100,0.1));
		rayFieldZ.setPreferredSize(fieldDimension);

		heightField = new JSpinner(new SpinnerNumberModel(height,0,100,0.1));
		heightField.setPreferredSize(fieldDimension);
		
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		JPanel panel = new JPanel(flowLayout);
		panel.add(new JLabel("color:"));
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(colorButton);
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(new JLabel("ray in X:"));
		add(panel);

		panel = new JPanel(flowLayout);
		panel.add(rayFieldX);
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(new JLabel("ray in Z:"));
		add(panel);

		panel = new JPanel(flowLayout);
		panel.add(rayFieldZ);
		add(panel);
				
		panel = new JPanel(flowLayout);
		panel.add(new JLabel("height:"));
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(heightField);
		add(panel);
	}
	
	/**
	 * Gets the selected ray in X 
	 * 
	 * @return the selected ray in X
	 */
	public double getSelectedRayX()
	{
		return ((Double)rayFieldX.getValue()).doubleValue();		
	}
	
	/**
	 * Gets the selected ray in Z 
	 * 
	 * @return the selected ray in Z
	 */
	public double getSelectedRayZ()
	{
		return ((Double)rayFieldZ.getValue()).doubleValue();		
	}
	
	/**
	 * Gets the selected height 
	 * 
	 * @return the selected height
	 */
	public double getSelectedHeight()
	{
		return ((Double)heightField.getValue()).doubleValue();
	}
	
	/**
	 * Sets the panel values
	 * 
	 * @param solid used to set the panel values
	 */
	public void setValues(PrimitiveSolid solid)
	{
		if(solid instanceof CylinderSolid)
		{
			CylinderSolid cylinder = (CylinderSolid)solid;
			name = cylinder.getName();
			setBorder(new TitledBorder(border,name+" Properties"));
			colorButton.setBackground((cylinder.getColors()[0]).get());
			rayFieldX.setValue(new Double(cylinder.getRayX()));
			rayFieldZ.setValue(new Double(cylinder.getRayZ()));
			heightField.setValue(new Double(cylinder.getHeight()));
		}
	}
	
	/**
	 * Gets the solid based on this panel values
	 * 
	 * @return solid based on this panel values
	 */
	public PrimitiveSolid getSolid()
	{
		double height, rayX, rayZ;
		height = getSelectedHeight();
		rayX = getSelectedRayX();
		rayZ = getSelectedRayZ();
		Color3f color = new Color3f(getColor());
		CylinderSolid cylinder = new CylinderSolid(name, height, rayX, rayZ, color);
		return cylinder;
	}
}