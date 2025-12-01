package com.sitm.mio.operationcontrol.ui;

import com.sitm.mio.operationcontrol.component.Controller;
import com.sitm.mio.operationcontrol.interfaces.IVisualizacion;
import com.sitm.mio.operationcontrol.model.AuthenticatedOperatorData;
import com.sitm.mio.operationcontrol.model.BusPositionUpdatedEvent;
import com.sitm.mio.operationcontrol.model.ZoneStatisticsResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Swing-based UI for Operation Control System.
 * Component from deployment diagram: UiControladorDeOperaciones
 * 
 * Implements IVisualizacion to receive updates from Controller.
 * 
 * Features:
 * - Login screen for operator authentication
 * - Dashboard with event log and zone statistics
 * - Real-time bus position updates
 * - Zone query interface
 */
public class OperationControlUI extends JFrame implements IVisualizacion {
    
    private Controller controller;
    
    // Login screen components
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    // Dashboard components
    private JPanel dashboardPanel;
    private JLabel realTimeInfoLabel;
    private JButton logoutButton;
    private JLabel operatorInfoLabel;
    private JLabel assignedZonesLabel;
    private JLabel connectionStatusLabel;
    private JPanel statsPanel;
    private com.sitm.mio.operationcontrol.component.ProxyClient proxyClient;
    private com.sitm.mio.operationcontrol.ice.EventSubscriberI eventSubscriber;
    private Runnable onZoneAssignedCallback;
    
    public OperationControlUI() {
        initializeUI();
    }
    
    public OperationControlUI(com.sitm.mio.operationcontrol.component.ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
        initializeUI();
    }
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    private void initializeUI() {
        setTitle("MIO Operation Control System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create login panel
        createLoginPanel();
        
        // Create dashboard panel (hidden initially)
        createDashboardPanel();
        
        // Show login panel first
        setContentPane(loginPanel);
        
        setVisible(true);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("MIO Operation Control");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(titleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        loginPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        loginPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton, gbc);
        
        // Status label
        gbc.gridy = 4;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        loginPanel.add(statusLabel, gbc);
        
        // Enter key listener
        passwordField.addActionListener(e -> handleLogin());
    }
    
    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Operator info and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEtchedBorder());
        topPanel.setBackground(new Color(220, 230, 240));
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        operatorInfoLabel = new JLabel("Operator: ");
        operatorInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        assignedZonesLabel = new JLabel("Assigned Zone: ");
        assignedZonesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        connectionStatusLabel = new JLabel("Status: Disconnected");
        connectionStatusLabel.setForeground(Color.RED);
        connectionStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        infoPanel.add(operatorInfoLabel);
        infoPanel.add(assignedZonesLabel);
        infoPanel.add(connectionStatusLabel);
        
        topPanel.add(infoPanel, BorderLayout.CENTER);
        
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(logoutButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        dashboardPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Real-Time Data Display
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Real-Time Zone Data"));
        
        // Create a label to show real-time information
        realTimeInfoLabel = new JLabel("<html><b>Waiting for real-time data...</b><br>" +
            "Events will appear here automatically.</html>");
        realTimeInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        realTimeInfoLabel.setVerticalAlignment(JLabel.TOP);
        realTimeInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane infoScrollPane = new JScrollPane(realTimeInfoLabel);
        infoScrollPane.setPreferredSize(new Dimension(600, 300));
        centerPanel.add(infoScrollPane, BorderLayout.CENTER);
        
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Note: Bottom statistics panel removed - not implemented
        // Real-time event data is shown in the center panel above
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            return;
        }
        
        loginButton.setEnabled(false);
        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(Color.BLUE);
        
        // Perform login in background thread
        SwingWorker<AuthenticatedOperatorData, Void> worker = new SwingWorker<>() {
            @Override
            protected AuthenticatedOperatorData doInBackground() {
                if (proxyClient != null) {
                    com.sitm.mio.operationcontrol.model.OperatorCredentials credentials = 
                        new com.sitm.mio.operationcontrol.model.OperatorCredentials(username, password);
                    return proxyClient.authenticate(credentials);
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    AuthenticatedOperatorData operator = get();
                    
                    // Validate authentication result
                    if (operator != null && 
                        operator.getOperatorId() != null && 
                        !operator.getOperatorId().isEmpty() &&
                        operator.getToken() != null && 
                        !operator.getToken().isEmpty()) {
                        
                        showDashboard(operator);
                    } else {
                        statusLabel.setText("Invalid credentials");
                        statusLabel.setForeground(Color.RED);
                        loginButton.setEnabled(true);
                        System.out.println("[UI] Login rejected - invalid authentication result");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Login failed: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    loginButton.setEnabled(true);
                    System.err.println("[UI] Login error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void showDashboard(AuthenticatedOperatorData operator) {
        operatorInfoLabel.setText("Operator: " + operator.getFullName() + " (" + operator.getUsername() + ")");
        
        // Display single assigned zone
        String assignedZone = operator.getAssignedZones().isEmpty() ? "None" : operator.getAssignedZones().get(0);
        assignedZonesLabel.setText("Assigned Zone: " + assignedZone);
        
        // Set zone filter for event subscriber
        if (eventSubscriber != null && !assignedZone.equals("None")) {
            eventSubscriber.setAssignedZone(assignedZone);
            System.out.println("[OperationControlUI] Client-side zone filter set to: " + assignedZone);
            
            // Trigger resubscription with zone filtering
            if (onZoneAssignedCallback != null) {
                System.out.println("[OperationControlUI] Triggering server-side resubscription...");
                onZoneAssignedCallback.run();
            }
        }
        
        // Update connection status to Connected
        updateConnectionStatus(true);
        
        // Clear login fields
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        loginButton.setEnabled(true);
        
        // Switch to dashboard
        setContentPane(dashboardPanel);
        revalidate();
        repaint();
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Set status to disconnected first
            updateConnectionStatus(false);
            
            if (controller != null) {
                controller.logout();
            }
            
            // Clear stats panel
            statsPanel.removeAll();
            statsPanel.revalidate();
            statsPanel.repaint();
            
            // Close the UI window
            dispose();
            System.exit(0);
        }
    }
    
    private void logToEventArea(String message) {
        // Event log removed - using direct display instead
        System.out.println("[OperationControl] " + message);
    }
    
    // ==================== IVisualizacion Implementation ====================
    
    @Override
    public void displayBusPosition(BusPositionUpdatedEvent event) {
        SwingUtilities.invokeLater(() -> {
            String message = String.format("Bus %s - Zone: %s, Speed: %.1f km/h",
                event.getBusId(),
                event.getZoneId(),
                event.getSpeed()
            );
            System.out.println("[BusPosition] " + message);
        });
    }
    
    @Override
    public void displayZoneStatistics(ZoneStatisticsResponse statistics) {
        SwingUtilities.invokeLater(() -> {
            statsPanel.removeAll();
            
            JPanel statCard = new JPanel(new GridLayout(4, 2, 10, 5));
            statCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            statCard.setBackground(Color.WHITE);
            
            statCard.add(new JLabel("Zone ID:"));
            statCard.add(new JLabel(statistics.getZoneId()));
            
            statCard.add(new JLabel("Vehicle Count:"));
            statCard.add(new JLabel(String.valueOf(statistics.getVehicleCount())));
            
            statCard.add(new JLabel("Average Speed:"));
            statCard.add(new JLabel(String.format("%.2f km/h", statistics.getAvgSpeed())));
            
            statCard.add(new JLabel("Last Update:"));
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(statistics.getTimestamp()));
            statCard.add(new JLabel(timestamp));
            
            statsPanel.add(statCard);
            statsPanel.revalidate();
            statsPanel.repaint();
        });
    }
    
    @Override
    public void displayAssignedZones(List<String> zones) {
        SwingUtilities.invokeLater(() -> {
            String zone = zones.isEmpty() ? "None" : zones.get(0);
            assignedZonesLabel.setText("Assigned Zone: " + zone);
        });
    }
    
    @Override
    public void displayAlert(String message) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[ALERT] " + message);
            JOptionPane.showMessageDialog(this, message, "Alert", JOptionPane.WARNING_MESSAGE);
        });
    }
    
    @Override
    public void updateConnectionStatus(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            if (connected) {
                connectionStatusLabel.setText("Status: Connected");
                connectionStatusLabel.setForeground(new Color(0, 150, 0));
            } else {
                connectionStatusLabel.setText("Status: Disconnected");
                connectionStatusLabel.setForeground(Color.RED);
            }
        });
    }
    
    @Override
    public void displayTrends(Object trends) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[Trends] Historical trends data received: " + trends);
        });
    }
    
    /**
     * Update real-time information display.
     * Called by EventSubscriberI when new events arrive.
     */
    public void updateRealTimeInfo(String info) {
        System.out.println("[OperationControlUI] updateRealTimeInfo called");
        System.out.println("[OperationControlUI] Info: " + info.substring(0, Math.min(100, info.length())) + "...");
        SwingUtilities.invokeLater(() -> {
            if (realTimeInfoLabel != null) {
                System.out.println("[OperationControlUI] Setting text on realTimeInfoLabel");
                realTimeInfoLabel.setText(info);
                
                // Force the parent container to refresh
                if (realTimeInfoLabel.getParent() != null) {
                    realTimeInfoLabel.getParent().revalidate();
                    realTimeInfoLabel.getParent().repaint();
                }
                
                // Also refresh the main dashboard
                if (dashboardPanel != null) {
                    dashboardPanel.revalidate();
                    dashboardPanel.repaint();
                }
                
                System.out.println("[OperationControlUI] Label updated and UI refreshed");
            } else {
                System.out.println("[OperationControlUI] ERROR: realTimeInfoLabel is null!");
            }
        });
    }
    
    /**
     * Set the event subscriber for zone filtering.
     */
    public void setEventSubscriber(com.sitm.mio.operationcontrol.ice.EventSubscriberI subscriber) {
        this.eventSubscriber = subscriber;
    }
    
    /**
     * Get the assigned zone for the logged-in operator.
     */
    public String getAssignedZone() {
        if (assignedZonesLabel != null) {
            String text = assignedZonesLabel.getText();
            if (text != null && text.startsWith("Assigned Zone: ")) {
                String zone = text.substring("Assigned Zone: ".length()).trim();
                return zone.equals("None") ? null : zone;
            }
        }
        return null;
    }
    
    /**
     * Set callback to be called when zone is assigned after login.
     */
    public void setOnZoneAssignedCallback(Runnable callback) {
        this.onZoneAssignedCallback = callback;
    }
}
