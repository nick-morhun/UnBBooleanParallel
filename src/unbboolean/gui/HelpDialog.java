package unbboolean.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Dialog with the instructions about how to use the application
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class HelpDialog extends JDialog 
{
	/** Default constructor */
	public HelpDialog()
	{
		setTitle("Help");
		setModal(true);

		//center screen
		setPreferredSize(new Dimension(800,600));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(screenSize.width > 800 && screenSize.height > 600)
		{
			setLocation((screenSize.width - 800) / 2, (screenSize.height - 600) / 2);
		}
		
		Box helpPanel = Box.createVerticalBox();
		helpPanel.add(new JLabel("<html>TO CREATE A PRIMITIVE:<br>On the 'Primitives' tab, choose a primitive into the list, change its parameters as you like and press 'OK' button.<br><br></html>"));
		helpPanel.add(new JLabel("<html>TO MOVE A SOLID:<br>Dragging the solid with the left mouse button makes it move around the screen (x and y translation). Using the right button, the solid is rotated (x and y rotation). Dragging and holding ALT button makes it move to the user direction or to the opposite one (z translation).<br><br></html>"));
		helpPanel.add(new JLabel("<html>TO REMOVE A SOLID:<br>Select the solid to be removed on the screen with the mouse button and press 'delete' button on the keyboard.<br><br></html>"));
		helpPanel.add(new JLabel("<html>TO COMBINE SOLIDS:<br>Holding CTRL, select and position both solids to be combined. Choose the operation on 'CSG trees' tab and press 'OK' button.<br><br></html>"));
		helpPanel.add(new JLabel("<html>TO CHANGE  A SOLID:<br>a) Change solid parameters: Select the solid to be changed on the screen. Its tree will be shown on the left into the 'CSG trees' tab. Select the node you'd like to change. Change the parameters on the panel below and press 'OK' button.<br>b) Move primitive components of a compound solid: Select the solid to be changed on the screen. Its tree will be shown on the left into the 'CSG trees' tab. Right click the tree leaf relative to the primitive you'd like to change. A popup menu appears. Select 'move' item. Then, move the primitive on the screen as you like. If you want to change its parameters, change it on the panel below the tree and then press 'OK' button and keep on moving it. After the primitive is on the right position, press 'stop moving' button.<br><br></html>"));
		helpPanel.add(new JLabel("<html>TO COPY A SOLID:<br>Select the solid to be copied on the screen. Its tree will be shown on the left into the 'CSG trees' tab. Right click the tree root. A popup menu appears. Select 'copy' item. You can do the same on another node to get a copy of an intermediate solid.<br><br></html>"));
		helpPanel.add(new JLabel("<html>TO SAVE/LOAD A SOLID:<br>On the menu bar, File>Save or File>Load. It saves and loads files on 'csg' format, which stores the tree structure and parameters. Coordinates can be saved in a 'obj' file too, to be used by other applications. Save menu is available when a solid is selected. <br><br></html>"));
		
		JPanel buttonPanel = new JPanel();
		JButton buttonOK = new JButton("OK");
		buttonOK.setAlignmentY(JPanel.TOP_ALIGNMENT);
		buttonOK.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt) 
			{
				setVisible(false);
			}
		});
		buttonPanel.add(buttonOK);
		
		getContentPane().add(helpPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		pack();
	}
}
