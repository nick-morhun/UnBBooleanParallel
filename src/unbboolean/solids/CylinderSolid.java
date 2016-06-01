package unbboolean.solids;

import javax.vecmath.Color3f;

/**
 * Class representing a cylinder
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class CylinderSolid extends PrimitiveSolid
{
	/** cylinder ray in X */
	private double rayX;
	/** cylinder ray in Z */
	private double rayZ;
	/** cylinder height */
	private double height;

	/**
	 * Constructs a customized Cylinder object
	 *
	 * @param name cylinder name
	 * @param height cylinder height
	 * @param rayX cylinder ray in X
	 * @param rayZ cylinder ray in Z
	 * @param color cylinder color
	 */
	public CylinderSolid(String name, double height, double rayX, double rayZ, Color3f color)
	{
		super();

		this.name = name;
		this.height = height;
		this.rayX = rayX;
		this.rayZ = rayZ;

		setData(DefaultCoordinates.DEFAULT_CYLINDER_VERTICES, DefaultCoordinates.DEFAULT_CYLINDER_COORDINATES, color);
		scale(rayX, height, rayZ);
	}

	/**
	 * Copies the solid
	 *
	 * @return solid copy
	 */
	public CSGSolid copy()
	{
		CylinderSolid cylinder = new CylinderSolid(name, height, rayX, rayZ, getColor());
		// TODO Check copying
        cylinder.setRotation(rotation);
        cylinder.setTranslation(translation);
		return cylinder;
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
	 * @return ray in Z
	 */
	public double getRayZ()
	{
		return rayZ;
	}
}