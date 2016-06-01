package unbboolean.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * Defines a link to be used at Swing panels. When you click in it, the page related 
 * opens at your default browser
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class LinkButton extends JButton
{
	/**
	 * Builds a default link  
	 * 
	 * @param text the text of the link
	 */
	public LinkButton(String text)
	{
		super(text);
		setBorder(BorderFactory.createEmptyBorder());
		setForeground(Color.BLUE);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				try
				{
					Desktop.getDesktop().browse(new URI(getText()));
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(LinkButton.this, "Error. Please type the address " + getText() + " on your browser.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}