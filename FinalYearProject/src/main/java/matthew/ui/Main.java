package matthew.ui;

import matthew.jaxb.types.EntitiesType;

import javax.swing.*;

public class Main
{
	private final EntitiesType entities;

	public Main()
	{
		entities = new EntitiesType();
	}


	public static void main(String[] args)
	{
		new Main().loadFrame();
	}

	private void loadFrame()
	{
		final JFrame frame = new JFrame("Relational Database Generation Application");
		frame.setSize(1280, 720);

		loadProjectOverviewPanel(frame);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// TODO decide layout constraint method
		// frame.pack();
		frame.setVisible(true);
	}


	private void loadProjectOverviewPanel(final JFrame frame)
	{
		new ProjectOverview(frame, entities).loadPanel();
	}

}
