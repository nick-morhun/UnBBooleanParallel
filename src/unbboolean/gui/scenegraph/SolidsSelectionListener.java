package unbboolean.gui.scenegraph;

import unbboolean.solids.CSGSolid;

/**
 * Interface whose implementor wants to know about a solid selection 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public interface SolidsSelectionListener
{
	/**
	 * A solid was selected
	 *  
	 * @param solid selected solid
	 */
	public void selectSolid(CSGSolid solid);
	
	/**
	 * Two solids were selected
	 *  
	 * @param solid1 first selected solid
	 * @param solid2 second selected solid
	 */
	public void selectSolids(CSGSolid solid1, CSGSolid solid2);
	
	/** All the solids were deselected */
	public void deselectSolids();
}