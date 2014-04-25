package matthew.ui;

import javax.swing.*;

/**
 * User: matthew
 * Date: 25/04/14
 * Time: 17:51
 */
public class CreateNewProject
{
	private JPanel createNewProjectPanel;
	private JButton createButton;
	private JTextField projectNameTextField;
	private JTextField packagePathTextField;
	private JTextField outputDirectoryTextField;
	private JTextArea projectNameHintTextArea;
	private JTextArea packagePathHintTextArea;
	private JTextArea outputDirectoryHintTextArea;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("CreateNewProject");
		frame.setContentPane(new CreateNewProject().createNewProjectPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
