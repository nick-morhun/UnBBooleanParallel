package unbboolean.gui.scenegraph;

import java.util.Enumeration;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import com.sun.j3d.utils.universe.SimpleUniverse;

import unbboolean.solids.CSGSolid;

/**
 * Manages the scene graph where the solids on the screen are set
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SceneGraphManager
{
	/** branch group where the solids are set */
	private BranchGroup solidsBG;
	/** behavior to be able to pick the solids */
	private GeneralPickBehavior pickBehavior;
	/** if solid must be presented as wireframe or renderized normally */
	protected boolean wireframeView = false;
	
	/**
	 * Constructs a SceneGraphManager that shows the solids into the received canvas and
	 * notifying about selections the received listener 
	 * 
	 * @param canvas3d screen where the solids are shown
	 * @param listener listener that has to be notified about solid selections
	 */
	public SceneGraphManager(Canvas3D canvas3d, SolidsSelectionListener listener)
	{
		SimpleUniverse simpleU = new SimpleUniverse(canvas3d);
		Transform3D viewerTranslation = new Transform3D();
		viewerTranslation.setTranslation(new Vector3f(0,0,50));
		simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(viewerTranslation);
		BranchGroup scene = createSceneGraph(simpleU, canvas3d, listener);
		scene.compile();	
		simpleU.addBranchGraph(scene);
	}
	
	/**
	 * Creates the main scene graph
	 * 
	 * @param simpleU virtual universe where the scene graph is set 
	 * @param canvas3d screen where the solids are shown
	 * @param listener listener that has to be notified about solid selections
	 * @return the main scene graph
	 */
	private BranchGroup createSceneGraph(SimpleUniverse simpleU, Canvas3D canvas3d, SolidsSelectionListener listener)
	{
		BranchGroup objRoot = new BranchGroup();
		
		// set branch group where solids are set
		solidsBG = new BranchGroup();
		solidsBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		solidsBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		solidsBG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		objRoot.addChild(solidsBG);
				
		//picking
		pickBehavior = new GeneralPickBehavior(objRoot, canvas3d, listener);
		objRoot.addChild(pickBehavior);
				
		//light
		BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0),50);
		DirectionalLight lightD = new DirectionalLight();
		lightD.setDirection(new Vector3f(0.0f,-1,-3.0f));
		lightD.setInfluencingBounds(bounds);
		objRoot.addChild(lightD);
		AmbientLight lightA = new AmbientLight();
		lightA.setInfluencingBounds(bounds);
		lightA.setColor(new Color3f(0.3f, 0.3f, 0.3f));
		objRoot.addChild(lightA);
		
		return objRoot;
	}
	
	/**
	 * Adds a solid into the scene graph
	 * 
	 * @param solid solid to be added into the scene graph
	 */
	public void addSolid(CSGSolid solid)
	{
		solid.setWireframeView(wireframeView);
		
		BranchGroup solidBG = new BranchGroup();
		solidBG.setCapability(BranchGroup.ALLOW_DETACH);
		solidBG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		
		//hierarchy
		solidBG.addChild(solid);
		solidsBG.addChild(solidBG);
	}
	
	/** Removes all the selected solids */
	public void removeSelectedSolids()
	{
		BranchGroup bg;
		CSGSolid solid;
		Enumeration list = solidsBG.getAllChildren();
		while(list.hasMoreElements())
		{
			bg = (BranchGroup)list.nextElement();
			solid = (CSGSolid)bg.getChild(0);
			if(solid.isLighted())
			{
				solidsBG.removeChild(bg);
				bg.removeChild(solid);
			}
		}
	}
	
	/**
	 * Removes a solid from the scene graph
	 * 
	 * @param solid solid to be removed from the scene graph
	 */
	public void removeSolid(CSGSolid solid)
	{
		BranchGroup bg;
		CSGSolid activeSolid;
		Enumeration list = solidsBG.getAllChildren();
		while(list.hasMoreElements())
		{
			bg = (BranchGroup)list.nextElement();
			activeSolid = (CSGSolid)bg.getChild(0);
			if(solid==activeSolid)
			{
				solidsBG.removeChild(bg);
				bg.removeChild(activeSolid);
				return;
			}
		}
	}
	
	/**
	 * Sets move mode on
	 * 
	 * @param solid move mode solid
	 */
	public void setMoveMode(CSGSolid solid)
	{
		pickBehavior.setMoveMode(solid);
	}
	
	/**
	 * Sets move mode off
	 * 
	 * @param solid solid to be selected after the move mode is off 
	 */
	public void unsetMoveMode(CSGSolid solid)
	{
		pickBehavior.unsetMoveMode(solid);
	}
	
	/**
	 * Defines if solids must be presented as wireframe or renderized normally 
	 * 
	 * @param wireFrameView true to the solid be presented as wireframe, false to be renderized normally
	 */
	public void setWireFrameView(boolean wireFrameView)
	{
		this.wireframeView = wireFrameView;
		
		BranchGroup bg;
		CSGSolid solid;
		Enumeration list = solidsBG.getAllChildren();
		while(list.hasMoreElements())
		{
			bg = (BranchGroup)list.nextElement();
			solid = (CSGSolid)bg.getChild(0);
			solid.setWireframeView(wireFrameView);
		}
	}
}