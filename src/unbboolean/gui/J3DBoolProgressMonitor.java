package unbboolean.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Class representing a progress monitor. When the start() method is called, the method
 * executeBooleanOperations() is started and a window opens, showing a progress bar
 * indicating the progress in the method and a cancel button to abort the operations. The 
 * method executeBooleanOperations() must call the method notifyProgress() every time an 
 * operation is accomplished to notify this monitor. This method returns a boolean value 
 * indicating if the user has pressed cancel button or not. If true is returned, the 
 * process must be stopped by the method.
 * 
 * If executeBooleanOperations() calls another methods to execute the operations, they must
 * receive as parameter an instance of this monitor to make the notifications. It's a good 
 * idea to pass it as a reference of J3DBoolProgressListener, that contains only the method 
 * they should know, notifyProgress().
 * 
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public abstract class J3DBoolProgressMonitor extends JDialog implements J3DBoolProgressListener 
{
	private Thread task;
	
	private int currentOperation = 0;
	private int numberOfOperations;
	private boolean cancelRequested = false;
	
	private JProgressBar progressBar;
	
	/**
	 * Default constructor
	 * 
	 * @param numberOfOperations total number of ooperations
	 */
	public J3DBoolProgressMonitor(int numberOfOperations)
	{
		this.numberOfOperations = numberOfOperations;
		
		setModal(true);
		setTitle("Progress");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		//center screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - 150) / 2, (screenSize.height - 100) / 2);
		
		//progress bar
		JPanel barPanel = new JPanel();
		progressBar = new JProgressBar(0, numberOfOperations);
		progressBar.setString("0 of " + numberOfOperations + " operations completed");
		progressBar.setPreferredSize(new Dimension(250, 25));
        progressBar.setStringPainted(true);
        barPanel.add(progressBar);
        
        //cancel button
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent evt) 
			{
				cancelRequested = true;	
				setVisible(false);
			}
        });
        buttonPanel.add(cancelButton);

        //add components
        Box panel = Box.createVerticalBox();
        panel.add(barPanel);
        panel.add(buttonPanel);
        getContentPane().add(panel);
        pack();
        
        //operations are executed in a separate thread
        task = new Thread()
        {
			public void run() 
			{
				executeBooleanOperations();
				setVisible(false);
			}
        };
	}
	
	/** Start operations and monitoring */
	public void start()
	{
		//if there is only one operations to execute, don't show the monitor
		if(numberOfOperations > 1)
		{
			task.start();
			setVisible(true);
		}
		else
		{
			executeBooleanOperations();
		}
	}

	/** 
	 * Execute boolean operations. Must call notifyProgress() when an operation is 
	 * accomplished and must stop when this method returns false
	 */
	public abstract void executeBooleanOperations();
	
	/**
	 * Notifies that one more operation has finished executing
	 * 
	 * @return true if the process must be cancelled, false otherwise
	 */
	public boolean notifyProgress() 
	{
		currentOperation++;
		progressBar.setString(currentOperation + " of " + J3DBoolProgressMonitor.this.numberOfOperations + " operations completed");
		progressBar.setValue(currentOperation);
		
		return cancelRequested;
	}
}
