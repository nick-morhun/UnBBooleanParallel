package unbboolean.gui.save;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filter class used to select obj files on file choosers 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class ObjFilter extends FileFilter  
{
	public boolean accept (File f)
	{
		return f.getName().toLowerCase().endsWith(".obj") || f.isDirectory(); 
	}

	public String getDescription()
	{
		return "Wavefront solid file (*.obj)";
	}
}