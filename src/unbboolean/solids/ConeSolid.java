package unbboolean.solids;

import java.io.File;
import javax.vecmath.Color3f;

/**
 * Class representing a cone
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class ConeSolid extends PrimitiveSolid
{
	/** cone ray in X */
	private double rayX;
	/** cone ray in Z */
	private double rayZ;
	/** cone height */
	private double height;

	/**
	 * Constructs a customized Cone object
	 *
	 * @param name cone name
	 * @param height cone height
	 * @param rayX cone ray in X
	 * @param rayZ cone ray in Z
	 * @param color cone color
	 */
	public ConeSolid(String name, double height, double rayX, double rayZ, Color3f color)
	{
		super();

		this.name = name;
		this.height = height;
		this.rayX = rayX;
		this.rayZ = rayZ;

		setData(DefaultCoordinates.DEFAULT_CONE_VERTICES, DefaultCoordinates.DEFAULT_CONE_COORDINATES, color);
		scale(rayX, height, rayZ);
	}

	/**
	 * Copies the solid
	 *
	 * @return solid copy
	 */
	public CSGSolid copy()
	{
		ConeSolid cone = new ConeSolid(name, height, rayX, rayZ, getColor());
		// TODO Check copying
        cone.setRotation(rotation);
        cone.setTranslation(translation);
		return cone;
	}

	/**
	 * Gets height
	 *
	 * @return height
	 */
	public double getHeight()
	{
		return height;
	}

	/**
	 * Gets ray in X
	 *
	 * @return ray in X
	 */
	public double getRayX()
	{
		return rayX;
	}

	/**
	 * Gets ray in Z
	 *
	 * @return ray in z
	 */
	public double getRayZ()
	{
		return rayZ;
	}
}