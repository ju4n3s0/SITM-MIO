package com.sitm.mio.citizen;

import javax.swing.SwingUtilities;

import com.sitm.mio.citizen.ui.CitizenUI;
import com.sitm.mio.citizen.controller.CitizenController;

/**
 * Main entry point for the Citizen application.
 * Launches the Swing UI for querying travel time information.
 */
public class Main {
    
    public static void main(String[] args) {
        // Launch UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            CitizenUI ui = new CitizenUI();
            ui.setVisible(true);
            
            System.out.println("Citizen application started");
            System.out.println("Ready to query travel times from ProxyServer");
        });
    }
}
