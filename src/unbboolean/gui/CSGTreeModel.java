package unbboolean.gui;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import unbboolean.solids.CSGSolid;
import unbboolean.solids.CompoundSolid;

/**
 * Tree model based on the CSGSolid structure 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class CSGTreeModel implements TreeModel
{
	/** tree root */
	private CSGSolid root;
	/** listen about the tree changes */
	private TreeModelListener listener;
	
	/**
	 * Constructs a CSGTreeModel with the given root.
	 * 
	 * @param root tree root
	 */	
	public CSGTreeModel(CSGSolid root)
	{
		this.root = root;
	}
	
	/**
	 * Gets the tree root
	 * 
	 * @return tree root (a CSGSolid object).
	 */
	public Object getRoot()
	{
		return root;
	}

	/**
	 * Gets a child of a given parent in a given index
	 * 
	 * @param parent parent whose child is to be found
	 * @param index index of the child to be found
	 * @return required child, null if parameters are invalid for this model
	 */
	public Object getChild(Object parent, int index)
	{
		if(!(parent instanceof CompoundSolid))
		{
			return null; 
		}
		else
		{
			CompoundSolid solid = (CompoundSolid)parent;
			if(index==0)
			{
				return solid.getOperator1();
			}
			else if(index==1)
			{
				return solid.getOperator2();
			}
			else
			{
				return null;
			}
		}
	}


	/**
	 * Gets the number of children of a give parent (2 for CompoundSolid and 0 for 
	 * PrimitiveSolid)
	 * 
	 * @param parent parent whose number of children is to be found 
	 * @return number of children of the given parent
	 */
	public int getChildCount(Object parent)
	{
		if(!(parent instanceof CompoundSolid))
		{
			return 0 ;
		}
		else
		{
			return 2;
		}
	}

	/**
	 * Returns if a given object is a tree leaf (if it is a CompoundSolid object, is
	 * a node, otherwise is a leaf)
	 * 
	 * @param node test node
	 * @return true if node is a leaf, false otherwise
	 */
	public boolean isLeaf(Object node)
	{
		if(node instanceof CompoundSolid)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Gets the index of a child of a given parent
	 * 
	 * @param parent parent of the child whose index is required
	 * @param child child whose index is required
	 * @return child index, -1 if the parameters are invalid for this model
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		if(!(parent instanceof CompoundSolid))
		{
			return -1;
		}
		else
		{
			CompoundSolid solid = (CompoundSolid)parent;
			if(solid.getOperator1()==child)
			{
				return 0;
			}
			else if(solid.getOperator2()==child)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}

	/**
	 * Sets a listener for changes into the model
	 * 
	 * @param l listener to be set
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		listener = l;
	}

	/**
	 * Removes a listener for changes into the model
	 * 
	 * @param l listener to be removed
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		if(listener==l)
		{
			listener = null;
		}
	}
	
	/** Unimplemented method. */
	public void valueForPathChanged(TreePath path, Object newValue){}
} 