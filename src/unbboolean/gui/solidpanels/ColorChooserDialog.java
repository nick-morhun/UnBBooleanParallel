package unbboolean.gui.solidpanels;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Dialog frame used to choose a color 
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class ColorChooserDialog extends JDialog
{
	/** the last selected color */
	private Color selectedColor;
	/** panel used to choose the color */
	private JColorChooser colorChooser;
	
	/** Constructs a default ColorChooserDialog object */
	public ColorChooserDialog()
	{
		setModal(true);
		Container contentPane = getContentPane();
		selectedColor = new Color(255,0,0);
		
		colorChooser = new JColorChooser(selectedColor);
		contentPane.add(colorChooser, "North");
		
		JPanel buttonsPanel = new JPanel();
		JButton okButton = new JButton("   OK   ");
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectedColor = colorChooser.getColor();
				hide();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				hide();
			}
		});
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		contentPane.add(buttonsPanel, "South");
		pack();
	}
	
	/**
	 * Gets the last selected color
	 * 
	 * @return last selected color
	 */
	public Color getSelectedColor()
	{
		return selectedColor;
	}
}