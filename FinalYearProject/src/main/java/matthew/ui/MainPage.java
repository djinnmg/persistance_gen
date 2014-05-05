package matthew.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.peterphi.std.util.jaxb.JAXBSerialiser;
import matthew.jaxb.types.EntitiesType;
import matthew.jaxb.types.ObjectFactory;
import matthew.jaxb.validation.JaxbTypeValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.xml.bind.JAXBElement;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainPage {
    public static transient Logger log = Logger.getLogger(MainPage.class);

    private JPanel mainPagePanel;
    private JTextField XMLDataModelLocationTextField;
    private JButton generateProjectFromXMLButton;
    private JButton createDataModelUsingButton;
    private JTextArea errorTextArea;

    private final JFrame frame;

    public MainPage(final JFrame frame) {
        this.frame = frame;

        // create a new data model using the UI builder tools
        createDataModelUsingButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                final EntitiesType entities = new EntitiesType();

                // Open the overview page of all the entities in the project(will be an empty list at this stage)
                new ProjectOverview(frame, entities).loadPanel();
            }
        });

        // Use a pre-existing xml file to generate the entities
        generateProjectFromXMLButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                resetError();

                // load the xml file and deserialise to an entities type
                final EntitiesType entities = getEntitiesFromXml(XMLDataModelLocationTextField.getText());

                // check if deserialisation happened successfully and open the create new project page,
                // if not entities will be null
                if (entities != null) {
                    new CreateNewProject(frame, entities).loadPanel();
                }
            }
        });
    }

    /**
     * Load the mainPage panel
     */
    public void loadPanel() {
        frame.setContentPane(mainPagePanel);
        frame.revalidate();

        errorTextArea.setEnabled(false);
    }

    /**
     * Load the xml file at dataModelLocation and deserialise to an entitiesType
     *
     * @param dataModelLocation The location of the xml file to deserialise
     * @return the deserialised entitiesType
     */
    private EntitiesType getEntitiesFromXml(final String dataModelLocation) {
        JAXBSerialiser serialiser = JAXBSerialiser.getInstance(ObjectFactory.class);

        // load xml file
        InputStream is = null;
        try {
            is = new FileInputStream(new File(dataModelLocation));
        } catch (FileNotFoundException e) {
            setError("Could not find resource at location: " + dataModelLocation);
            return null;
        }


        // deserialise
        EntitiesType entitiesType = (
                (JAXBElement<EntitiesType>) serialiser
                        .deserialise(is)).getValue();

        // validate
        if (validateEntities(entitiesType)) {
            return entitiesType;
        } else {
            return null;
        }
    }

    /**
     * Use the JaxbTypeValidator validateEntityStructure to validate the entities type and output errors if any are
     * present
     *
     * @param entitiesType The EntityStructure to validate
     * @return true if valid, false otherwise
     */
    private boolean validateEntities(final EntitiesType entitiesType) {
        String errorMessage = JaxbTypeValidator.validateEntityStructure(entitiesType);

        if (StringUtils.isNotEmpty(errorMessage)) {
            setError(errorMessage);
            return false;
        } else {
            return true;
        }
    }

    /**
     * reset the error text area
     */
    private void resetError() {
        errorTextArea.setText("");
    }

    /**
     * Set the value of the error text area and log the error
     *
     * @param errorMessage The message to be output
     */
    private void setError(final String errorMessage) {
        errorTextArea.setText(errorMessage);
        log.warn(errorMessage);
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
    private void $$$setupUI$$$() {
        mainPagePanel = new JPanel();
        mainPagePanel.setLayout(new GridLayoutManager(6, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Relational Database Generation");
        mainPagePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPagePanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPagePanel.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPagePanel.add(spacer3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        generateProjectFromXMLButton = new JButton();
        generateProjectFromXMLButton.setText("Generate Project From XML");
        generateProjectFromXMLButton.setMnemonic('G');
        generateProjectFromXMLButton.setDisplayedMnemonicIndex(0);
        mainPagePanel.add(generateProjectFromXMLButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        XMLDataModelLocationTextField = new JTextField();
        mainPagePanel.add(XMLDataModelLocationTextField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("XML Data Model Location");
        mainPagePanel.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createDataModelUsingButton = new JButton();
        createDataModelUsingButton.setText("Create Data Model Using GUI");
        createDataModelUsingButton.setMnemonic('C');
        createDataModelUsingButton.setDisplayedMnemonicIndex(0);
        mainPagePanel.add(createDataModelUsingButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        errorTextArea = new JTextArea();
        mainPagePanel.add(errorTextArea, new GridConstraints(5, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        label2.setLabelFor(XMLDataModelLocationTextField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPagePanel;
    }
}
