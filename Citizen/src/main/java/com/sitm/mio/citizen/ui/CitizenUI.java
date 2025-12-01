package com.sitm.mio.citizen.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.sitm.mio.citizen.controller.CitizenController;

/**
 * User Interface for the Citizen application.
 * Allows citizens to query travel time information between stops.
 */
public class CitizenUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CitizenController controller;

    private final JTextField originField;
    private final JTextField destinationField;

    /**
     * Create the Citizen UI.
     */
    public CitizenUI() {
        super("Citizen System – Travel Time Query");
        this.controller = new CitizenController(this);

        // Create query panel
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.add(new JLabel("Origin stop ID:"));
        originField = new JTextField(6);
        queryPanel.add(originField);
        queryPanel.add(new JLabel("Destination stop ID:"));
        destinationField = new JTextField(6);
        queryPanel.add(destinationField);
        JButton queryButton = new JButton("Query");
        queryPanel.add(queryButton);
        // Button to cancel queries
        JButton cancelButton = new JButton("Cancel");
        queryPanel.add(cancelButton);

        // Window configuration
        setLayout(new BorderLayout());
        
        add(queryPanel, BorderLayout.NORTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Button listeners
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performQuery();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCancellation();
            }
        });
    }

    /**
     * Perform a travel time query.
     */
    private void performQuery() {
        try {
            long origin = Long.parseLong(originField.getText());
            long destination = Long.parseLong(destinationField.getText());
            controller.query(origin, destination);
        } catch (NumberFormatException ex) {
            showCitizenInformation("Please enter numeric stop identifiers.");
        }
    }

    /**
     * Perform a query cancellation.
     */
    private void performCancellation() {
        try {
            long origin = Long.parseLong(originField.getText());
            long destination = Long.parseLong(destinationField.getText());
            String key = origin + "-" + destination;
            // Cancel functionality removed in ICE version
            showCitizenInformation("Cancel functionality not available in ICE version");
        } catch (NumberFormatException ex) {
            showCitizenInformation("Please enter numeric stop identifiers to cancel.");
        }
    }

    /**
     * Show information to the citizen.
     * @param message The message to display
     */
    public void showCitizenInformation(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Main entry point for the Citizen application.
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CitizenUI();
            }
        });
    }
}
