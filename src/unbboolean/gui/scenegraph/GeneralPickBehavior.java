package unbboolean.gui.scenegraph;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.swing.SwingUtilities;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import unbboolean.solids.CSGSolid;
import unbboolean.solids.CompoundSolid;

/**
 * Class responsible for applying transformations on solids where the user drags
 * one of them with a mouse on a screen
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com) (modified)
 */
public class GeneralPickBehavior extends Behavior {

    /**
     * last solid pressed on a screen
     */
    private CSGSolid solid = null;
    /**
     * a second solid selected on a screen
     */
    private CSGSolid solid2 = null;
    /**
     * last registered position of a mouse drag on a solid
     */
    private int xpos, ypos;
    /**
     * says if move mode is on
     */
    private boolean moveMode = false;
    /**
     * wakeup condition
     */
    private WakeupOr wakeupCondition;
    /**
     * catch mouse event
     */
    private MouseEvent mevent;
    /**
     * used to get the picked solid
     */
    private PickCanvas pickScene;
    /**
     * listener that receives information about solids selection
     */
    private SolidsSelectionListener listener;
    /**
     * if a button were pressed on the last wakeup
     */
    private boolean buttonPress = false;
    /**
     * if allowed direct translation
     */
    private boolean mouseTranslate = false;
    private TransformGroup viewPlatformTG;
    private Point3d eye = new Point3d(0.0, 0.0, 30.0);
    private Point3d at = new Point3d(0.0, 0.0, 0.0);
    protected Vector3d up = new Vector3d(0.0, 1.0, 0.0);
    Transform3D lookAt = new Transform3D();
    // Keep a few Transform3Ds for use during event processing. This
    // avoids having to allocate new ones on each event.
//    protected Transform3D currentTransform = new Transform3D();
    protected Transform3D transform1 = new Transform3D();
//    protected Transform3D transform2 = new Transform3D();
    protected Matrix4d matrix = new Matrix4d();
    protected Vector3d origin = new Vector3d(0.0, 0.0, 0.0);
    protected Vector3d translation = new Vector3d(0.0, 0.0, 0.0);
    protected Vector3d rotation = new Vector3d(0.0, 0.0, 0.0);
    protected Vector3d forward = new Vector3d(0.0, 0.0, 0.0);
    protected double scale = 0.5d;

    // ------------------------------CONSTRUCTORS------------------------------------//
    /**
     * Constructs a GeneralPickBehavior object
     *
     * @param root root of the scene graph where the solids are
     * @param canvas screen where the iteration with the solids occurs
     * @param listener listener that receives information about solids selection
     */
    public GeneralPickBehavior(BranchGroup root, Canvas3D canvas,
            SolidsSelectionListener listener, TransformGroup viewPlatformTG) {
        pickScene = new PickCanvas(canvas, root);
        this.setSchedulingBounds(new BoundingSphere());
        this.listener = listener;
        this.viewPlatformTG = viewPlatformTG;
    }

    // --------------------------BEHAVIOR_METHODS------------------------------------//
    /**
     * Initializes the behavior attributes
     */
    public void initialize() {
        WakeupCriterion[] conditions = new WakeupCriterion[3];
        conditions[0] = new WakeupOnAWTEvent(Event.MOUSE_DOWN);
        conditions[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        conditions[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
        //conditions[3] = new WakeupOnAWTEvent(MouseWheelEvent.MOUSE_WHEEL);
        wakeupCondition = new WakeupOr(conditions);

        wakeupOn(wakeupCondition);
    }

    /**
     * Applies a transformation when a solid is dragged
     *
     * @param criteria set of stimulus received
     */
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup = (WakeupCriterion) criteria.nextElement();
        AWTEvent[] evt = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
        int xPos, yPos;

        mevent = (MouseEvent) evt[0];
        processMouseEvent((MouseEvent) evt[0]);
        xPos = mevent.getPoint().x;
        yPos = mevent.getPoint().y;

        if (evt[0].getID() == MouseEvent.MOUSE_RELEASED) {
            if (solid instanceof CompoundSolid) {
                CompoundSolid compound = (CompoundSolid) solid;
                compound.updateChildren();
            }
        } else {
            updateScene(xPos, yPos);
        }

        wakeupOn(wakeupCondition);
    }

    /**
     * Identifies the kind of mouse event
     *
     * @param evt mouse event
     */
    private void processMouseEvent(MouseEvent evt) {
        buttonPress = false;

        if (evt.getID() == MouseEvent.MOUSE_PRESSED
                || evt.getID() == MouseEvent.MOUSE_CLICKED) {
            if (!evt.isAltDown() && !evt.isMetaDown()) {
                buttonPress = true;
            }
            this.xpos = evt.getPoint().x;
            this.ypos = evt.getPoint().y;
        }
    }

    /**
     * Applies a transformation according to the mouse movement and the buttons
     * pressed
     *
     * @param xpos current mouse position on the x axis
     * @param ypos current mouse position on the y axis
     */
    private void updateScene(int xpos, int ypos) {
        double dx, dy;
        if (buttonPress) {
            // move mode: only one solid can be selected
            if (moveMode) {
                pickScene.setShapeLocation(xpos, ypos);
                PickResult results[] = pickScene.pickAll();
                int i = 0;
                if (results != null) {
                    for (; i < results.length; i++) {
                        if ((CSGSolid) results[i].getObject() == solid2) {
                            solid = solid2;
                            break;
                        }
                    }
                    if (i == results.length) {
                        solid = null;
                    }
                }
            } else {
                pickScene.setShapeLocation(xpos, ypos);
                PickResult result = pickScene.pickClosest();

                // none solid were selected: deselect all
                if (result == null) {
                    if (solid != null) {
                        listener.deselectSolids();
                    }

                    solid = null;
                    solid2 = null;
                } else {
                    CSGSolid pickedSolid = (CSGSolid) result.getObject();
                    // a new solid were selected
                    if (!pickedSolid.equals(solid)
                            && !pickedSolid.equals(solid2)) {
                        // if control is pressed: if one solid were selected, it
                        // still is, in case o two, one is deselected
                        if (mevent.isControlDown()) {
                            solid2 = solid;
                            solid = pickedSolid;

                            if (solid2 == null) {
                                listener.selectSolid(solid);
                            } else {
                                listener.selectSolids(solid2, solid);
                            }
                        } // else: the currently selected solids are deselected
                        // and the new one is selected
                        else {
                            solid = pickedSolid;
                            solid2 = null;

                            listener.selectSolid(solid);
                        }
                    } // a currently selected solid were selected
                    else {
                        // if control is pressed: the solid is deselected
                        if (mevent.isControlDown()) {
                            if (pickedSolid.equals(solid2)) {
                                solid2 = solid;
                                solid = pickedSolid;
                            }
                        } // else: other currently selected solids are deselected
                        else {
                            if (solid2 != null) {
                                if (pickedSolid == solid) {
                                    solid2 = null;
                                } else {
                                    solid = solid2;
                                    solid2 = null;
                                }
                                listener.selectSolid(pickedSolid);
                            }
                        }
                    }
                }
            }
        } else if (mouseTranslate) {
            // translate solid
            if (!mevent.isAltDown() && !mevent.isMetaDown()) {
                if (solid != null) {
                    solid.translate((xpos - this.xpos) / 25d,
                            (this.ypos - ypos) / 25d, 0);
                }
            } // rotate solid
            else if (!mevent.isAltDown() && mevent.isMetaDown()) {
                if (solid != null) {
                    solid.rotate((ypos - this.ypos) / 50d,
                            (xpos - this.xpos) / 50d, 0);
                }
            } // zoom solid
            else {
                if (solid != null) {
                    solid.translate(0, 0, (ypos - this.ypos) / 25d);
                }
            }
            // viewer transforms
        } else {
            // pan view
            if (!mevent.isAltDown() && !mevent.isMetaDown()) {
                if (!mevent.isControlDown()) {
                    // Build transform
                    transform1.setIdentity();
                    forward.set(at);
                    forward.sub(eye);
                    Vector3d xdir = new Vector3d();
                    xdir.cross(forward, up);
                    xdir.normalize();
                    dx = -(xpos - this.xpos) / 50d;

                    translation.set(xdir);
                    translation.scale(dx);
                    transform1.setTranslation(translation);
                    transform1.transform(eye);
                    transform1.transform(at);

                    transform1.setIdentity();
                    Vector3d ydir = new Vector3d();
                    ydir.cross(forward, xdir);
                    ydir.normalize();
                    // preserve up
                    if (ydir.y < 0) {
                        ydir.y = -ydir.y;
                    }
                    dy = (ypos - this.ypos) / 50d;

                    translation.set(ydir);
                    translation.scale(dy);
                    transform1.setTranslation(translation);
                    transform1.transform(eye);
                    transform1.transform(at);

                    lookAt.lookAt(eye, at, up);
                    lookAt.setScale(scale);
                    lookAt.invert();

                    // Update the transform group
                    viewPlatformTG.setTransform(lookAt);
                } // zoom view
                else {
                    //if (mevent instanceof MouseWheelEvent) {
                    int n = (ypos - this.ypos);//((MouseWheelEvent) mevent).getWheelRotation();
                    if (n > 1 && n < 50) {
                        if (scale < 1) {
                            scale += n / 50d;
                        }
                    } else if (n > -10 && n < -1) {
                        scale += n / 50d;
                    }

                    if (scale < 0.1) {
                        scale = 0.1;
                    }

                    lookAt.lookAt(eye, at, up);
                    lookAt.setScale(scale);
                    lookAt.invert();
                    viewPlatformTG.setTransform(lookAt);
                }

            } else // rotate view around Y
            if (!mevent.isAltDown() && mevent.isMetaDown()) {

                // Build transform
                transform1.setIdentity();
                transform1.rotY((xpos - this.xpos) / 50d);
                //transform1.setRotation(new AxisAngle4d(0, (xpos - this.xpos) / 50d, 0, 0.01));
                transform1.transform(eye);
                lookAt.lookAt(eye, at, up);
                lookAt.setScale(scale);
                lookAt.invert();

                // Update the transform group
                viewPlatformTG.setTransform(lookAt);

            } else // pitch around view target
            if (SwingUtilities.isMiddleMouseButton(mevent) && !mevent.isControlDown()) {

                Vector3d dir = new Vector3d(at);
                dir.sub(eye);
                dir.cross(dir, up);
                dy = -(ypos - this.ypos) / 50d;
                rotation.set(dir);
                transform1.setIdentity();
                transform1.setRotation(new AxisAngle4d(rotation, dy));

                Point3d newEye = new Point3d(eye);
                transform1.transform(newEye);

                //rotation.set(up);
                forward.set(at);
                forward.sub(newEye);
                dy = up.angle(forward);

                // Check constraints.
                if (dy > 0.1d && dy < Math.PI - 0.1d) {
                    eye = newEye;
                    lookAt.lookAt(eye, at, up);
                    lookAt.setScale(scale);
                    lookAt.invert();
                    viewPlatformTG.setTransform(lookAt);
                }
            }
        }
        this.xpos = xpos;
        this.ypos = ypos;
    }

    // -------------------------------------OTHERS-----------------------------------//
    /**
     * Sets move mode on
     *
     * @param solid move mode solid
     */
    public void setMoveMode(CSGSolid solid) {
        moveMode = true;
        this.solid = solid;
        this.solid2 = solid;
        solid.unlight();
    }

    /**
     * Sets move mode off
     *
     * @param solid solid to be selected after the move mode is off
     */
    public void unsetMoveMode(CSGSolid solid) {
        moveMode = false;
        this.solid = solid;
        this.solid2 = null;
        solid.unlight();
    }

    /**
     * Defines if solids can be moved with mouse or with typeins only
     *
     * @param mouseTranslate true to the solid be moved with mouse, false to be
     * moved with typing values
     */
    public void setMouseTranslate(boolean mouseTranslate) {
        this.mouseTranslate = mouseTranslate;
    }

    /**
     * Reset camera position.
     */
    public void resetView() {
        this.eye.set(0.0, 0.0, 30.0);
        this.at.set(0.0, 0.0, 0.0);
        this.up.set(0.0, 1.0, 0.0);
        lookAt.lookAt(eye, at, up);
        lookAt.setScale(scale);
        lookAt.invert();
        viewPlatformTG.setTransform(lookAt);
    }
}