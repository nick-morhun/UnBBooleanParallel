package unbboolean.solids;

import java.io.File;
import javax.vecmath.Color3f;

/**
 * Class representing a sphere
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class SphereSolid extends PrimitiveSolid
{
	/** sphere ray in X */
	private double rayX;
	/** sphere ray in Y */
	private double rayY;
	/** sphere ray in Z */
	private double rayZ;
	
	/**
	 * Constructs a customized Sphere object
	 * 
	 * @param name sphere name
	 * @param rayX sphere ray in X
	 * @param rayY sphere ray in Y
	 * @param rayZ sphere ray in Z
	 * @param color sphere color
	 */	
	public SphereSolid(String name, double rayX, double rayY, double rayZ, Color3f color)
	{
		super();
		
		this.rayX = rayX;
		this.rayY = rayY;
		this.rayZ = rayZ;
		this.name = name;
		
		setData(DefaultCoordinates.DEFAULT_SPHERE_VERTICES, DefaultCoordinates.DEFAULT_SPHERE_COORDINATES, color);
		scale(rayX, rayY, rayZ);
	}
	
	/**
	 * Copies the solid
	 * 
	 * @return solid copy
	 */
	public CSGSolid copy()
	{
		SphereSolid sphere = new SphereSolid(name, rayX, rayY, rayZ, getColor());
		sphere.updateLocation(getLocation());
		return sphere;
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
	 * Gets ray in Y
	 * 
	 * @return ray in Y
	 */

	public double getRayY()
	{
		return rayY;
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