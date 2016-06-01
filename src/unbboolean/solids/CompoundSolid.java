package unbboolean.solids;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import unbboolean.gui.J3DBoolProgressListener;
import unbboolean.gui.solidpanels.InvalidBooleanOperationException;
import unbboolean.j3dbool.BooleanModeller;

/**
 * Class representing a compound solid
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com) (modified by N. Morhun)
 */
public class CompoundSolid extends CSGSolid {

    /**
     * union operation
     */
    public static final int UNION = 1;
    /**
     * intersection operation
     */
    public static final int INTERSECTION = 2;
    /**
     * difference operation
     */
    public static final int DIFFERENCE = 3;
    /**
     * operation applied onto the operands
     */
    private int operation;
    /**
     * first operand
     */
    private CSGSolid operand1;
    /**
     * second operand
     */
    private CSGSolid operand2;

    private Vector3d oldRotation, oldTranslation;

    /**
     * Constructs a customized CompoundSolid object
     *
     * @param name solid name
     * @param operation operation applied onto the operands - UNION,
     * INTERSECTION or DIFFERENCE
     * @param operand1 first operand
     * @param operand2 second operand
     * @throws InvalidBooleanOperationException if a boolean operation generates
     * an empty solid
     */
    public CompoundSolid(String name, int operation, CSGSolid operand1,
            CSGSolid operand2) throws InvalidBooleanOperationException {
        super();
        this.name = name;
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
        oldRotation = new Vector3d();
        oldTranslation = new Vector3d();
        try {

            long time = -System.currentTimeMillis();
            applyBooleanOperation();
            time += System.currentTimeMillis();
            System.out.println("Node construction time " + time + " ms");
            System.out.println();

            operand1.setParentSolid(this);
            operand2.setParentSolid(this);
        } catch (InvalidBooleanOperationException e) {
            throw e;
        }
    }

    /**
     * Constructor used to copy a compound solid. It doesn't apply boolean
     * operations on the operands. Instead, it is constructed with the
     * coordinates of the copied solid. It's much faster.
     *
     * @param name solid name
     * @param operation operation applied onto the operands - UNION,
     * INTERSECTION or DIFFERENCE
     * @param operand1 first operand
     * @param operand2 second operand
     * @param vertices array of points defining the solid vertices
     * @param indices array of indices for a array of vertices
     * @param colors array of colors defining the vertices colors
     */
    private CompoundSolid(String name, int operation, CSGSolid operand1,
            CSGSolid operand2, Point3d[] vertices, int[] indices, Color3f[] colors) {
        this.name = name;
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
        setData(vertices, indices, colors);
        oldRotation = new Vector3d();
        oldRotation.set(rotation);
        oldTranslation = new Vector3d();
        oldTranslation.set(translation);
        operand1.setParentSolid(this);
        operand2.setParentSolid(this);
    }

    /**
     * String representation of a compound solid (to be used on the CSG Tree)
     *
     * @return string representation
     */
    public String toString() {
        if (operation == UNION) {
            return "U";
        } else if (operation == INTERSECTION) {
            return "\u2229";
        } else {
            return "-";
        }
    }

    /**
     * Gets the operation
     *
     * @return operation
     */
    public int getOperation() {
        return operation;
    }

    /**
     * Gets the first operand
     *
     * @return first operand
     */
    public CSGSolid getOperand1() {
        return operand1;
    }

    /**
     * Gets the second operand
     *
     * @return second operand
     */
    public CSGSolid getOperand2() {
        return operand2;
    }

    /**
     * Sets the operation
     *
     * @param operation operation
     * @param listener must be notified when an operation is executed
     * @return true if the user has cancelled the process, false otherwise
     * @throws InvalidBooleanOperationException if a boolean operation generates
     * an empty solid
     */
    public boolean setOperation(int operation, J3DBoolProgressListener listener) throws InvalidBooleanOperationException {
        this.operation = operation;
        System.out.println("Changing the operation...");
        long time = -System.currentTimeMillis();
        boolean cancelRequested = updateItselfAndParents(listener);
        time += System.currentTimeMillis();
        System.out.println("Total time is " + time + " ms");
        System.out.println();
        return cancelRequested;
    }

    /**
     * Sets the operation to inverse difference (invert the operands and apply
     * difference)
     *
     * @param listener must be notified when an operation is executed
     * @return true if the user has cancelled the process, false otherwise
     * @throws InvalidBooleanOperationException if a boolean operation generates
     * an empty solid
     *
     */
    public boolean setOperationToInverseDifference(J3DBoolProgressListener listener) throws InvalidBooleanOperationException {
        this.operation = DIFFERENCE;
        CSGSolid temp = operand1;
        operand1 = operand2;
        operand2 = temp;
        System.out.println("Inversing difference...");
        long time = -System.currentTimeMillis();
        boolean cancelRequested = updateItselfAndParents(listener);
        time += System.currentTimeMillis();
        System.out.println("Total time is " + time + " ms");
        System.out.println();
        return cancelRequested;
    }

    /**
     * Sets the first operand
     *
     * @param solid first operand
     * @param listener must be notified when an operation is executed
     * @return true if the user has cancelled the process, false otherwise
     * @throws InvalidBooleanOperationException if a boolean operation generates
     * an empty solid
     */
    public boolean setOperand1(CSGSolid solid, J3DBoolProgressListener listener) throws InvalidBooleanOperationException {
        // if it wasn't before...
        if (operand1 != solid) {
            operand1 = solid;
            solid.setParentSolid(this);
        }
        System.out.println("Changing operand 1...");
        long time = -System.currentTimeMillis();
        boolean cancelRequested = updateItselfAndParents(listener);
        time += System.currentTimeMillis();
        System.out.println("Total time is " + time + " ms");
        System.out.println();
        return cancelRequested;
    }

    /**
     * Sets the second operand
     *
     * @param solid second operand
     * @param listener must be notified when an operation is executed
     * @return true if the user has cancelled the process, false otherwise
     * @throws InvalidBooleanOperationException if a boolean operation generates
     * an empty solid
     */
    public boolean setOperand2(CSGSolid solid, J3DBoolProgressListener listener) throws InvalidBooleanOperationException {
        // if it wasn't before...
        if (operand2 != solid) {
            operand2 = solid;
            solid.setParentSolid(this);
        }
        System.out.println("Changing operand 2...");
        long time = -System.currentTimeMillis();
        boolean cancelRequested = updateItselfAndParents(listener);
        time += System.currentTimeMillis();
        System.out.println("Total time is " + time + " ms");
        System.out.println();
        return cancelRequested;
    }

    /**
     * Update itself and parents (called when coordinates were changed)
     *
     * @param listener must be notified when an operation is executed
     * @return true if the user has canceled the process, false otherwise
     * @throws InvalidBooleanOperationException if a boolean operation generates
     * an empty solid
     *
     */
    public boolean updateItselfAndParents(J3DBoolProgressListener listener) throws InvalidBooleanOperationException {
        long time = -System.currentTimeMillis();
        applyBooleanOperation();
        time += System.currentTimeMillis();
        System.out.println("Node time is " + time + " ms");
        System.out.println();
        boolean cancelRequested = listener.notifyProgress();
        if (cancelRequested) {
            return true;
        } else {
            return updateParents(listener);
        }
    }

    /**
     * Updates all its descendants (called after some transforms were performed)
     */
    public void updateChildren() {
        Vector3d zero = new Vector3d();
        Vector3d dR = new Vector3d(rotation);
        Vector3d dT = new Vector3d(translation);
        dR.sub(oldRotation);
        dT.sub(oldTranslation);

        // if position has changed
        if (!(dR.equals(zero) && dT.equals(zero))) {
//            CSGSolid solid;
//            CompoundSolid compoundSolid;
//            ArrayList<CSGSolid> descendants = new ArrayList<>();
//            descendants.add(operand1);
//            descendants.add(operand2);
//
//            while (!descendants.isEmpty()) {
//                solid = descendants.remove(0);
//
//                if (solid instanceof CompoundSolid) {
//                    compoundSolid = (CompoundSolid) solid;
//                    descendants.add(compoundSolid.operand1);
//                    descendants.add(compoundSolid.operand2);
//                } else {
//                    solid.rotate(dR.x, dR.y, dR.z);
//                    solid.translate(dT.x, dT.y, dT.z);
//                }
//            }
            oldRotation.set(this.rotation);
            oldTranslation.set(this.translation);
        }
    }

    /**
     * Apply boolean operation taking as account the operation and operands set
     * before
     *
     * @throws InvalidBooleanOperationException if a boolean operation generates
     * an empty solid
     *
     */
    private void applyBooleanOperation() throws InvalidBooleanOperationException {
        BooleanModeller modeller = new BooleanModeller(operand1, operand2);

        Solid solid;
        if (operation == CompoundSolid.UNION) {
            solid = modeller.getUnion();
        } else if (operation == CompoundSolid.INTERSECTION) {
            solid = modeller.getIntersection();
        } else {
            solid = modeller.getDifference();
        }

        if (solid.isEmpty()) {
            throw new InvalidBooleanOperationException();
        } else {
            solid.setRotation(rotation);
            solid.setTranslation(translation);
            setData(solid.getVertices(), solid.getIndices(), solid.getColors());
        }
    }

    /**
     * Copies the solid
     *
     * @return solid copy
     */
    public CSGSolid copy() {
        CompoundSolid clone = new CompoundSolid(name, operation, operand1.copy(),
                operand2.copy(), vertices, indices, colors);
        return clone;
    }

    /**
     * Applies a translation into a solid
     *
     * @param dx translation on the x axis
     * @param dy translation on the y axis
     * @param dz translation on the z axis
     */
    public void translate(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return;
        }
        oldTranslation.set(this.translation);
        super.translate(dx, dy, dz);
        updateChildren();
    }

    /**
     * Applies a rotation into a solid
     *
     * @param dx rotation on the x axis
     * @param dy rotation on the y axis
     * @param dz rotation on the z axis
     */
    public void rotate(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return;
        }
        oldRotation.set(this.rotation);
        super.rotate(dx, dy, dz);
        updateChildren();
    }
}
