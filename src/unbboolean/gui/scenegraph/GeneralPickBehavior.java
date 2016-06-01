package unbboolean.gui.scenegraph;

import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;

import unbboolean.solids.CSGSolid;
import unbboolean.solids.CompoundSolid;

/**
 * Class responsible for applying transformations on solids where the user drags one of 
 * them with a mouse on a screen
 * 
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */
public class GeneralPickBehavior extends Behavior 
{
	/** last solid pressed on a screen  */
	private CSGSolid solid = null;
	/** a second solid selected on a screen */
	private CSGSolid solid2 = null;
	/** last registered position of a mouse drag on a solid */
  	private int xpos, ypos;
  	/** says if move mode is on */
  	private boolean moveMode = false;
  	/** wakeup condition */
	private WakeupOr wakeupCondition;
	/** catch mouse event */
	private MouseEvent mevent;
	/** used to get the picked solid */
  	private PickCanvas pickScene;
  	/** listener that receives information about solids selection */
  	private SolidsSelectionListener listener;
  	/** if a button were pressed on the last wakeup */
  	private boolean buttonPress = false;
  	  	
  	//------------------------------CONSTRUCTORS------------------------------------//
    
    /** 
     * Constructs a GeneralPickBehavior object
     * 
     * @param root root of the scene graph where the solids are  
     * @param canvas screen where the iteration with the solids occurs
     * @param listener listener that receives information about solids selection 
     */
  	public GeneralPickBehavior(BranchGroup root, Canvas3D canvas, SolidsSelectionListener listener)
  	{
  		pickScene = new PickCanvas(canvas,root);
		this.setSchedulingBounds(new BoundingSphere());
		this.listener = listener;
  	}
  	
  	//--------------------------BEHAVIOR_METHODS------------------------------------//
  	
	/** Initializes the behavior attributes */
  	public void initialize()
  	{
		WakeupCriterion[] conditions = new WakeupCriterion[3];
		conditions[0] = new WakeupOnAWTEvent(Event.MOUSE_DOWN);
		conditions[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
		conditions[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
		wakeupCondition = new WakeupOr(conditions);

		wakeupOn(wakeupCondition);
  	}
  	
  	/**
  	 * Applies a transformation when a solid is dragged
  	 * 
  	 * @param criteria set of stimulus received 
  	 */
  	public void processStimulus (Enumeration criteria) 
  	{
	  	WakeupCriterion wakeup = (WakeupCriterion)criteria.nextElement();
	  	AWTEvent[] evt = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
	  	int xpos = 0, ypos = 0;

	  	mevent = (MouseEvent) evt[0];
		processMouseEvent((MouseEvent)evt[0]);
		xpos = mevent.getPoint().x;
		ypos = mevent.getPoint().y;
		
		if(evt[0].getID()==MouseEvent.MOUSE_RELEASED)
		{
			if(solid instanceof CompoundSolid)
			{
				CompoundSolid compound = (CompoundSolid)solid;
				compound.updateChildren();
			}
		}
		else
		{
		  	updateScene(xpos, ypos);
		}
	  	
	  	wakeupOn (wakeupCondition);
	}
	
	/**
	 * Identifies the kind of mouse event
	 * @param evt mouse event
	 */
	private void processMouseEvent(MouseEvent evt) 
	{
		buttonPress = false;

		if (evt.getID()==MouseEvent.MOUSE_PRESSED |
		evt.getID()==MouseEvent.MOUSE_CLICKED) 
		{
			buttonPress = true;
			return;
		}
	}
	
	/**
	 * Applies a transformation according to the mouse movement and the buttons pressed
	 * 
	 * @param xpos current mouse position on the x axis
	 * @param ypos current mouse position on the y axis
	 */	
  	private void updateScene(int xpos, int ypos)
  	{
  		if(buttonPress)
		{
			//move mode: only one solid can be selected
			if(moveMode)
			{
				pickScene.setShapeLocation(xpos, ypos);
				PickResult results[] = pickScene.pickAll();
				int i=0;
				if(results!=null)
				{
					for(;i<results.length;i++)
					{
						if((CSGSolid)results[i].getObject()==solid2)
						{
							solid = solid2;
							break;					
						}
					}
					if(i==results.length)
					{
						solid = null;
					}
				}
			}
			else
			{
				pickScene.setShapeLocation(xpos, ypos);
				PickResult result = pickScene.pickClosest();
			
				//none solid were selected: deselect all
				if(result==null)
				{
					if(solid!=null) 
					{
						listener.deselectSolids();
					}
								
					solid = null;
					solid2 = null;
				}
				else 
				{
					CSGSolid pickedSolid = (CSGSolid)result.getObject();
					//a new solid were selected
					if (!pickedSolid.equals(solid) && !pickedSolid.equals(solid2))
					{
						//if control is pressed: if one solid were selected, it still is, in case o two, one is deselected
						if(mevent.isControlDown())
						{
							solid2 = solid;
							solid = pickedSolid;
					
							if(solid2==null) 
							{
								listener.selectSolid(solid);
							} 	
							else
							{ 
								listener.selectSolids(solid2, solid); 
							}
						}
						//else: the currently selected solids are deselected and the new one is selected 
						else
						{
							solid = pickedSolid;
							solid2 = null;
					
							listener.selectSolid(solid);
						}
					}
			
					//a currently selected solid were selected
					else
					{
						//if control is pressed: the solid is deselected
						if(mevent.isControlDown())
						{
							if(pickedSolid.equals(solid2))
							{
								solid2 = solid;
								solid = pickedSolid;
							}
						}
						//else: other currently selected solids are deselected
						else
						{
							if (solid2!=null) 
							{
								if(pickedSolid==solid)
								{
									solid2 = null;							
								}
								else
								{
									solid = solid2;
									solid2 = null;
								}
								listener.selectSolid(pickedSolid);
							} 
						}
					}
				}
			}
		}
		//translate solid
		else if (!mevent.isAltDown() && !mevent.isMetaDown())
		{
			if(solid!=null) solid.translate((xpos-this.xpos)/25d, (this.ypos-ypos)/25d);
		}
		//rotate solid
		else if (!mevent.isAltDown() && mevent.isMetaDown())
		{
			if(solid!=null) solid.rotate((ypos-this.ypos)/50d, (xpos-this.xpos)/50d);
		}
		//zoom solid
		else
		{
			if(solid!=null) solid.zoom((ypos-this.ypos)/25d);
		}
		
		this.xpos = xpos;
		this.ypos = ypos;
	}
 	
	//-------------------------------------OTHERS-----------------------------------//
 	
 	/**
 	 * Sets move mode on
 	 * 
 	 * @param solid move mode solid
 	 */
	public void setMoveMode(CSGSolid solid)
	{
		moveMode = true;
		this.solid = solid;
		this.solid2 = solid;
		solid.light();		
	}
	
	/**
 	 * Sets move mode off
 	 * 
 	 * @param solid solid to be selected after the move mode is off 
 	 */
	public void unsetMoveMode(CSGSolid solid)
	{
		moveMode = false;
		this.solid = solid;
		this.solid2 = null;
		solid.light();
	}
}