package unbboolean.gui.solidpanels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Color3f;

import unbboolean.solids.*;

/**
 * Panel responsible to show cone features 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class ConePanel extends SolidPanel
{
	/** cone ray in X */
	private JSpinner rayXField;
	/** cone ray in Z */
	private JSpinner rayZField;
	/** cone height */
	private JSpinner heightField;
	
	/** Constructs a default ConePanel object */
	public ConePanel()
	{
		this(5,5,10);
	}
	
	/**
	 * Constructs a panel setting the initial values
	 * 
	 * @param rayX cone ray in X
	 * @param rayZ cone ray in Z
	 * @param height cone height
	 */
	public ConePanel(double rayX, double rayZ, double height)
	{
		name = "cone";
		currentName = name+cont;
		setLayout(new GridLayout(5,2));
						
		setBorder(new TitledBorder(border,"Cone Properties"));
		
		rayXField = new JSpinner(new SpinnerNumberModel(rayX,0,100,0.1));
		Dimension fieldDimension = new Dimension(60,rayXField.getPreferredSize().height);
		rayXField.setPreferredSize(fieldDimension);
		rayZField = new JSpinner(new SpinnerNumberModel(rayZ,0,100,0.1));
		rayZField.setPreferredSize(fieldDimension);
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
		panel.add(rayXField);
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(new JLabel("ray in Z:"));
		add(panel);

		panel = new JPanel(flowLayout);
		panel.add(rayZField);
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
		return ((Double)rayXField.getValue()).doubleValue();		
	}
	
	/**
	 * Gets the selected ray in Z 
	 * 
	 * @return the selected ray in Z
	 */
	public double getSelectedRayZ()
	{
		return ((Double)rayZField.getValue()).doubleValue();		
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
		if(solid instanceof ConeSolid)
		{
			ConeSolid cone = (ConeSolid)solid;
			name = cone.getName();
			setBorder(new TitledBorder(border,name+" Properties"));
			colorButton.setBackground((cone.getColors()[0]).get());
			rayXField.setValue(new Double(cone.getRayX()));
			rayZField.setValue(new Double(cone.getRayZ()));
			heightField.setValue(new Double(cone.getHeight()));
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
		ConeSolid cone = new ConeSolid(name, height, rayX, rayZ, color);
		return cone;
	}
}