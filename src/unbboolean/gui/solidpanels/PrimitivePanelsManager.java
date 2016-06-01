package unbboolean.gui.solidpanels;

import unbboolean.solids.BoxSolid;
import unbboolean.solids.ConeSolid;
import unbboolean.solids.CylinderSolid;
import unbboolean.solids.PrimitiveSolid;
import unbboolean.solids.SphereSolid;

/**
 * Manage the use of primitive panels  
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class PrimitivePanelsManager
{
	/** panel for the box */
	private BoxPanel boxPanel;
	/** panel for the sphere */
	private SpherePanel spherePanel;
	/** panel for the cone */ 
	private ConePanel conePanel;
	/** panel for the cylinder */
	private CylinderPanel cylinderPanel;
	/** primitives list */
	private static final String[] primitivesList = {"box", "sphere", "cone", "cylinder"};
	
	/** Constructs a default PrimitivePanelsManager object */
	public PrimitivePanelsManager()
	{
		boxPanel = new BoxPanel();
		spherePanel = new SpherePanel();
		conePanel = new ConePanel();
		cylinderPanel = new CylinderPanel();
	}
	
	/**
	 * Gets the solid panel relative to a solid
	 * 
	 * @param solid solid whose panel is required
	 * @return solid panel required 
	 */
	public SolidPanel getSolidPanel(PrimitiveSolid solid)
	{
		if(solid instanceof BoxSolid)
		{
			boxPanel.setValues(solid);
			return boxPanel;
		}
		else if(solid instanceof SphereSolid)
		{
			spherePanel.setValues(solid);
			return spherePanel;
		}
		else if(solid instanceof ConeSolid)
		{
			conePanel.setValues(solid);
			return conePanel;
		}
		else if(solid instanceof CylinderSolid)
		{
			cylinderPanel.setValues(solid);
			return cylinderPanel;
		}
		
		else return null;
	}
	
	/**
	 * Gets the solid panel relative to a solid
	 * 
	 * @param solid string defining a solid whose panel is required
	 * @return solid panel required 
	 */
	public SolidPanel getSolidPanel(String solid)
	{
		if(solid.equals("box"))
		{
			return boxPanel;
		}
		else if(solid.equals("sphere"))
		{
			return spherePanel;	
		}
		else if(solid.equals("cone"))
		{
			return conePanel;
		}
		else if(solid.equals("cylinder"))
		{
			return cylinderPanel;
		}
		else return null;
	}
	
	/**
	 * Gets the solid panel relative to a solid
	 * 
	 * @param pos position on the list of the solid whose panel is required
	 * @return solid panel required 
	 */
	public SolidPanel getSolidPanel(int pos)
	{
		if(pos==0)
		{
			return boxPanel;
		}
		else if(pos==1)
		{
			return spherePanel;	
		}
		else if(pos==2)
		{
			return conePanel;
		}
		else if(pos==3)
		{
			return cylinderPanel;
		}
		else return null;
	}
	
	/**
	 * Gets the list of primitives
	 * 
	 * @return the list of primitives
	 */
	public String[] getPrimitivesList()
	{
		return primitivesList;
	}
}