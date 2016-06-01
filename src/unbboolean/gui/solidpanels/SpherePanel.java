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
 * Panel responsible to show sphere features 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SpherePanel extends SolidPanel
{
	/** sphere ray in X */
	private JSpinner rayXField;
	/** sphere ray in Y */
	private JSpinner rayYField;
	/** sphere ray in Z */
	private JSpinner rayZField;
	
	/** Constructs a default SpherePanel object */		
	public SpherePanel()
	{
		this(5,5,5);
	}
	
	/**
	 * Constructs a panel setting the initial values
	 * 
	 * @param rayX sphere ray in X
	 * @param rayY sphere ray in Y
	 * @param rayZ sphere ray in Z
	 */
	public SpherePanel(double rayX,double rayY,double rayZ)
	{
		name = "sphere";
		currentName = name+cont;
		setLayout(new GridLayout(5,2));
				
		setBorder(new TitledBorder(border,"Sphere Properties"));
		
		rayXField = new JSpinner(new SpinnerNumberModel(rayX,0,100,0.1));
		Dimension fieldDimension = new Dimension(60,rayXField.getPreferredSize().height);
		rayXField.setPreferredSize(fieldDimension);
		rayYField = new JSpinner(new SpinnerNumberModel(rayY,0,100,0.1));
		rayYField.setPreferredSize(fieldDimension);
		rayZField = new JSpinner(new SpinnerNumberModel(rayZ,0,100,0.1));
		rayZField.setPreferredSize(fieldDimension);
				
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
		panel.add(new JLabel("ray in Y:"));
		add(panel);

		panel = new JPanel(flowLayout);
		panel.add(rayYField);
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(new JLabel("ray in Z:"));
		add(panel);

		panel = new JPanel(flowLayout);
		panel.add(rayZField);
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
	 * Gets the selected ray in Y 
	 * 
	 * @return the selected ray in Y
	 */
	public double getSelectedRayY()
	{
		return ((Double)rayYField.getValue()).doubleValue();
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
	 * Sets the panel values
	 * 
	 * @param solid used to set the panel values
	 */
	public void setValues(PrimitiveSolid solid)
	{
		if(solid instanceof SphereSolid)
		{
			SphereSolid sphere = (SphereSolid)solid;
			name = sphere.getName();
			setBorder(new TitledBorder(border,name+" Properties"));
			colorButton.setBackground((sphere.getColors()[0]).get());
			rayXField.setValue(new Double(sphere.getRayX()));
			rayYField.setValue(new Double(sphere.getRayY()));
			rayZField.setValue(new Double(sphere.getRayZ()));
		}
	}
	
	/**
	 * Gets the solid based on this panel values
	 * 
	 * @return solid based on this panel values
	 */
	public PrimitiveSolid getSolid()
	{
		double rayX, rayY, rayZ;
		rayX = getSelectedRayX();
		rayY = getSelectedRayY();
		rayZ = getSelectedRayZ();
		Color3f color = new Color3f(getColor());
		SphereSolid sphere = new SphereSolid(name, rayX, rayY, rayZ, color);
		return sphere;
	}
}