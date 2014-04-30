package matthew.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.types.PropertyType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PropertyOverview
{
	public static transient Logger log = Logger.getLogger(PropertyOverview.class);

	private JPanel propertyViewPanel;
	private JCheckBox idCheckboxCheckBox;
	private JTextField nameTextField;
	private JTextField typeTextField;
	private JCheckBox nullableCheckBoxCheckBox;
	private JCheckBox serialiseCheckBoxCheckBox;
	private JCheckBox textAreaCheckBoxCheckBox;
	private JTextField incomingTextField;
	private JTextField mappingTextField;
	private JButton cancelButton;
	private JButton saveButton;
	private JTextArea incomingHintTextArea;
	private JTextArea mappingHintTextArea;
	private JTextArea textAreaHintTextArea;
	private JTextArea serialiseHintTextArea;
	private JTextArea nullableHintTextArea;
	private JTextArea typeHintTextArea;
	private JTextArea nameHintTextArea;
	private JTextArea idHintTextArea;
	private JTextArea errorTextArea;


	private final JFrame frame;
	private final EntityType entity;
	private final boolean entityHasId;


	public PropertyOverview(final JFrame frame, final EntitiesType entities, final EntityType entity,
			final boolean entityHasId)
	{
		this.frame = frame;
		this.entity = entity;
		this.entityHasId = entityHasId;


		cancelButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);

				new EntityOverview(frame, entities, entity).loadPanel();
			}
		});


		saveButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				resetError();

				if (createNewProperty())
				{
					new EntityOverview(frame, entities, entity).loadPanel();
				}
			}
		});
	}

	public void loadPanel()
	{
		frame.setContentPane(propertyViewPanel);
		frame.revalidate();

		errorTextArea.setEnabled(false);
		generatePropertyHints();

		if (entityHasId)
		{
			idCheckboxCheckBox.setEnabled(false);
		}

	}

	private void resetError()
	{
		errorTextArea.setText("");
	}

	private void setError(final String error)
	{
		errorTextArea.setText(error);
		log.warn(error);
	}

	private boolean createNewProperty()
	{
		final PropertyType property = new PropertyType();

		boolean error = false;

		final String propertyName = nameTextField.getText().replace(" ", "");
		if (StringUtils.isEmpty(propertyName))
		{
			error = true;
			setError("Warning property name is required!");
		}
		if (!StringUtils.isAlphanumeric(propertyName))
		{
			error = true;
			setError("Warning property name is not fully alphanumeric!");
		}
		property.setName(propertyName);

		final String propertyType = typeTextField.getText().replace(" ", "");
		if (StringUtils.isEmpty(propertyType))
		{
			error = true;
			setError("Warning property type is required!");
		}
		if (!StringUtils.isAlphanumeric(propertyType))
		{
			error = true;
			setError("Warning property type is not fully alphanumeric!");
		}
		property.setType(propertyType);

		property.setId(idCheckboxCheckBox.isSelected());
		property.setSerialise(serialiseCheckBoxCheckBox.isSelected());
		property.setNullable(nullableCheckBoxCheckBox.isSelected());
		property.setTextArea(textAreaCheckBoxCheckBox.isSelected());

		final String propertyIncoming = incomingTextField.getText().replace(" ", "");
		if (!StringUtils.isAlphanumeric(propertyIncoming))
		{
			error = true;
			setError("Warning property incoming is not fully alphanumeric!");
		}
		property.setIncoming(propertyIncoming);

		final String propertyMapping = mappingTextField.getText().replace(" ", "");
		if (!StringUtils.isAlphanumeric(propertyMapping))
		{
			error = true;
			setError("Warning property mapping is not fully alphanumeric!");
		}
		property.setMapping(propertyMapping);

		if (!error)
		{
			entity.getProperty().add(property);
		}

		// if no error, creation is a success
		return !error;
	}

	private void generatePropertyHints()
	{
		incomingHintTextArea.setEnabled(false);
		mappingHintTextArea.setEnabled(false);
		textAreaHintTextArea.setEnabled(false);
		serialiseHintTextArea.setEnabled(false);
		nullableHintTextArea.setEnabled(false);
		typeHintTextArea.setEnabled(false);
		nameHintTextArea.setEnabled(false);
		idHintTextArea.setEnabled(false);

		incomingHintTextArea.setWrapStyleWord(true);
		mappingHintTextArea.setWrapStyleWord(true);
		textAreaHintTextArea.setWrapStyleWord(true);
		serialiseHintTextArea.setWrapStyleWord(true);
		nullableHintTextArea.setWrapStyleWord(true);
		typeHintTextArea.setWrapStyleWord(true);
		nameHintTextArea.setWrapStyleWord(true);
		idHintTextArea.setWrapStyleWord(true);

		incomingHintTextArea.setLineWrap(true);
		mappingHintTextArea.setLineWrap(true);
		textAreaHintTextArea.setLineWrap(true);
		serialiseHintTextArea.setLineWrap(true);
		nullableHintTextArea.setLineWrap(true);
		typeHintTextArea.setLineWrap(true);
		nameHintTextArea.setLineWrap(true);
		idHintTextArea.setLineWrap(true);

		incomingHintTextArea.setText(
				"When using a complex type, this will give the name to the variable on the other end. If using " +
				"complex" +
				" types, this is required."
		);
		mappingHintTextArea.setText(
				"This determines the mapping used for complex types. The valid inputs are OneToMany, " +
				"ManyToOne and ManyToMany. Use OneToMany for a list of the complex type in this entity. Use " +
				"ManyToOne" +
				" " +
				"for a list of this Entity in the complex type. Use ManyToMany for a list of this entity in the " +
				"complex type and a list of the complex type in this entity."
		);
		textAreaHintTextArea.setText(
				"Set this flag when using string type properties which will be longer than a couple of words. This " +
				"will allow the property to be more clearly displayed in the web UI."
		);
		serialiseHintTextArea.setText(
				"Set this flag if you want the property to be available in serialised form. This will allow the " +
				"property to be included in the xml the rest services use for CRUD operations. This will also allow " +
				"the property to be displayed in the web UI. If the property is the entities id it will always be " +
				"serialised."
		);
		nullableHintTextArea.setText(
				"Set this flag if you want the property to be nullable. If the property is the entities id this will" +
				" " +
				"be ignored, the id cannot be nullable."
		);
		typeHintTextArea.setText(
				"This determines the type of the property. The simple types allowed are string, int, integer, long, " +
				"boolean, date and datetime. Complex types are allowed, the type should be the same as the name of " +
				"the" +
				" entity which provides the type."
		);
		nameHintTextArea.setText(
				"This determines the name of the property. Only alphanumeric characters are allowed and the first " +
				"character must not be numerical."
		);
		idHintTextArea.setText(
				"This determines if the property will be used as the ID for the entity. Only one ID should be used " +
				"per" +
				" entity so this box will be disabled if one already exists for this entity."
		);

	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$()
	{
		propertyViewPanel = new JPanel();
		propertyViewPanel.setLayout(new GridLayoutManager(11, 3, new Insets(5, 5, 5, 5), -1, -1));
		final JLabel label1 = new JLabel();
		label1.setText("ID");
		propertyViewPanel.add(label1,
		                      new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		idCheckboxCheckBox = new JCheckBox();
		idCheckboxCheckBox.setText("");
		propertyViewPanel.add(idCheckboxCheckBox,
		                      new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_CAN_GROW,
		                                          GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false
		                      )
		);
		final JLabel label2 = new JLabel();
		label2.setText("Name");
		propertyViewPanel.add(label2,
		                      new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		nameTextField = new JTextField();
		propertyViewPanel.add(nameTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST,
		                                                         GridConstraints.FILL_HORIZONTAL,
		                                                         GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                         GridConstraints.SIZEPOLICY_FIXED, null,
		                                                         new Dimension(150, -1), null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("Type");
		propertyViewPanel.add(label3,
		                      new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		typeTextField = new JTextField();
		propertyViewPanel.add(typeTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST,
		                                                         GridConstraints.FILL_HORIZONTAL,
		                                                         GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                         GridConstraints.SIZEPOLICY_FIXED, null,
		                                                         new Dimension(150, -1), null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("Nullable");
		propertyViewPanel.add(label4,
		                      new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		final JLabel label5 = new JLabel();
		label5.setText("Serialise");
		propertyViewPanel.add(label5,
		                      new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		final JLabel label6 = new JLabel();
		label6.setText("Text Area");
		propertyViewPanel.add(label6,
		                      new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		final JLabel label7 = new JLabel();
		label7.setText("Incoming");
		propertyViewPanel.add(label7,
		                      new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		final JLabel label8 = new JLabel();
		label8.setText("Mapping");
		propertyViewPanel.add(label8,
		                      new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		nullableCheckBoxCheckBox = new JCheckBox();
		nullableCheckBoxCheckBox.setText("");
		propertyViewPanel.add(nullableCheckBoxCheckBox,
		                      new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_CAN_GROW,
		                                          GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false
		                      )
		);
		serialiseCheckBoxCheckBox = new JCheckBox();
		serialiseCheckBoxCheckBox.setText("");
		propertyViewPanel.add(serialiseCheckBoxCheckBox,
		                      new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_CAN_GROW,
		                                          GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false
		                      )
		);
		textAreaCheckBoxCheckBox = new JCheckBox();
		textAreaCheckBoxCheckBox.setText("");
		propertyViewPanel.add(textAreaCheckBoxCheckBox,
		                      new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_CAN_GROW,
		                                          GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false
		                      )
		);
		incomingTextField = new JTextField();
		propertyViewPanel.add(incomingTextField, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST,
		                                                             GridConstraints.FILL_HORIZONTAL,
		                                                             GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                             GridConstraints.SIZEPOLICY_FIXED, null,
		                                                             new Dimension(150, -1), new Dimension(300, -1), 0,
		                                                             false));
		mappingTextField = new JTextField();
		propertyViewPanel.add(mappingTextField, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST,
		                                                            GridConstraints.FILL_HORIZONTAL,
		                                                            GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                            GridConstraints.SIZEPOLICY_FIXED, null,
		                                                            new Dimension(150, -1), new Dimension(300, -1), 0,
		                                                            false));
		incomingHintTextArea = new JTextArea();
		propertyViewPanel.add(incomingHintTextArea,
		                      new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		mappingHintTextArea = new JTextArea();
		propertyViewPanel.add(mappingHintTextArea,
		                      new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		textAreaHintTextArea = new JTextArea();
		propertyViewPanel.add(textAreaHintTextArea,
		                      new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		serialiseHintTextArea = new JTextArea();
		propertyViewPanel.add(serialiseHintTextArea,
		                      new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		nullableHintTextArea = new JTextArea();
		propertyViewPanel.add(nullableHintTextArea,
		                      new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		typeHintTextArea = new JTextArea();
		propertyViewPanel.add(typeHintTextArea,
		                      new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		nameHintTextArea = new JTextArea();
		propertyViewPanel.add(nameHintTextArea,
		                      new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		idHintTextArea = new JTextArea();
		idHintTextArea.setText("");
		propertyViewPanel.add(idHintTextArea,
		                      new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		final JLabel label9 = new JLabel();
		label9.setText("Property Attributes");
		propertyViewPanel.add(label9,
		                      new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                          GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                          null, null, null, 0, false)
		);
		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		propertyViewPanel.add(cancelButton, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_NORTH,
		                                                        GridConstraints.FILL_HORIZONTAL,
		                                                        GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                        GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
		                                                        false
		));
		saveButton = new JButton();
		saveButton.setText("Save");
		propertyViewPanel.add(saveButton, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_NORTH,
		                                                      GridConstraints.FILL_HORIZONTAL,
		                                                      GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                      GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                      GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
		                                                      false
		));
		errorTextArea = new JTextArea();
		propertyViewPanel.add(errorTextArea,
		                      new GridConstraints(10, 0, 1, 3, GridConstraints.ANCHOR_CENTER,
		                                          GridConstraints.FILL_BOTH,
		                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                          GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                      )
		);
		label2.setLabelFor(nameTextField);
		label3.setLabelFor(typeTextField);
		label7.setLabelFor(incomingTextField);
		label8.setLabelFor(mappingTextField);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{ return propertyViewPanel; }
}
