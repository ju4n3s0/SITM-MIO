package com.sitm.mio.operationcontrol;

import com.sitm.mio.operationcontrol.component.*;
import com.sitm.mio.operationcontrol.config.ConfigLoader;
import com.sitm.mio.operationcontrol.model.BusPositionUpdatedEvent;
import com.sitm.mio.operationcontrol.ui.OperationControlUI;

import javax.swing.SwingUtilities;
import java.util.function.Consumer;

/**
 * Main entry point for the Operation Control System.
 * Sistema Controlador de Operación (from deployment diagram).
 * 
 * This system communicates with ProxyCache via REST API:
 * - POST requests for authentication and logout
 * - GET requests for zone statistics queries
 * - WebSocket for real-time event streaming
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("OperationControl System Starting...");
        
        ConfigLoader.printConfiguration();
        
        SwingUtilities.invokeLater(() -> {
            OperationControlUI ui = new OperationControlUI();
            
            String proxyUrl = ConfigLoader.getProxyUrl();
            String wsUrl = ConfigLoader.getWebSocketUrl();
            String observerUrl = ConfigLoader.getObserverUrl();
            
            ProxyClient proxyClient = new ProxyClient(proxyUrl);
            EventReceiver eventReceiver = new EventReceiver(wsUrl);
            TaskDelegator taskDelegator = new TaskDelegator();
            AlertSender alertSender = new AlertSender();
            ReportSender reportSender = new ReportSender();
            AnalyticsClient analyticsClient = new AnalyticsClient(observerUrl);
            
            Controller controller = new Controller(
                proxyClient,
                eventReceiver,
                ui,
                taskDelegator,
                alertSender,
                reportSender,
                analyticsClient
            );
            
            // Link controller to UI
            ui.setController(controller);
            
            // Register event handler
            eventReceiver.onEvent((Consumer<BusPositionUpdatedEvent>) controller::handleBusPositionEvent);
            
            System.out.println("\nOperationControl initialized successfully");
        });
    }
}
