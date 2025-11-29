package com.sitm.mio.operationcontrol;

import com.sitm.mio.operationcontrol.ui.OperationControlUI;

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
        // TODO: Launch JavaFX UI
        // OperationControlUI.launch(OperationControlUI.class, args);
    }
}
