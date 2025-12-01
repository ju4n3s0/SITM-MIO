package com.sitm.mio.citizen;

import javax.swing.SwingUtilities;

import com.sitm.mio.citizen.ui.CitizenUI;

/**
 * Main entry point for the Citizen application.
 * Launches the Swing UI for querying travel time information.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SITM-MIO Citizen Starting...        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        
        // Launch UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create UI (controller is automatically initialized inside)
            CitizenUI ui = new CitizenUI();
            
            // Show UI
            ui.setVisible(true);
            
            System.out.println("Citizen UI initialized");
            System.out.println("Controller connected");
            System.out.println();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   Citizen Ready!                      ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Ready to query travel times from ProxyServer");
            System.out.println("Enter origin and destination stop IDs in the UI");
        });
    }
}
