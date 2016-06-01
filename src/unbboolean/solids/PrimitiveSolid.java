package unbboolean.solids;

import java.util.Arrays;
import javax.vecmath.Color3f;

/**
 * Class representing a primitive solid
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public abstract class PrimitiveSolid extends CSGSolid
{
	/**
	 * String representation of a primitive solid (to be used on the CSG Tree)
	 *
	 * @return string representation
	 */
	public String toString()
	{
		return name;
	}

	/**
	 * Gets color
	 *
	 * @return color
	 */
	public Color3f getColor()
	{
		return (Color3f)colors[0].clone();
	}

    /**
	 * Gets color
	 *
	 * @return color
	 */
	public void setColor(Color3f color)
	{
        Arrays.fill(colors, color);
        defineGeometry();
	}
}