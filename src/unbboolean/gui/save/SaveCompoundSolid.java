package unbboolean.gui.save;

import unbboolean.gui.J3DBoolProgressListener;
import unbboolean.gui.solidpanels.InvalidBooleanOperationException;
import unbboolean.solids.CSGSolid;
import unbboolean.solids.CompoundSolid;

/**
 * Class representing a compound solid to be saved
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SaveCompoundSolid extends SaveSolid
{
	/** operation applied */
	private int operation;
	/** first solid operator */
	private SaveSolid operator1;
	/** second solid operator */
	private SaveSolid operator2;
	
	private static final long serialVersionUID = 152008386452776152L;
	
	/**
	 * Constructs a SaveCompoundSolid object based on a CompoundSolid object
	 * 
	 * @param solid compound solid to be saved
	 */
	public SaveCompoundSolid(CompoundSolid solid)
	{  
		super(solid); 
		operation = solid.getOperation();
		operator1 = getSaveSolid(solid.getOperator1());
		operator2 = getSaveSolid(solid.getOperator2());
	}
	
	/**
	 * Gets the solid corresponding to this save solid
	 * 
	 * @param listener must be notified when an operation is executed 
	 * @return the solid corresponding to this save solid
	 */
	public CSGSolid getSolid(J3DBoolProgressListener listener)
	{
		try
		{
			CSGSolid solid1 = operator1.getSolid(listener);
			
			if(solid1 == null)
			{
				//return null when operation is cancelled
				return null;
			}
			
			CSGSolid solid2 = operator2.getSolid(listener);
			
			if(solid2 == null)
			{
				//return null when operation is cancelled
				return null;
			}

			CompoundSolid compound = new CompoundSolid(name, operation, solid1, solid2);
				
			//notifies the progress and gets if the operation was cancelled
			boolean cancelRequested = listener.notifyProgress();
			if(!cancelRequested)
			{
				return compound;
			}
			else
			{	
				//return null when operation is cancelled				
				return null;
			}
		}
		catch(InvalidBooleanOperationException e)
		{
			return null;
		}
	}
	
	/**
	 * Gets the number of operations used to create the solid 
	 * (the number of nodes on CSG tree)
	 * 
	 * @return the number of operations used to create the solid
	 */
	public int getNumberOfOperations()
	{
		return operator1.getNumberOfOperations() + operator2.getNumberOfOperations() + 1;
	}
}