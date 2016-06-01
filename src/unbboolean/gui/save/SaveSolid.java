package unbboolean.gui.save;

import java.io.Serializable;

import unbboolean.gui.J3DBoolProgressListener;
import unbboolean.solids.BoxSolid;
import unbboolean.solids.CSGSolid;
import unbboolean.solids.CompoundSolid;
import unbboolean.solids.ConeSolid;
import unbboolean.solids.CylinderSolid;
import unbboolean.solids.SphereSolid;

/**
 * Class representing a solid to be saved
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public abstract class SaveSolid implements Serializable
{
	/** solid name */
	protected String name;
	
	private static final long serialVersionUID = 8087186090574810434L;
	
	/** 
	 * Constructs a SaveSolid object based on a CSGSolid object
	 * 
	 * @param solid solid to be saved 
	 */	
	public SaveSolid(CSGSolid solid)
	{
		name = solid.getName();
	}
	
	/**
	 * Gets the save solid corresponding to a save solid
	 * 
	 * @return the save solid corresponding to a solid
	 */
	public static SaveSolid getSaveSolid(CSGSolid solid)
	{
		if(solid instanceof CompoundSolid)
		{
			return new SaveCompoundSolid((CompoundSolid)solid);
		}
		else if(solid instanceof BoxSolid)
		{
			return new SaveBoxSolid((BoxSolid)solid);
		}
		else if(solid instanceof SphereSolid)
		{
			return new SaveSphereSolid((SphereSolid)solid);
		}
		else if(solid instanceof CylinderSolid)
		{
			return new SaveCylinderSolid((CylinderSolid)solid);
		}
		else if(solid instanceof ConeSolid)
		{
			return new SaveConeSolid((ConeSolid)solid);
		}
		else return null;
	}
	
	/**
	 * Gets the solid corresponding to this save solid
	 * 
	 * @param listener must be notified when an operation is executed
	 * @return the solid corresponding to this save solid
	 */
	public abstract CSGSolid getSolid(J3DBoolProgressListener listener);
	
	/**
	 * Gets the number of operations used to create the solid 
	 * (the number of nodes on CSG tree)
	 * 
	 * @return the number of operations used to create the solid
	 */
	public abstract int getNumberOfOperations();
}