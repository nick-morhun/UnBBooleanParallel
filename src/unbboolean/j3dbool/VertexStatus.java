package unbboolean.j3dbool;

/**
 *
 * @author N. Morhun
 */
public enum VertexStatus {

    /**
     * vertex status if it is still unknown
     */
    UNKNOWN,
    /**
     * vertex status if it is inside a solid
     */
    INSIDE,
    /**
     * vertex status if it is outside a solid
     */
    OUTSIDE,
    /**
     * vertex status if it on the boundary of a solid
     */
    BOUNDARY
}
