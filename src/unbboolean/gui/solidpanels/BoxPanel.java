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

import unbboolean.solids.BoxSolid;
import unbboolean.solids.PrimitiveSolid;

/**
 * Panel responsible to show box features 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class BoxPanel extends SolidPanel
{
	/** box length */
	private JSpinner lengthField;
	/** box width */
	private JSpinner widthField;
	/** box height */
	private JSpinner heightField;
	
	/** Constructs a default BoxPanel object */
	public BoxPanel()
	{
		this(8,8,8);
	}
	
	/**
	 * Construct a panel setting the initial values
	 * 
	 * @param length box length
	 * @param width box width
	 * @param height box height
	 */
	public BoxPanel(double length, double width, double height)
	{
		name = "box";
		currentName = name+cont;
		setLayout(new GridLayout(5,2));
						
		setBorder(new TitledBorder(border,"Box Properties"));
		
		lengthField = new JSpinner(new SpinnerNumberModel(length,0,100,0.1));
		Dimension fieldDimension = new Dimension(60,lengthField.getPreferredSize().height);
		lengthField.setPreferredSize(fieldDimension);
		widthField = new JSpinner(new SpinnerNumberModel(width,0,100,0.1));
		widthField.setPreferredSize(fieldDimension);
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
		panel.add(new JLabel("length:"));
		add(panel);

		panel = new JPanel(flowLayout);
		panel.add(lengthField);
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(new JLabel("width:"));
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(widthField);
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(new JLabel("height:"));
		add(panel);
		
		panel = new JPanel(flowLayout);
		panel.add(heightField);
		add(panel);
	}
	
	/**
	 * Gets the selected length 
	 * 
	 * @return the selected length
	 */
	public double getSelectedLength()
	{
		return ((Double)lengthField.getValue()).doubleValue();		
	}
	
	/**
	 * Gets the selected width 
	 * 
	 * @return the selected width
	 */
	public double getSelectedWidth()
	{
		return ((Double)widthField.getValue()).doubleValue();
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
		if(solid instanceof BoxSolid)
		{
			BoxSolid box = (BoxSolid)solid;
			name = box.getName();
			setBorder(new TitledBorder(border,name+" Properties"));
			colorButton.setBackground((box.getColors()[0]).get());
			lengthField.setValue(new Double(box.getLength()));
			widthField.setValue(new Double(box.getWidth()));
			heightField.setValue(new Double(box.getHeight()));
		}
	}
	
	/**
	 * Gets the solid based on this panel values
	 * 
	 * @return solid based on this panel values
	 */
	public PrimitiveSolid getSolid()
	{
		double height, width, length;
		height = getSelectedHeight();
		length = getSelectedLength();
		width = getSelectedWidth();
		Color3f color = new Color3f(getColor());
		BoxSolid box = new BoxSolid(name, length, height, width, color);
		return box;
	}
}