package unbboolean.solids;

import java.io.File;
import javax.vecmath.Color3f;

/**
 * Class representing a box
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class BoxSolid extends PrimitiveSolid
{
	/** box length */
	private double length;
	/** box height */
	private double height;
	/** box width */
	private double width;
	
	/**
	 * Constructs a customized Box object
	 * 
	 * @param name box name
	 * @param length box length
	 * @param height box height
	 * @param width box width
	 * @param color box color
	 */	
	public BoxSolid(String name, double length, double height, double width, Color3f color)
	{
		super();
		
		this.name = name;
		this.length = length;
		this.height = height;
		this.width = width;
						
		setData(DefaultCoordinates.DEFAULT_BOX_VERTICES, DefaultCoordinates.DEFAULT_BOX_COORDINATES, color);
		scale(width, height, length);
	}
	
	/**
	 * Copies the solid
	 * 
	 * @return solid copy
	 */
	public CSGSolid copy()
	{
		BoxSolid box = new BoxSolid(name, length, height, width, getColor());
		box.updateLocation(getLocation());
		return box;
	}
	
	/**
	 * Gets height
	 * 
	 * @return height
	 */
	public double getHeight()
	{
		return height;
	}

	/**
	 * Gets length
	 * 
	 * @return length
	 */
	public double getLength()
	{
		return length;
	}

	/**
	 * Gets width
	 * 
	 * @return width
	 */
	public double getWidth()
	{
		return width;
	}
}