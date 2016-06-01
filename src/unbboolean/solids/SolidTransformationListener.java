package unbboolean.solids;

/**
 * Interface whose implementor wants to know about a solid translation
 *
 * @author Morhun
 */
public interface SolidTransformationListener
{
	/**
	 * A solid was rotated
	 *
	 * @param solid rotated solid
	 */
	public void rotateSolid(Solid solid);

	/**
	 * A solid was translated
	 *
	 * @param solid translated solid
	 */
	public void translateSolid(Solid solid);
}