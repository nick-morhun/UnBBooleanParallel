package unbboolean.gui.save;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filter class used to select csgscene files on file choosers
 *
 * @author N. Morhun
 */
public class CSGsceneFilter extends FileFilter
{
	public boolean accept (File f)
	{
		return f.getName().toLowerCase().endsWith(".csgscene") || f.isDirectory();
	}

	public String getDescription()
	{
		return "CSG scene file (*.csgscene)";
	}
}
