package unbboolean.gui;

/**
 * Interface representing a progress monitor. It must be notified about the 
 * progress of the operations and may request its abortion
 * 
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */
public interface J3DBoolProgressListener 
{
	/**
	 * Notifies that one more operation has finished executing
	 * 
	 * @return true if the process must be cancelled, false otherwise
	 */
	public boolean notifyProgress();

}
