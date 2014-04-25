package matthew.ui;

import javax.swing.*;

/**
 * User: matthew
 * Date: 25/04/14
 * Time: 21:14
 */
public class CreateNewEntity
{
	private JPanel createEntityPanel;
	private JTextField entityNameTextField;
	private JButton createEntityButton;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("CreateNewEntity");
		frame.setContentPane(new CreateNewEntity().createEntityPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
