package com.sitm.mio.operationcontrol;

import com.sitm.mio.operationcontrol.component.*;
import com.sitm.mio.operationcontrol.ui.OperationControlUI;

import javax.swing.SwingUtilities;

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
        
        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create UI
            OperationControlUI ui = new OperationControlUI();
            
            // TODO: Initialize backend components
            // String proxyUrl = System.getProperty("proxy.url", "http://localhost:8080");
            // String wsUrl = System.getProperty("ws.url", "ws://localhost:8080/stream/operator");
            
            // ProxyClient proxyClient = new ProxyClient(proxyUrl);
            // EventReceiver eventReceiver = new EventReceiver(wsUrl);
            // ITaskDelegator taskDelegator = new TaskDelegator();
            // IAlertSender alertSender = new AlertSender();
            // IReportSender reportSender = new ReportSender();
            // IAnalyticsClient analyticsClient = new AnalyticsClient();
            
            // Controller controller = new Controller(
            //     proxyClient,
            //     eventReceiver,
            //     ui,  // UI implements IVisualizacion
            //     taskDelegator,
            //     alertSender,
            //     reportSender,
            //     analyticsClient
            // );
            
            // ui.setController(controller);
            
            System.out.println("UI launched successfully");
        });
    }
}
