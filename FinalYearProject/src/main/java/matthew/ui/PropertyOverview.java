package matthew.ui;

import javax.swing.*;

/**
 * User: matthew
 * Date: 25/04/14
 * Time: 20:45
 */
public class PropertyOverview
{
	private JPanel propertyViewPanel;
	private JCheckBox idCheckboxCheckBox;
	private JTextField nameTextField;
	private JTextField typeTextField;
	private JCheckBox nullableCheckBoxCheckBox;
	private JCheckBox serialiseCheckBoxCheckBox;
	private JCheckBox textAreaCheckBoxCheckBox;
	private JTextField incomingTextField;
	private JTextField mappingTextField;
	private JButton deleteButton;
	private JButton saveButton;
	private JTextArea incomingHintTextArea;
	private JTextArea mappingHintTextArea;
	private JTextArea textAreaHintTextArea;
	private JTextArea serialiseHintTextArea;
	private JTextArea nullableHintTextArea;
	private JTextArea typeHintTextArea;
	private JTextArea nameHintTextArea;
	private JTextArea idHintTextArea;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("PropertyOverview");
		frame.setContentPane(new PropertyOverview().propertyViewPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
