package matthew.ui;

import javax.swing.*;

/**
 * User: matthew
 * Date: 25/04/14
 * Time: 20:23
 */
public class ProjectOverview
{
	private JPanel projectOverviewPanel;
	private JList entitiesList;
	private JButton addNewEntityButton;
	private JButton validateEntityStructureButton;
	private JButton createProjectButton;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("ProjectOverview");
		frame.setContentPane(new ProjectOverview().projectOverviewPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
