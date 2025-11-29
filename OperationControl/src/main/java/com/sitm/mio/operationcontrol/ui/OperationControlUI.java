package com.sitm.mio.operationcontrol.ui;

import com.sitm.mio.operationcontrol.component.Controller;
import com.sitm.mio.operationcontrol.component.ProxyClient;
import com.sitm.mio.operationcontrol.component.EventReceiver;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX UI for Operation Control System.
 * Component from deployment diagram: UiControladorDeOperaciones
 * 
 * Provides:
 * - Login screen for operator authentication
 * - Dashboard with event log and zone statistics
 * - Real-time bus position updates
 * - Zone query interface
 */
public class OperationControlUI extends Application {
    
    private Controller controller;
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // TODO: Initialize backend components
        // String proxyUrl = System.getProperty("proxy.url", "http://localhost:8080");
        // String wsUrl = System.getProperty("ws.url", "ws://localhost:8080/stream/operator");
        // ProxyClient proxyClient = new ProxyClient(proxyUrl);
        // EventReceiver eventReceiver = new EventReceiver(wsUrl);
        // this.controller = new Controller(proxyClient, eventReceiver);
        
        // TODO: Show login screen
    }
    
    /**
     * Display login screen.
     */
    private void showLoginScreen() {
        // TODO: Implement login UI
    }
    
    /**
     * Handle login button click.
     */
    private void handleLogin() {
        // TODO: Implement login logic
        // Call controller.login(username, password)
    }
    
    /**
     * Display main dashboard after successful login.
     */
    private void showDashboard(Object operator) {
        // TODO: Implement dashboard UI
        // - Top bar: operator info, assigned zones, logout button
        // - Center: event log (TextArea)
        // - Bottom: zone query input and button
    }
    
    /**
     * Handle zone statistics query.
     */
    private void handleZoneQuery(String zoneId) {
        // TODO: Implement zone query
        // Call controller.queryZoneStatistics(zoneId)
    }
    
    /**
     * Handle logout.
     */
    private void handleLogout() {
        // TODO: Implement logout
        // Call controller.logout()
        // Show login screen
    }
    
    @Override
    public void stop() {
        // TODO: Cleanup on application close
        // Call controller.logout() if authenticated
    }
}
