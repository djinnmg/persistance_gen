package matthew.ui;

import javax.swing.*;

/**
 * User: matthew
 * Date: 25/04/14
 * Time: 20:35
 */
public class EntityOverview
{
	private JPanel entityViewPanel;
	private JTable propertyTable;
	private JButton deleteButton;
	private JButton editButton;
	private JButton addNewPropertyButton;
	private JTextField textField1;
	private JButton updateNameButton;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("EntityOverview");
		frame.setContentPane(new EntityOverview().entityViewPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
