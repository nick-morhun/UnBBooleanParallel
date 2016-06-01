package unbboolean.gui.save;

import unbboolean.solids.BoxSolid;
import unbboolean.solids.CSGSolid;

/**
 * Class representing a box solid to be saved
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SaveBoxSolid extends SavePrimitiveSolid
{
	/** box length */
	private double length;
	/** box height */
	private double height;
	/** box width */
	private double width;
	
	/**
	 * Constructs a SaveBoxSolid object based on a BoxSolid object
	 * 
	 * @param solid box solid to be saved
	 */
	public SaveBoxSolid(BoxSolid solid)
	{
		super(solid);
		length = solid.getLength();
		height = solid.getHeight();
		width = solid.getWidth();
	}
	
	/**
	 * Gets the solid corresponding to this save solid
	 * 
	 * @return the solid corresponding to this save solid
	 */
	public CSGSolid getSolid()
	{
		BoxSolid box = new BoxSolid(name, length, height, width, color);
		box.updateLocation(transformMatrix);
		return box;
	}
}