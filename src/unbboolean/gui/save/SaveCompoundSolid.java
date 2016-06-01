package unbboolean.gui.save;

import javax.vecmath.Vector3d;
import unbboolean.gui.J3DBoolProgressListener;
import unbboolean.gui.solidpanels.InvalidBooleanOperationException;
import unbboolean.solids.CSGSolid;
import unbboolean.solids.CompoundSolid;

/**
 * Class representing a compound solid to be saved
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SaveCompoundSolid extends SaveSolid {

    /**
     * operation applied
     */
    private int operation;
    /**
     * first solid operand
     */
    private SaveSolid operand1;
    /**
     * second solid operand
     */
    private SaveSolid operand2;

    private Vector3d operand1r;
    private Vector3d operand1t;

    private Vector3d operand2r;
    private Vector3d operand2t;

    private static final long serialVersionUID = 152008386452776152L;

    /**
     * Constructs a SaveCompoundSolid object based on a CompoundSolid object
     *
     * @param solid compound solid to be saved
     */
    public SaveCompoundSolid(CompoundSolid solid) {
        super(solid);
        operation = solid.getOperation();

        CSGSolid s = solid.getOperand1();
        operand1 = getSaveSolid(s);
        operand1r = s.getRotation();
        operand1t = s.getTranslation();

        s = solid.getOperand2();
        operand2 = getSaveSolid(s);
        operand2r = s.getRotation();
        operand2t = s.getTranslation();
    }

    /**
     * Gets the solid corresponding to this save solid
     *
     * @param listener must be notified when an operation is executed
     * @return the solid corresponding to this save solid
     */
    public CSGSolid getSolid(J3DBoolProgressListener listener) {
        try {
            CSGSolid solid1 = operand1.getSolid(listener);

            if (solid1 == null) {
                //return null when operation is cancelled
                return null;
            }

            CSGSolid solid2 = operand2.getSolid(listener);

            if (solid2 == null) {
                //return null when operation is cancelled
                return null;
            }

            solid1.setRotation(operand1r);
            solid1.setTranslation(operand1t);
            solid2.setRotation(operand2r);
            solid2.setTranslation(operand2t);
            CompoundSolid compound = new CompoundSolid(name, operation, solid1, solid2);

            //notifies the progress and gets if the operation was cancelled
            boolean cancelRequested = listener.notifyProgress();
            if (!cancelRequested) {
                return compound;
            } else {
                //return null when operation is cancelled
                return null;
            }
        } catch (InvalidBooleanOperationException e) {
            return null;
        }
    }

    /**
     * Gets the number of operations used to create the solid (the number of
     * nodes on CSG tree)
     *
     * @return the number of operations used to create the solid
     */
    public int getNumberOfOperations() {
        return operand1.getNumberOfOperations() + operand2.getNumberOfOperations() + 1;
    }
}
