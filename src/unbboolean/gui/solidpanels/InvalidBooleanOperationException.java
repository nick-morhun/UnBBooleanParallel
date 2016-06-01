package unbboolean.gui.solidpanels;

/**
 * Exception representing a situation where the solid resulting of a boolean operation 
 * is empty 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class InvalidBooleanOperationException extends Exception
{
	/** Constructs a default InvalidBooleanOperationException object */
	public InvalidBooleanOperationException()
	{
		super();
	}
	
	/**
	 * Constructs a new exception with the specified detail message 
	 * 
	 * @param message detail message
	 */
	public InvalidBooleanOperationException(String message)
	{
		super(message);
	}
}