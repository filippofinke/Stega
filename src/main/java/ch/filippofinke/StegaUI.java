package ch.filippofinke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;

/**
 * The StegaUI class represents the user interface for the Stega application.
 * It extends JFrame and implements the ActionListener interface to handle
 * button clicks and other events.
 */
public class StegaUI extends JFrame implements ActionListener {
    private JButton selectSourceButton;
    private JButton selectDestinationButton;
    private JTextField textField;
    private JButton encodeButton;
    private JButton decodeButton;
    private JLabel statusLabel;
    private JFileChooser sourceFileChooser;
    private JFileChooser destinationFileChooser;
    private Path sourcePath;
    private Path destinationPath;

    /**
     * Constructs a new StegaUI object. It initializes the UI components,
     * sets up the layout, adds the components, and attaches listeners.
     */
    public StegaUI() {
        setTitle("Stega");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));
        setPreferredSize(new Dimension(600, 200));
        setResizable(false);

        createComponents();
        addComponents();
        attachListeners();

        pack();
        setLocationRelativeTo(null); // Center the window on the screen
    }

    /**
     * Creates the UI components used in the StegaUI.
     */
    private void createComponents() {
        selectSourceButton = new JButton("Select Source Image");
        selectDestinationButton = new JButton("Select Destination Image");
        textField = new JTextField(20);
        encodeButton = new JButton("Encode");
        decodeButton = new JButton("Decode");
        statusLabel = new JLabel("Select source and destination images.");

        sourceFileChooser = new JFileChooser();
        destinationFileChooser = new JFileChooser();
    }

    /**
     * Adds the UI components to the JFrame.
     */
    private void addComponents() {
        JPanel sourceDestinationPanel = createPanelWithLayout(new FlowLayout());
        sourceDestinationPanel.add(selectSourceButton);
        sourceDestinationPanel.add(selectDestinationButton);

        JPanel textPanel = createPanelWithLayout(new FlowLayout());
        JLabel textLabel = new JLabel("Text to Hide:");
        textPanel.add(textLabel);
        textPanel.add(textField);

        JPanel encodeDecodePanel = createPanelWithLayout(new FlowLayout());
        encodeDecodePanel.add(encodeButton);
        encodeDecodePanel.add(decodeButton);

        JPanel statusPanel = createPanelWithLayout(new FlowLayout());
        statusPanel.add(statusLabel);

        add(sourceDestinationPanel);
        add(textPanel);
        add(encodeDecodePanel);
        add(statusPanel);
    }

    /**
     * Creates a JPanel with the specified layout.
     *
     * @param layout the layout manager for the panel.
     * @return the created JPanel.
     */
    private JPanel createPanelWithLayout(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setPreferredSize(new Dimension(600, 50));
        return panel;
    }

    /**
     * Attaches action listeners to the UI buttons.
     */
    private void attachListeners() {
        selectSourceButton.addActionListener(this);
        selectDestinationButton.addActionListener(this);
        encodeButton.addActionListener(this);
        decodeButton.addActionListener(this);
    }

    /**
     * Handles the actionPerformed event for UI buttons.
     *
     * @param e the ActionEvent representing the button click.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectSourceButton) {
            handleSelectSourceButton();
        } else if (e.getSource() == selectDestinationButton) {
            handleSelectDestinationButton();
        } else if (e.getSource() == encodeButton) {
            handleEncodeButton();
        } else if (e.getSource() == decodeButton) {
            handleDecodeButton();
        }
    }

    /**
     * Handles the select source button click event.
     * Displays a file chooser dialog and sets the selected source image path.
     */
    private void handleSelectSourceButton() {
        int result = sourceFileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            sourcePath = sourceFileChooser.getSelectedFile().toPath();
            statusLabel.setText("Selected source image: " + sourcePath.toString());
        }
    }

    /**
     * Handles the select destination button click event.
     * Displays a file chooser dialog and sets the selected destination image path.
     */
    private void handleSelectDestinationButton() {
        int result = destinationFileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            destinationPath = destinationFileChooser.getSelectedFile().toPath();
            statusLabel.setText("Selected destination: " + destinationPath.toString());
        }
    }

    /**
     * Handles the encode button click event.
     * Encodes the text using the selected source and destination images.
     */
    private void handleEncodeButton() {
        String text = textField.getText();
        if (sourcePath != null && destinationPath != null) {
            try {
                Main.hideText(text, sourcePath, destinationPath);
                statusLabel.setText("Text encoded successfully!");
            } catch (IOException ex) {
                statusLabel.setText("Error encoding text: " + ex.getMessage());
            }
        } else {
            statusLabel.setText("Please select source and destination images.");
        }
    }

    /**
     * Handles the decode button click event.
     * Decodes the hidden text from the selected source image.
     */
    private void handleDecodeButton() {
        if (sourcePath != null) {
            try {
                String hiddenText = Main.revealText(sourcePath);
                if (hiddenText != null) {
                    statusLabel.setText("Hidden text: " + hiddenText);
                } else {
                    statusLabel.setText("No hidden text found!");
                }
            } catch (IOException ex) {
                statusLabel.setText("Error decoding text: " + ex.getMessage());
            }
        } else {
            statusLabel.setText("Please select source image.");
        }
    }
}
