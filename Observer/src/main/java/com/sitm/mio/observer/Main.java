package com.sitm.mio.observer;

import com.sitm.mio.observer.component.AnalyticsAPIServer;
import com.sitm.mio.observer.component.AnalyticsUpdater;
import com.sitm.mio.observer.component.EventReceiver;
import com.sitm.mio.observer.config.ConfigLoader;

/**
 * Main entry point for the Observer System.
 * Real-time analytics processor for SITM-MIO bus data.
 * 
 * This system communicates with ProxyCache via:
 * - WebSocket for real-time event streaming
 * - Optional REST API for queries (GET requests)
 * 
 * Component from deployment diagram: Observer Node
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  SITM-MIO Observer System Starting");
        System.out.println("  Real-time Analytics Processor");
        System.out.println("========================================\n");
        
        // Load configuration
        ConfigLoader.printConfiguration();
        
        // Get configuration values
        String wsUrl = ConfigLoader.getWebSocketUrl();
        int apiPort = ConfigLoader.getApiServerPort();
        
        System.out.println("Configuration:");
        System.out.println("  WebSocket URL: " + wsUrl);
        System.out.println("  API Server Port: " + apiPort);
        System.out.println();
        
        // Initialize components
        System.out.println("Initializing components...\n");
        
        // 1. Create EventReceiver (WebSocket client)
        EventReceiver eventReceiver = new EventReceiver(wsUrl);
        
        // 2. Create AnalyticsUpdater
        AnalyticsUpdater analyticsUpdater = new AnalyticsUpdater(eventReceiver);
        
        // 3. Create Analytics API Server
        AnalyticsAPIServer apiServer = new AnalyticsAPIServer(analyticsUpdater, apiPort);
        
        // 4. Start analytics processing
        analyticsUpdater.start();
        
        // 5. Start API server
        apiServer.start();
        
        // 6. Connect to ProxyCache WebSocket
        eventReceiver.connect();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\nShutting down Observer...");
            apiServer.stop();
            analyticsUpdater.stop();
            eventReceiver.disconnect();
            System.out.println("Observer shutdown complete");
        }));
        
        System.out.println("\nObserver System initialized successfully");
        System.out.println("Listening for real-time events...");
        System.out.println("API Server ready for queries");
        System.out.println("Press Ctrl+C to stop\n");
        
        // Keep the application running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("Observer interrupted");
        }
    }
}
