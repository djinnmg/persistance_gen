package matthew.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.EntityType;
import matthew.jaxb.validation.JaxbTypeValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProjectOverview
{
	public static transient Logger log = Logger.getLogger(ProjectOverview.class);

	private JPanel projectOverviewPanel;
	private JList<String> entitiesList;
	private JButton addNewEntityButton;
	private JButton validateEntityStructureButton;
	private JButton createProjectButton;
	private JTextArea errorTextArea;


	private final JFrame frame;
	private final EntitiesType entities;

	public ProjectOverview(final JFrame frame, final EntitiesType entities)
	{
		this.frame = frame;
		this.entities = entities;

		// Open the page for creating new entities
		addNewEntityButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				new CreateNewEntity(frame, entities).LoadPanel();
			}
		});

		// If any entities are double clicked on, open the associated entity overview page
		entitiesList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);

				JList list = (JList) e.getSource();
				if (e.getClickCount() == 2)
				{
					int index = list.locationToIndex(e.getPoint());

					final EntityType selectedEntity = entities.getEntity().get(index);

					new EntityOverview(frame, entities, selectedEntity).loadPanel();
				}
			}
		});

		// Validate entity structure and open create new project page
		createProjectButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				resetError();

				// validate
				String errorString = JaxbTypeValidator.validateEntityStructure(entities);

				if (StringUtils.isNotEmpty(errorString))
				{
					setError(errorString);
				}
				else
				{
					new CreateNewProject(frame, entities).loadPanel();
				}
			}
		});

		// Validate entity structure
		validateEntityStructureButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				resetError();

				// validate
				String errorString = JaxbTypeValidator.validateEntityStructure(entities);

				if (StringUtils.isNotEmpty(errorString))
				{
					setError(errorString);
				}
				else
				{
					errorTextArea.setText("Valid");
				}
			}
		});
	}

	/**
	 * Loads the projectOverview panel
	 */
	public void loadPanel()
	{
		frame.setContentPane(projectOverviewPanel);
		frame.revalidate();

		errorTextArea.setEnabled(false);

		loadEntityList();
	}

	/**
	 * Populates the entitiesList with all the entity names
	 */
	private void loadEntityList()
	{
		DefaultListModel<String> listModel = new DefaultListModel<>();

		for (EntityType entity : entities.getEntity())
		{
			listModel.addElement(entity.getName());
		}

		entitiesList.setModel(listModel);
	}

	/**
	 * Resets the error text area
	 */
	private void resetError()
	{
		errorTextArea.setText("");
	}

	/**
	 * Sets the value of the error text area and logs the error
	 *
	 * @param error the message to be output
	 */
	private void setError(final String error)
	{
		errorTextArea.setText(error);
		log.warn(error);
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
		projectOverviewPanel = new JPanel();
		projectOverviewPanel.setLayout(new GridLayoutManager(9, 4, new Insets(5, 5, 5, 5), -1, -1));
		final JScrollPane scrollPane1 = new JScrollPane();
		projectOverviewPanel.add(scrollPane1, new GridConstraints(3, 0, 4, 1, GridConstraints.ANCHOR_NORTHWEST,
		                                                          GridConstraints.FILL_NONE,
		                                                          GridConstraints.SIZEPOLICY_FIXED,
		                                                          GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                          GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                          new Dimension(500, 400), new Dimension(500, 500),
		                                                          new Dimension(500, 600), 0, false));
		entitiesList = new JList();
		scrollPane1.setViewportView(entitiesList);
		final JLabel label1 = new JLabel();
		label1.setText("Project Overview");
		projectOverviewPanel.add(label1,
		                         new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
		                                             GridConstraints.FILL_NONE,
		                                             GridConstraints.SIZEPOLICY_FIXED,
		                                             GridConstraints.SIZEPOLICY_FIXED,
		                                             null, new Dimension(305, 18), null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Entities");
		projectOverviewPanel.add(label2,
		                         new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST,
		                                             GridConstraints.FILL_NONE,
		                                             GridConstraints.SIZEPOLICY_FIXED,
		                                             GridConstraints.SIZEPOLICY_FIXED,
		                                             null, new Dimension(305, 18), null, 0, false));
		final Spacer spacer1 = new Spacer();
		projectOverviewPanel.add(spacer1, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                      GridConstraints.FILL_HORIZONTAL,
		                                                      GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null,
		                                                      null,
		                                                      0, false));
		addNewEntityButton = new JButton();
		addNewEntityButton.setText("Add New Entity");
		addNewEntityButton.setMnemonic('A');
		addNewEntityButton.setDisplayedMnemonicIndex(0);
		projectOverviewPanel.add(addNewEntityButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST,
		                                                                 GridConstraints.FILL_NONE,
		                                                                 GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                                 GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                                 GridConstraints.SIZEPOLICY_FIXED, null, null,
		                                                                 null, 0, false));
		validateEntityStructureButton = new JButton();
		validateEntityStructureButton.setText("Validate Entity Structure");
		validateEntityStructureButton.setMnemonic('V');
		validateEntityStructureButton.setDisplayedMnemonicIndex(0);
		projectOverviewPanel.add(validateEntityStructureButton,
		                         new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST,
		                                             GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                                        GridConstraints.SIZEPOLICY_CAN_GROW,
		                                             GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		createProjectButton = new JButton();
		createProjectButton.setText("Create Project");
		createProjectButton.setMnemonic('C');
		createProjectButton.setDisplayedMnemonicIndex(0);
		projectOverviewPanel.add(createProjectButton, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST,
		                                                                  GridConstraints.FILL_NONE,
		                                                                  GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                                  GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                                  GridConstraints.SIZEPOLICY_FIXED, null, null,
		                                                                  null, 0, false));
		final Spacer spacer2 = new Spacer();
		projectOverviewPanel.add(spacer2, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                      GridConstraints.FILL_VERTICAL, 1,
		                                                      GridConstraints.SIZEPOLICY_WANT_GROW, null, null,
		                                                      null, 0,
		                                                      false));
		final Spacer spacer3 = new Spacer();
		projectOverviewPanel.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                      GridConstraints.FILL_VERTICAL, 1,
		                                                      GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                      new Dimension(305, 15), null, 0, false));
		final Spacer spacer4 = new Spacer();
		projectOverviewPanel.add(spacer4, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                      GridConstraints.FILL_HORIZONTAL,
		                                                      GridConstraints.SIZEPOLICY_FIXED, 1, null,
		                                                      new Dimension(5, -1), new Dimension(5, -1), 0, false));
		errorTextArea = new JTextArea();
		projectOverviewPanel.add(errorTextArea, new GridConstraints(8, 0, 1, 4, GridConstraints.ANCHOR_CENTER,
		                                                            GridConstraints.FILL_BOTH,
		                                                            GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                            GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                            new Dimension(305, 150), null, 0, false));
		final Spacer spacer5 = new Spacer();
		projectOverviewPanel.add(spacer5, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                      GridConstraints.FILL_VERTICAL, 1,
		                                                      GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                      new Dimension(305, 15), null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{
		return projectOverviewPanel;
	}

	//	private boolean validateEntityStructure(final EntitiesType entitiesType)
//	{
//		if (entitiesType.getEntity().isEmpty())
//		{
//			setError("Entities list is empty");
//			return false;
//		}
//
//		for (EntityType entity : entitiesType.getEntity())
//		{
//			// check has name
//			if (StringUtils.isEmpty(entity.getName()))
//			{
//				setError("Entities require a name!");
//				return false;
//			}
//
//			if (entity.getProperty().isEmpty())
//			{
//				setError("Entity " + entity.getName() + " has no properties!");
//				return false;
//			}
//
//
//			int numIds = 0;
//
//			for (PropertyType property : entity.getProperty())
//			{
//				if (StringUtils.isEmpty(property.getName()))
//				{
//					setError("Entity " + entity.getName() +
//					         " has a property with no name! All properties require names!");
//					return false;
//				}
//
//				if (StringUtils.isEmpty(property.getType()))
//				{
//					setError("Entity " + entity.getName() +
//					         " has a property with no type! All properties require types!");
//					return false;
//				}
//
//				if (property.isId())
//				{
//					if (!StringUtils.equalsIgnoreCase(property.getType(), "int") &&
//					    !StringUtils.equalsIgnoreCase(property.getType(), "long"))
//					{
//						setError("Entity " + entity.getName() + " has id of type " + property.getType() +
//						         ". Only types int and long are allowed for ids!");
//						return false;
//					}
//
//					numIds++;
//				}
//
//				switch (property.getType().toLowerCase())
//				{
//					case "string":
//					case "long":
//					case "int":
//					case "integer":
//					case "boolean":
//					case "date":
//					case "datetime":
//						break;
//					default:
//						if (!checkComplexProperty(property.getType(), entitiesType))
//						{
//							setError("Property type " + property.getType() +
//							         " is not supported! Property is not one of supported types: string, int, " +
//							         "integer," +
//							         " long, boolean, date, datetime and a matching entity name cannot be found for
// a" +
//							         " " +
//							         "complex type!");
//							return false;
//						}
//						// if a property is complex, incoming is required
//						else if (StringUtils.isEmpty(property.getIncoming()))
//						{
//							setError("Complex property " + property.getName() + " in entity " + entity.getName() +
//							         " does not have a value for 'incoming'. This is a required field for complex " +
//							         "properties.");
//							return false;
//						}
//				}
//
//			}
//
//			if (numIds < 1)
//			{
//				setError("Entity " + entity.getName() +
//				         " does not have an id property! All entities require an id property!");
//				return false;
//			}
//
//			if (numIds > 1)
//			{
//				setError("Only one id allowed per entity! Entity " + entity.getName() + " has " + numIds + "!");
//				return false;
//			}
//
//
//		}
//
//		return true;
//	}
//
//	private boolean checkComplexProperty(final String type, final EntitiesType entitiesType)
//	{
//		for (EntityType entity : entitiesType.getEntity())
//		{
//			if (StringUtils.equalsIgnoreCase(entity.getName(), type))
//			{
//				return true;
//			}
//		}
//
//		return false;
//	}


}
