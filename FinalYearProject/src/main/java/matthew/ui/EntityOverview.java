package matthew.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.types.PropertyType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class EntityOverview
{

	public static transient Logger log = Logger.getLogger(EntityOverview.class);

	private JPanel entityViewPanel;
	private JTable propertyTable;
	private JButton deleteButton;
	private JButton addNewPropertyButton;
	private JTextField entityNameField;
	private JButton updateNameButton;
	private JButton viewEntitiesButton;
	private JButton updatePropertiesButton;
	private JTextArea errorTextArea;

	private final JFrame frame;
	private final EntitiesType entities;
	private final EntityType entity;

	public EntityOverview(final JFrame frame, final EntitiesType entities, final EntityType entity)
	{
		this.frame = frame;
		this.entities = entities;
		this.entity = entity;

		// Try to update the entity name if it is valid
		updateNameButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				resetError();

				final String updatedEntityName = entityNameField.getText().replace(" ", "");

				// Check validity of hopeful update name
				if (!StringUtils.isAlphanumeric(updatedEntityName))
				{
					setError("Entity name must be fully alphanumeric!");
				}

				entity.setName(updatedEntityName);

			}
		});

		// Return to the overview of all entities in the project
		viewEntitiesButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				resetError();

				// Check that the entity has exactly one id property before returning to overview page
				if (getEntityHasMoreThanOneId())
				{
					setError("An Entity should only have one ID property");
				}
				else if (!getEntityHasId())
				{
					setError("An Entity requires an ID property");
				}
				else
				{
					new ProjectOverview(frame, entities).loadPanel();
				}
			}
		});

		// delete the entity and return overview of all entities in the project
		deleteButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);

				entities.getEntity().remove(entity);
				new ProjectOverview(frame, entities).loadPanel();
			}
		});

		// Open the property page to create a new property for this entity
		addNewPropertyButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);

				// Open the creation page and pass in if the entity has an id already so an extra id can't be added by
				// mistake
				new PropertyOverview(frame, entities, entity, getEntityHasId()).loadPanel();
			}
		});

		// Check the table of properties for the entity for any modifications and try to update modified fields.
		updatePropertiesButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				resetError();

				// check for any modified properties, update them and check if the page needs refreshed
				if (UpdateChangedProperties())
				{
					// validate entity has exactly one id
					if (getEntityHasMoreThanOneId())
					{
						setError("An Entity should only have one ID property");
					}
					else if (!getEntityHasId())
					{
						setError("An Entity requires an ID property");
					}
					else
					{
						// TODO loadPropertyTable(); instead of whole page?
						// refresh page
						new EntityOverview(frame, entities, entity).loadPanel();
					}
				}
			}
		});
	}

	/**
	 * Loads the entityViewPanel
	 */
	public void loadPanel()
	{
		frame.setContentPane(entityViewPanel);
		frame.revalidate();

		entityNameField.setText(entity.getName());

		loadPropertyTable();
	}

	/**
	 * Resets the error text area
	 */
	private void resetError()
	{
		errorTextArea.setText("");
	}

	/**
	 * Sets the error in the error text area and logs the error
	 *
	 * @param error The error to be output
	 */
	private void setError(final String error)
	{
		errorTextArea.setText(error);
		log.warn(error);
	}

	/**
	 * Determine if the entity has an ID
	 *
	 * @return true if has an ID, false otherwise
	 */
	private boolean getEntityHasId()
	{
		for (PropertyType property : entity.getProperty())
		{
			if (property.isId())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the entity has more than one id
	 *
	 * @return true if entity has more than one id, false otherwise
	 */
	private boolean getEntityHasMoreThanOneId()
	{
		log.debug("Checking for more than one Id property in Entity");

		int numIds = 0;

		for (PropertyType property : entity.getProperty())
		{
			if (property.isId())
			{
				numIds++;
			}
		}

		log.debug("No. Id properties: " + numIds);

		return numIds > 1;
	}

	/**
	 * Loads the property table with all the properties of the entity
	 */
	private void loadPropertyTable()
	{
		String[] columnHeaders = {"Name", "Type", "Id", "Serialise", "Nullable", "Text Area", "Incoming", "Mapping"};
		DataModel tableModel = new DataModel(columnHeaders, entity.getProperty().size());
		propertyTable.setModel(tableModel);

		int tableRow = 0;

		for (PropertyType property : entity.getProperty())
		{
			if (tableRow >= entity.getProperty().size())
			{
				throw new RuntimeException("Trying to display row " + tableRow + 1 + " in property table for entity "
				                           + entity.getName() + " but entity only has " + entity.getProperty().size() +
				                           " rows!");
			}

			propertyTable.setValueAt(property.getName(), tableRow, 0);
			propertyTable.setValueAt(property.getType(), tableRow, 1);
			propertyTable.setValueAt(property.isId(), tableRow, 2);
			propertyTable.setValueAt(property.isSerialise(), tableRow, 3);
			propertyTable.setValueAt(property.isNullable(), tableRow, 4);
			propertyTable.setValueAt(property.isTextArea(), tableRow, 5);
			propertyTable.setValueAt(property.getIncoming(), tableRow, 6);
			propertyTable.setValueAt(property.getMapping(), tableRow, 7);

			tableRow++;
		}
	}

	/**
	 * Determine if any of the properties in the property table have changed. If they are valid update the properties
	 *
	 * @return true if the page needs to be refreshed
	 */
	private boolean UpdateChangedProperties()
	{
		boolean updateRequired = false;

		for (int i = 0; i < propertyTable.getRowCount(); i++)
		{
			final PropertyType property = entity.getProperty().get(i);

			final String updateName = (String) propertyTable.getValueAt(i, 0);
			if (!StringUtils.equals(property.getName(), updateName))
			{
				if (StringUtils.isEmpty(updateName))
				{
					setError("Warning property name is required at row " + i + "!");
					return false;
				}
				else if (!StringUtils.isAlphanumeric(updateName))
				{
					setError("Warning property name is not fully alphanumeric at row " + i + "!");
					return false;
				}
				else
				{
					setError("Updating property name to " + updateName);
					property.setName(updateName);
					updateRequired = true;
				}
			}

			final String updateType = (String) propertyTable.getValueAt(i, 1);
			if (!StringUtils.equals(property.getType(), updateType))
			{
				if (StringUtils.isEmpty(updateType))
				{
					setError("Warning property type is required at row " + i + "!");
					return false;
				}
				else if (!StringUtils.isAlphanumeric(updateType))
				{
					setError("Warning property type is not fully alphanumeric at row " + i + "!");
					return false;
				}
				else
				{
					setError("Updating property type to " + updateType);
					property.setType(updateType);
					updateRequired = true;
				}
			}


			final Boolean updateId = (Boolean) propertyTable.getValueAt(i, 2);
			if (updateId != property.isId())
			{
				setError("Updating property ID to " + updateId);
				property.setId(updateId);
				updateRequired = true;
			}

			final Boolean updateSerialise = (Boolean) propertyTable.getValueAt(i, 3);
			if (updateSerialise != property.isSerialise())
			{
				setError("Updating property serialise to " + updateSerialise);
				property.setSerialise(updateSerialise);
				updateRequired = true;
			}

			final Boolean updateNullable = (Boolean) propertyTable.getValueAt(i, 4);
			if (updateNullable != property.isNullable())
			{
				setError("Updating property nullable to " + updateNullable);
				property.setNullable(updateNullable);
				updateRequired = true;
			}

			final Boolean updateTextArea = (Boolean) propertyTable.getValueAt(i, 5);
			if (updateTextArea != property.isTextArea())
			{
				setError("Updating property textArea to " + updateTextArea);
				property.setTextArea(updateTextArea);
				updateRequired = true;
			}

			final String updateIncoming = (String) propertyTable.getValueAt(i, 6);
			if (!StringUtils.equals(property.getIncoming(), updateIncoming))
			{
				if (!StringUtils.isAlphanumeric(updateIncoming))
				{
					setError("Warning property incoming is not fully alphanumeric at row " + i + "!");
					return false;
				}
				else
				{
					setError("Updating property incomming to " + updateIncoming);
					property.setIncoming(updateIncoming);
					updateRequired = true;
				}
			}

			final String updateMapping = (String) propertyTable.getValueAt(i, 7);
			if (!StringUtils.equals(property.getMapping(), updateMapping))
			{
				if (!StringUtils.isAlphanumeric(updateMapping))
				{
					setError("Warning property mapping is not fully alphanumeric at row " + i + "!");
					return false;
				}
				else
				{
					setError("Updating property mapping to " + updateMapping);
					property.setMapping(updateMapping);
					updateRequired = true;
				}
			}
		}

		log.debug("Update required equals " + updateRequired);

		return updateRequired;
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
		entityViewPanel = new JPanel();
		entityViewPanel.setLayout(new GridLayoutManager(7, 5, new Insets(5, 5, 5, 5), -1, -1));
		final JLabel label1 = new JLabel();
		label1.setText("Properties");
		entityViewPanel.add(label1,
		                    new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                        null, null, null, 0, false)
		);
		final Spacer spacer1 = new Spacer();
		entityViewPanel.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                 GridConstraints.FILL_VERTICAL, 1,
		                                                 GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
		                                                 false));
		final Spacer spacer2 = new Spacer();
		entityViewPanel.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                 GridConstraints.FILL_VERTICAL, 1,
		                                                 GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                 new Dimension(-1, 15), null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		entityViewPanel.add(scrollPane1,
		                    new GridConstraints(4, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
		                                        GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                        GridConstraints.SIZEPOLICY_WANT_GROW,
		                                        GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                        GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false
		                    )
		);
		propertyTable = new JTable();
		scrollPane1.setViewportView(propertyTable);
		deleteButton = new JButton();
		deleteButton.setText("Delete");
		deleteButton.setMnemonic('D');
		deleteButton.setDisplayedMnemonicIndex(0);
		entityViewPanel.add(deleteButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                      GridConstraints.FILL_HORIZONTAL,
		                                                      GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                      GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                      GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
		                                                      false
		));
		addNewPropertyButton = new JButton();
		addNewPropertyButton.setText("Add New Property");
		addNewPropertyButton.setMnemonic('A');
		addNewPropertyButton.setDisplayedMnemonicIndex(0);
		entityViewPanel.add(addNewPropertyButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                              GridConstraints.FILL_HORIZONTAL,
		                                                              GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                              GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                              GridConstraints.SIZEPOLICY_FIXED, null, null,
		                                                              null, 0, false
		));
		final Spacer spacer3 = new Spacer();
		entityViewPanel.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                 GridConstraints.FILL_HORIZONTAL,
		                                                 GridConstraints.SIZEPOLICY_WANT_GROW, 1, null,
		                                                 new Dimension(40, -1), null, 0, false));
		entityNameField = new JTextField();
		entityViewPanel.add(entityNameField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
		                                                         GridConstraints.FILL_HORIZONTAL,
		                                                         GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                         GridConstraints.SIZEPOLICY_FIXED, null,
		                                                         new Dimension(150, -1), null, 0, false));
		updateNameButton = new JButton();
		updateNameButton.setText("Update Name");
		entityViewPanel.add(updateNameButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                          GridConstraints.FILL_HORIZONTAL,
		                                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                          GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                          GridConstraints.SIZEPOLICY_FIXED, null, null,
		                                                          null, 0,
		                                                          false
		));
		final JLabel label2 = new JLabel();
		label2.setText("Entity Overview");
		entityViewPanel.add(label2,
		                    new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
		                                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
		                                        null, null, null, 0, false)
		);
		viewEntitiesButton = new JButton();
		viewEntitiesButton.setText("View Entities");
		entityViewPanel.add(viewEntitiesButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                            GridConstraints.FILL_HORIZONTAL,
		                                                            GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                            GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                            GridConstraints.SIZEPOLICY_FIXED, null, null, null,
		                                                            0, false
		));
		updatePropertiesButton = new JButton();
		updatePropertiesButton.setText("Update Properties");
		entityViewPanel.add(updatePropertiesButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                                GridConstraints.FILL_HORIZONTAL,
		                                                                GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                                GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                                GridConstraints.SIZEPOLICY_FIXED, null, null,
		                                                                null, 0, false
		));
		errorTextArea = new JTextArea();
		entityViewPanel.add(errorTextArea, new GridConstraints(6, 0, 1, 5, GridConstraints.ANCHOR_SOUTH,
		                                                       GridConstraints.FILL_HORIZONTAL,
		                                                       GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                       GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                       new Dimension(150, 150), new Dimension(-1, 150), 0,
		                                                       false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{ return entityViewPanel; }

	private class DataModel extends DefaultTableModel
	{

		public DataModel(Object[] columnNames, int rowCount)
		{
			super(columnNames, rowCount);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex > 1 && columnIndex < 6)
			{
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}
	}

}
