package matthew.ui;

import javax.swing.*;

public class Main
{

	public static void main(String[] args)
	{
		new Main().loadFrame();
	}

	private void loadFrame()
	{
		final JFrame frame = new JFrame("Relational Database Generation Application");
		frame.setSize(1280, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		loadMainPanel(frame);
	}


	private void loadMainPanel(final JFrame frame)
	{
		new MainPage(frame).loadPanel();
	}

}
