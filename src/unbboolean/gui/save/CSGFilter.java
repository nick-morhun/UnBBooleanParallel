package unbboolean.gui.save;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filter class used to select csg files on file choosers 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class CSGFilter extends FileFilter  
{
	public boolean accept (File f)
	{
		return f.getName().toLowerCase().endsWith(".csg") || f.isDirectory(); 
	}

	public String getDescription()
	{
		return "CSG solid file (*.csg)";
	}
}
