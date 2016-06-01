package unbboolean.solids;

import java.util.ArrayList;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import unbboolean.gui.J3DBoolProgressListener;
import unbboolean.gui.J3DBoolProgressMonitor;
import unbboolean.gui.solidpanels.InvalidBooleanOperationException;
import unbboolean.j3dbool.BooleanModeller;
import unbboolean.j3dbool.Solid;

/**
 * Class representing a compound solid 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class CompoundSolid extends CSGSolid 
{
	/** union operation */
	public static final int UNION = 1;
	/** intersection operation */
	public static final int INTERSECTION = 2;
	/** difference operation */
	public static final int DIFFERENCE = 3;
	
	/** operation applied onto the operators */
	private int operation;
	/** first operator */
	private CSGSolid operator1;
	/** second operator */
	private CSGSolid operator2; 
	
	/**
	 * Constructs a customized CompoundSolid object
	 * 
	 * @param name solid name
	 * @param operation operation applied onto the operators - UNION, INTERSECTION or DIFFERENCE
	 * @param operator1 first operator
	 * @param operator2 second operator
	 * @throws InvalidBooleanOperationException if a boolean operation generates an empty solid
	 */		
	public CompoundSolid(String name, int operation, CSGSolid operator1, CSGSolid operator2) throws InvalidBooleanOperationException
	{
		super();
		this.name = name;
		this.operation = operation;
		this.operator1 = operator1;
		this.operator2 = operator2;
		
		try
		{
			applyBooleanOperation();
			operator1.setParentSolid(this);
			operator2.setParentSolid(this);
		}
		catch(InvalidBooleanOperationException e)
		{
			throw e;
		}
	}
	
	/**
	 * Constructor used to copy a compound solid. It doesn't apply boolean operations on
	 * the operators. Instead, it is constructed with the coordinates of the copied 
	 * solid. It's much faster. 
	 * 
	 * @param name solid name
	 * @param operation operation applied onto the operators - UNION, INTERSECTION or DIFFERENCE
	 * @param operator1 first operator
	 * @param operator2 second operator
	 * @param vertices array of points defining the solid vertices
	 * @param indices array of indices for a array of vertices
	 * @param colors array of colors defining the vertices colors 
	 */		
	private CompoundSolid(String name, int operation, CSGSolid operator1, CSGSolid operator2, Point3d[] vertices, int[] indices, Color3f[] colors)
	{
		this.name = name;
		this.operation = operation;
		this.operator1 = operator1;
		this.operator2 = operator2;
		setData(vertices, indices, colors);			
		operator1.setParentSolid(this);
		operator2.setParentSolid(this);
	}
	
   	/**
	 * String representation of a compound solid (to be used on the CSG Tree)
	 * 
	 * @return string representation
	 */
	public String toString()
	{
		if(operation==UNION)
		{
			return "U";
		}
		else if(operation==INTERSECTION)
		{
			return "\u2229";
		}
		else
		{
			return "-";			
		}
	}
	
	/**
	 * Gets the operation
	 * 
	 * @return operation
	 */ 
	public int getOperation()
	{
		return operation;
	}

	/**
	 * Gets the first operator
	 * 
	 * @return first operator
	 */ 
	public CSGSolid getOperator1()
	{
		return operator1;
	}

	/**
	 * Gets the second operator
	 * 
	 * @return second operator
	 */ 
	public CSGSolid getOperator2()
	{
		return operator2;
	}
	
	/**
	 * Sets the operation
	 * 
	 * @param operation operation
	 * @param listener must be notified when an operation is executed
	 * @return true if the user has cancelled the process, false otherwise
	 * @throws InvalidBooleanOperationException if a boolean operation generates an empty solid
	 */ 
	public boolean setOperation(int operation, J3DBoolProgressListener listener) throws InvalidBooleanOperationException
	{
		this.operation = operation;
		return updateItselfAndParents(listener);
	}
	
	/** 
	 * Sets the operation to inverse difference (invert the operators and apply difference)
	 *  
	 * @param listener must be notified when an operation is executed 
	 * @return true if the user has cancelled the process, false otherwise
	 * @throws InvalidBooleanOperationException if a boolean operation generates an empty solid
	 * */
	public boolean setOperationToInverseDifference(J3DBoolProgressListener listener) throws InvalidBooleanOperationException
	{
		this.operation = DIFFERENCE;
		CSGSolid temp = operator1;
		operator1 = operator2;
		operator2 = temp;
		return updateItselfAndParents(listener);		
	}
	
	/**
	 * Sets the first operator
	 * 
	 * @param solid first operator
	 * @param listener must be notified when an operation is executed
	 * @return true if the user has cancelled the process, false otherwise
	 * @throws InvalidBooleanOperationException if a boolean operation generates an empty solid
	 */ 
	public boolean setOperator1(CSGSolid solid, J3DBoolProgressListener listener) throws InvalidBooleanOperationException
	{
		operator1 = solid;
		solid.setParentSolid(this);
		return updateItselfAndParents(listener);
	}
	
	/**
	 * Sets the second operator
	 * 
	 * @param solid second operator
	 * @param listener must be notified when an operation is executed
	 * @return true if the user has cancelled the process, false otherwise
	 * @throws InvalidBooleanOperationException if a boolean operation generates an empty solid
	 */ 
	public boolean setOperator2(CSGSolid solid, J3DBoolProgressListener listener) throws InvalidBooleanOperationException
	{
		operator2 = solid;
		solid.setParentSolid(this);
		return updateItselfAndParents(listener);
	}
	
	/** 
	 * Update itself and parents (called when coordinates were changed)
	 * 
	 * @param listener must be notified when an operation is executed
	 * @return true if the user has cancelled the process, false otherwise
	 * @throws InvalidBooleanOperationException if a boolean operation generates an empty solid 
	 * */
	public boolean updateItselfAndParents(J3DBoolProgressListener listener) throws InvalidBooleanOperationException 
	{
		applyBooleanOperation();
		boolean cancelRequested = listener.notifyProgress();
		if(cancelRequested)
		{
			return true;
		}
		else
		{
			return updateParents(listener);
		}
	}
	
	/** Updates all its descendants (called after some transforms were performed) */	
	public void updateChildren()
	{
		if(!(transformMatrix.equals(new Matrix4d()))) 
		{
			CSGSolid solid;
			CompoundSolid compoundSolid;
			ArrayList descendants = new ArrayList();
			descendants.add(operator1);
			descendants.add(operator2);
					
			while(!descendants.isEmpty())
			{
				solid = (CSGSolid)descendants.remove(0);
				solid.updateLocation(transformMatrix);
				if(solid instanceof CompoundSolid)
				{
					compoundSolid = (CompoundSolid)solid;
					descendants.add(compoundSolid.operator1);						
					descendants.add(compoundSolid.operator2);
				}
			}
			
			transformMatrix = startTransformMatrix();
		}
	}
	
	/** 
	 * Apply boolean operation taking as account the operation and operators set before 
	 * 
	 * @throws InvalidBooleanOperationException if a boolean operation generates an empty solid
	 * */
	private void applyBooleanOperation() throws InvalidBooleanOperationException
	{
		BooleanModeller modeller = new BooleanModeller(operator1, operator2);
		
		Solid solid;
		if(operation==CompoundSolid.UNION)
		{
			solid = modeller.getUnion();
		}
		else if(operation==CompoundSolid.INTERSECTION)
		{
			solid = modeller.getIntersection();
		}
		else
		{
			solid = modeller.getDifference();
		}
		
		if(solid.isEmpty())
		{
			throw new InvalidBooleanOperationException();
		}
		else
		{
			setData(solid.getVertices(),solid.getIndices(), solid.getColors());
		}		
	}

	/**
	 * Copies the solid
	 * 
	 * @return solid copy
	 */
	public CSGSolid copy()
	{
		CompoundSolid clone = new CompoundSolid(name, operation, operator1.copy(), operator2.copy(), vertices, indices, colors);
		return clone;
	}
}