package matthew.ui;

import org.apache.log4j.Logger;

import javax.swing.*;

public class Main
{
	public static transient Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args)
	{
		new Main().loadFrame();
	}

	private void loadFrame()
	{
		log.debug("Loading frame for Generation Application...");
		final JFrame frame = new JFrame("Relational Database Generation Application");
		frame.setSize(1280, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		log.debug("Loading main panel...");
		loadMainPanel(frame);
	}


	private void loadMainPanel(final JFrame frame)
	{
		new MainPage(frame).loadPanel();
	}

}
