package matthew.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.peterphi.std.util.jaxb.JAXBSerialiser;
import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.ObjectFactory;
import matthew.velocity.processing.TemplateProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class CreateNewProject
{
	public static transient Logger log = Logger.getLogger(CreateNewProject.class);
	private final JFrame frame;
	private JPanel createNewProjectPanel;
	private JButton createButton;
	private JTextField projectNameTextField;
	private JTextField packagePathTextField;
	private JTextField outputDirectoryTextField;
	private JTextArea projectNameHintTextArea;
	private JTextArea packagePathHintTextArea;
	private JTextArea outputDirectoryHintTextArea;
	private JButton backToEntityDesignerButton;
	private JTextArea errorTextArea;


	public CreateNewProject(final JFrame frame, final EntitiesType entities)
	{
		this.frame = frame;
		final JAXBSerialiser serialiser = JAXBSerialiser.getInstance(ObjectFactory.class);
		serialiser.setPrettyOutput(true);

		// Try to create a new project
		createButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);

				resetError();

				final String outputDirectory = outputDirectoryTextField.getText();
				final String packagePath = packagePathTextField.getText();
				final String projectName = projectNameTextField.getText();

				// validate project details and try to create
				if (validateOutputDirectoryPath(outputDirectory) &&
				    validatePackagePath(packagePath) &&
				    validateProjectName(projectName))
				{
					final String dataModelXml = serialiser.serialise(new ObjectFactory().createEntities(entities));

					log.debug("Data Model: \n" + dataModelXml);

					outputDataModel(outputDirectory, projectName, dataModelXml);

					final TemplateProcessor processor =
							new TemplateProcessor(packagePath, outputDirectory, projectName);

					// try to process the entities
					try
					{
						processor.processEntities(entities);
					}
					// catch any error, log to the ui and rethrow error
					catch (Exception ex)
					{
						setError("Error processing the entities structure! " + ex.getMessage());
						throw ex;
					}

					setError("Success! The project has been generated at location: " + outputDirectory + "/" +
					         projectName);

					new MainPage(frame).loadPanel();
				}
			}
		});

		//return to entities designer page to modify entities if required
		backToEntityDesignerButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);

				new ProjectOverview(frame, entities).loadPanel();
			}
		});
	}


	/**
	 * Load the createNewProject panel
	 */
	public void loadPanel()
	{
		frame.setContentPane(createNewProjectPanel);
		frame.revalidate();

		errorTextArea.setEnabled(false);

		generateHintFields();
	}

	/**
	 * generate the hint fields which give hints for inputting project details
	 */
	private void generateHintFields()
	{
		outputDirectoryHintTextArea.setEnabled(false);
		packagePathHintTextArea.setEnabled(false);
		projectNameHintTextArea.setEnabled(false);

		outputDirectoryHintTextArea.setWrapStyleWord(true);
		packagePathHintTextArea.setWrapStyleWord(true);
		projectNameHintTextArea.setWrapStyleWord(true);

		outputDirectoryHintTextArea.setLineWrap(true);
		packagePathHintTextArea.setLineWrap(true);
		projectNameHintTextArea.setLineWrap(true);

		outputDirectoryHintTextArea.setText(
				"This is the base directory into which the generated project will be output starting from root e.g. " +
				"/home/Code. " +
				"FilePaths" +
				" must be alphanumeric with the first character being a letter. Trailing slashes are not valid. The " +
				"filepath must also be above root."
		);
		packagePathHintTextArea.setText(
				"This is the base package path to use in the generated code e.g. using com.test will produce a " +
				"package" +
				" path of com.test.rest.service for the rest service interfaces. The package path must be valid for " +
				"use as a java package. It must be alphanumeric with the first character being a letter. Each level " +
				"of" +
				" the path is separated by a period but there must not be any trailing periods."
		);
		projectNameHintTextArea.setText(
				"This is the name of the project which will be generated e.g. Demo. It must be alphanumeric with the" +
				" " +
				"first character being a letter."
		);
	}

	/**
	 * Validates an output directory
	 *
	 * @param outputPath the output path to validate
	 * @return true if valid, false otherwise
	 */
	private boolean validateOutputDirectoryPath(final String outputPath)
	{
		log.debug("Validating output directory: " + outputPath);

		// only alphanumeric with 1st char being a letter, no trailing slashes, / not allowed either
		final boolean valid = Pattern.matches("^/[a-zA-Z][a-zA-Z0-9]*(/[a-zA-Z][a-zA-Z0-9]*)*$", outputPath);

		if (!valid)
		{
			setError("Output directory is not valid!");
		}

		return valid;
	}

	/**
	 * Validates a package path
	 *
	 * @param packagePath The package path to be validated
	 * @return true if valid, false otherwise
	 */
	private boolean validatePackagePath(final String packagePath)
	{
		log.debug("Validating package path...");
		final boolean valid = Pattern.matches("^[a-zA-Z][a-zA-Z0-9]*(.[a-zA-Z][a-zA-Z0-9]*)*$", packagePath);

		if (!valid)
		{
			setError("Package path is not valid!");
		}

		return valid;
	}

	/**
	 * Validates a project name
	 *
	 * @param projectName The project name to be validated
	 * @return true if valid, false otherwise
	 */
	private boolean validateProjectName(final String projectName)
	{
		log.debug("Validating project name...");
		final boolean valid = Pattern.matches("^[a-zA-Z][a-zA-Z0-9]*$", projectName);

		if (!valid)
		{
			setError("Project name is not valid!");
		}

		return valid;
	}

	/**
	 * Outputs the data model for the serialised form of the generated entitiesType in the newly generated project
	 *
	 * @param outputDir    The directory to output the project in
	 * @param projectName  The name of the project to be generated
	 * @param fileContents The serialised form of the generated entitiesType
	 */
	private void outputDataModel(final String outputDir, final String projectName, final String fileContents)
	{
		try
		{
			// output the generated data model to the resources directory in the generated project
			File file = new File(outputDir + "/" + projectName + "/src/main/resources/DataModel.xml");

			if (file.exists())
			{
				file.delete();
			}

			file.getParentFile().mkdirs();

			System.out.println("Trying to output data model to " + file.getAbsolutePath());

			FileUtils.writeStringToFile(file, fileContents);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	/**
	 * Sets the text in the error text area and logs a warning
	 *
	 * @param errorMessage The message to be output
	 */
	private void setError(final String errorMessage)
	{
		errorTextArea.setText(errorMessage);
		log.warn(errorMessage);
	}

	/**
	 * Resets the error text area
	 */
	private void resetError()
	{
		errorTextArea.setText("");
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
		createNewProjectPanel = new JPanel();
		createNewProjectPanel.setLayout(new GridLayoutManager(9, 3, new Insets(5, 5, 5, 5), -1, -1));
		final Spacer spacer1 = new Spacer();
		createNewProjectPanel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                       GridConstraints.FILL_HORIZONTAL,
		                                                       GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null,
		                                                       null, 0, false));
		final Spacer spacer2 = new Spacer();
		createNewProjectPanel.add(spacer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                       GridConstraints.FILL_VERTICAL, 1,
		                                                       GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                       new Dimension(-1, 15), null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Project Name:");
		createNewProjectPanel.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST,
		                                                      GridConstraints.FILL_NONE,
		                                                      GridConstraints.SIZEPOLICY_FIXED,
		                                                      GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1,
		                                                      false));
		final JLabel label2 = new JLabel();
		label2.setText("Package Path:");
		createNewProjectPanel.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST,
		                                                      GridConstraints.FILL_NONE,
		                                                      GridConstraints.SIZEPOLICY_FIXED,
		                                                      GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
		                                                      false));
		final JLabel label3 = new JLabel();
		label3.setText("Output Directory:");
		createNewProjectPanel.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST,
		                                                      GridConstraints.FILL_NONE,
		                                                      GridConstraints.SIZEPOLICY_FIXED,
		                                                      GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
		                                                      false));
		final Spacer spacer3 = new Spacer();
		createNewProjectPanel.add(spacer3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                       GridConstraints.FILL_VERTICAL, 1,
		                                                       GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                       new Dimension(-1, 250), null, 0, false));
		createButton = new JButton();
		createButton.setText("Create");
		createNewProjectPanel.add(createButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                            GridConstraints.FILL_HORIZONTAL,
		                                                            GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                                            GridConstraints.SIZEPOLICY_CAN_GROW,
		                                                            GridConstraints.SIZEPOLICY_FIXED, null, null, null,
		                                                            0, false));
		projectNameTextField = new JTextField();
		createNewProjectPanel.add(projectNameTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST,
		                                                                    GridConstraints.FILL_HORIZONTAL,
		                                                                    GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                                    GridConstraints.SIZEPOLICY_FIXED, null,
		                                                                    new Dimension(150, -1), null, 0, false));
		packagePathTextField = new JTextField();
		createNewProjectPanel.add(packagePathTextField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST,
		                                                                    GridConstraints.FILL_HORIZONTAL,
		                                                                    GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                                    GridConstraints.SIZEPOLICY_FIXED, null,
		                                                                    new Dimension(150, -1), null, 0, false));
		outputDirectoryTextField = new JTextField();
		createNewProjectPanel.add(outputDirectoryTextField, new GridConstraints(4, 1, 1, 1,
		                                                                        GridConstraints.ANCHOR_WEST,
		                                                                        GridConstraints.FILL_HORIZONTAL,
		                                                                        GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                                        GridConstraints.SIZEPOLICY_FIXED, null,
		                                                                        new Dimension(150, -1), null, 0,
		                                                                        false));
		final Spacer spacer4 = new Spacer();
		createNewProjectPanel.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                       GridConstraints.FILL_VERTICAL, 1,
		                                                       GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                       new Dimension(-1, 25), null, 0, false));
		final Spacer spacer5 = new Spacer();
		createNewProjectPanel.add(spacer5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                                       GridConstraints.FILL_HORIZONTAL,
		                                                       GridConstraints.SIZEPOLICY_WANT_GROW, 1, null,
		                                                       new Dimension(150, -1), null, 0, false));
		projectNameHintTextArea = new JTextArea();
		createNewProjectPanel.add(projectNameHintTextArea,
		                          new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                              GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW,
		                                              GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
		                                              false));
		packagePathHintTextArea = new JTextArea();
		createNewProjectPanel.add(packagePathHintTextArea,
		                          new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                              GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW,
		                                              GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
		                                              false));
		outputDirectoryHintTextArea = new JTextArea();
		createNewProjectPanel.add(outputDirectoryHintTextArea,
		                          new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                              GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW,
		                                              GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
		                                              false));
		final JLabel label4 = new JLabel();
		label4.setText("Create New Project");
		createNewProjectPanel.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
		                                                      GridConstraints.FILL_NONE,
		                                                      GridConstraints.SIZEPOLICY_FIXED,
		                                                      GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
		                                                      false));
		backToEntityDesignerButton = new JButton();
		backToEntityDesignerButton.setText("Back To Entity Designer");
		createNewProjectPanel.add(backToEntityDesignerButton,
		                          new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
		                                              GridConstraints.FILL_HORIZONTAL,
		                                              GridConstraints.SIZEPOLICY_CAN_SHRINK |
		                                              GridConstraints.SIZEPOLICY_CAN_GROW,
		                                              GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		errorTextArea = new JTextArea();
		createNewProjectPanel.add(errorTextArea, new GridConstraints(8, 0, 1, 3, GridConstraints.ANCHOR_CENTER,
		                                                             GridConstraints.FILL_BOTH,
		                                                             GridConstraints.SIZEPOLICY_WANT_GROW,
		                                                             GridConstraints.SIZEPOLICY_WANT_GROW, null,
		                                                             new Dimension(150, 50), null, 0, false));
		label1.setLabelFor(projectNameTextField);
		label2.setLabelFor(packagePathTextField);
		label3.setLabelFor(outputDirectoryTextField);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{
		return createNewProjectPanel;
	}
}
