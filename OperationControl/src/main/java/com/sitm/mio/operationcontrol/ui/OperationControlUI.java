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
    private JTextArea eventLogArea;
    private JTextField zoneQueryField;
    private JButton queryButton;
    private JButton logoutButton;
    private JLabel operatorInfoLabel;
    private JLabel assignedZonesLabel;
    private JLabel connectionStatusLabel;
    private JPanel statsPanel;
    private com.sitm.mio.operationcontrol.component.ProxyClient proxyClient;
    
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
        assignedZonesLabel = new JLabel("Assigned Zones: ");
        connectionStatusLabel = new JLabel("Status: Disconnected");
        connectionStatusLabel.setForeground(Color.RED);
        
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
        
        // Center panel - Event log
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Event Log"));
        
        eventLogArea = new JTextArea();
        eventLogArea.setEditable(false);
        eventLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(eventLogArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - Zone query and statistics
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Query panel
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.setBorder(BorderFactory.createTitledBorder("Zone Query"));
        queryPanel.add(new JLabel("Zone ID:"));
        zoneQueryField = new JTextField(15);
        queryPanel.add(zoneQueryField);
        queryButton = new JButton("Query Statistics");
        queryButton.addActionListener(e -> handleZoneQuery());
        queryPanel.add(queryButton);
        
        zoneQueryField.addActionListener(e -> handleZoneQuery());
        
        bottomPanel.add(queryPanel, BorderLayout.NORTH);
        
        // Statistics panel
        statsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Zone Statistics"));
        statsPanel.setPreferredSize(new Dimension(600, 150));
        JScrollPane statsScrollPane = new JScrollPane(statsPanel);
        bottomPanel.add(statsScrollPane, BorderLayout.CENTER);
        
        dashboardPanel.add(bottomPanel, BorderLayout.SOUTH);
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
                    if (operator != null) {
                        showDashboard(operator);
                    } else {
                        statusLabel.setText("Invalid credentials");
                        statusLabel.setForeground(Color.RED);
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Login failed: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    loginButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private void showDashboard(AuthenticatedOperatorData operator) {
        operatorInfoLabel.setText("Operator: " + operator.getFullName() + " (" + operator.getUsername() + ")");
        assignedZonesLabel.setText("Assigned Zones: " + String.join(", ", operator.getAssignedZones()));
        
        // Clear login fields
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        loginButton.setEnabled(true);
        
        // Switch to dashboard
        setContentPane(dashboardPanel);
        revalidate();
        repaint();
        
        logToEventArea("Logged in successfully as " + operator.getFullName());
    }
    
    private void handleZoneQuery() {
        String zoneId = zoneQueryField.getText().trim();
        if (zoneId.isEmpty()) {
            displayAlert("Please enter a zone ID");
            return;
        }
        
        queryButton.setEnabled(false);
        
        SwingWorker<ZoneStatisticsResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected ZoneStatisticsResponse doInBackground() {
                if (controller != null) {
                    return controller.queryZoneStatistics(zoneId);
                }
                return null;
            }
            
            @Override
            protected void done() {
                queryButton.setEnabled(true);
                try {
                    ZoneStatisticsResponse stats = get();
                    if (stats != null) {
                        logToEventArea("Zone statistics retrieved for: " + zoneId);
                    }
                } catch (Exception e) {
                    displayAlert("Query failed: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller != null) {
                controller.logout();
            }
            
            // Clear event log
            eventLogArea.setText("");
            statsPanel.removeAll();
            statsPanel.revalidate();
            statsPanel.repaint();
            
            // Switch back to login
            setContentPane(loginPanel);
            revalidate();
            repaint();
            
            updateConnectionStatus(false);
        }
    }
    
    private void logToEventArea(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
            eventLogArea.append("[" + timestamp + "] " + message + "\n");
            eventLogArea.setCaretPosition(eventLogArea.getDocument().getLength());
        });
    }
    
    // ==================== IVisualizacion Implementation ====================
    
    @Override
    public void displayBusPosition(BusPositionUpdatedEvent event) {
        SwingUtilities.invokeLater(() -> {
            String message = String.format("Bus %s - Zone: %s, Speed: %.1f km/h, Location: (%.6f, %.6f)",
                event.getBusId(),
                event.getZoneId(),
                event.getSpeed(),
                event.getLatitude(),
                event.getLongitude()
            );
            logToEventArea(message);
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
            assignedZonesLabel.setText("Assigned Zones: " + String.join(", ", zones));
        });
    }
    
    @Override
    public void displayAlert(String message) {
        SwingUtilities.invokeLater(() -> {
            logToEventArea("ALERT: " + message);
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
            logToEventArea("Historical trends data received: " + trends);
        });
    }
}
