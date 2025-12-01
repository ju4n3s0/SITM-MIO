package com.sitm.mio.observer.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sitm.mio.observer.interfaces.IAnalyticsQuery;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API Server that exposes analytics results to external consumers.
 * Component from deployment diagram: Servidor API de Análisis
 * 
 * Realizes: IAnalyticsQuery
 * 
 * This server allows OperationControl and other modules to query
 * the processed analytics results from the Observer module.
 * 
 * Exposed Endpoints:
 * - GET /analytics/system - System-wide analytics
 * - GET /analytics/zone/{zoneId} - Zone-specific analytics
 * - GET /analytics/zone/{zoneId}/arc/{arcId} - Arc-level analytics
 * - GET /analytics/trends - Traffic trends and patterns
 * - GET /analytics/performance - System performance metrics
 * - GET /analytics/health - Health check
 * 
 * Port: 8081 (separate from ProxyCache on 8080)
 * 
 * Connection Pattern:
 * OperationControl → (HTTP GET) → Observer.AnalyticsAPIServer → AnalyticsUpdater
 */
public class AnalyticsAPIServer implements IAnalyticsQuery {
    
    private final AnalyticsUpdater analyticsUpdater;
    private final int port;
    private final ObjectMapper objectMapper;
    private HttpServer server;
    private boolean running;
    
    public AnalyticsAPIServer(AnalyticsUpdater analyticsUpdater, int port) {
        this.analyticsUpdater = analyticsUpdater;
        this.port = port;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.running = false;
    }
    
    /**
     * Start the REST API server.
     */
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Register endpoints
            server.createContext("/analytics/system", this::handleSystemAnalytics);
            server.createContext("/analytics/zone/", this::handleZoneAnalytics);
            server.createContext("/analytics/trends", this::handleTrends);
            server.createContext("/analytics/performance", this::handlePerformance);
            server.createContext("/analytics/health", this::handleHealth);
            
            server.setExecutor(null); // Use default executor
            server.start();
            
            this.running = true;
            System.out.println("Analytics API Server started on port " + port);
            System.out.println("Endpoints available:");
            System.out.println("   - GET http://localhost:" + port + "/analytics/system");
            System.out.println("   - GET http://localhost:" + port + "/analytics/zone/{zoneId}");
            System.out.println("   - GET http://localhost:" + port + "/analytics/trends");
            System.out.println("   - GET http://localhost:" + port + "/analytics/performance");
            System.out.println("   - GET http://localhost:" + port + "/analytics/health");
            
        } catch (IOException e) {
            System.err.println("Failed to start Analytics API Server: " + e.getMessage());
            this.running = false;
        }
    }
    
    /**
     * Stop the REST API server.
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
        this.running = false;
        System.out.println("Analytics API Server stopped");
    }
    
    @Override
    public Object getAnalyticsSummary() {
        return analyticsUpdater.getAnalyticsSummary();
    }
    
    @Override
    public Object getZoneAnalytics(String zoneId) {
        return analyticsUpdater.getZoneAnalytics(zoneId);
    }
    
    @Override
    public Object getHistoricalAnalytics(String timeRange) {
        return analyticsUpdater.getHistoricalAnalytics(timeRange);
    }
    
    // HTTP Handler methods
    
    private void handleSystemAnalytics(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, createError("Method not allowed"));
            return;
        }
        
        try {
            Object analytics = getAnalyticsSummary();
            sendResponse(exchange, 200, analytics);
        } catch (Exception e) {
            sendResponse(exchange, 500, createError("Internal server error: " + e.getMessage()));
        }
    }
    
    private void handleZoneAnalytics(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, createError("Method not allowed"));
            return;
        }
        
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            
            if (parts.length < 4) {
                sendResponse(exchange, 400, createError("Zone ID required"));
                return;
            }
            
            String zoneId = parts[3];
            Object analytics = getZoneAnalytics(zoneId);
            
            if (analytics == null) {
                sendResponse(exchange, 404, createError("Zone not found: " + zoneId));
            } else {
                sendResponse(exchange, 200, analytics);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, createError("Internal server error: " + e.getMessage()));
        }
    }
    
    private void handleTrends(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, createError("Method not allowed"));
            return;
        }
        
        try {
            String query = exchange.getRequestURI().getQuery();
            String timeRange = "24h"; // default
            
            if (query != null && query.startsWith("timeRange=")) {
                timeRange = query.substring(10);
            }
            
            Object trends = getHistoricalAnalytics(timeRange);
            sendResponse(exchange, 200, trends);
        } catch (Exception e) {
            sendResponse(exchange, 500, createError("Internal server error: " + e.getMessage()));
        }
    }
    
    private void handlePerformance(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, createError("Method not allowed"));
            return;
        }
        
        try {
            Object performance = analyticsUpdater.getPerformanceMetrics();
            sendResponse(exchange, 200, performance);
        } catch (Exception e) {
            sendResponse(exchange, 500, createError("Internal server error: " + e.getMessage()));
        }
    }
    
    private void handleHealth(HttpExchange exchange) throws IOException {
        Map<String, Object> health = new HashMap<>();
        health.put("status", running ? "UP" : "DOWN");
        health.put("service", "Observer Analytics API");
        health.put("port", port);
        sendResponse(exchange, 200, health);
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String json = objectMapper.writeValueAsString(data);
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
    
    private Map<String, String> createError(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
    
    // Additional endpoints for Observer-OperationControl integration
    
    /**
     * Get analytics for a specific arc within a zone.
     * Endpoint: GET /analytics/zone/{zoneId}/arc/{arcId}
     * 
     * @param zoneId Zone identifier
     * @param arcId Arc identifier
     * @return Arc analytics data
     */
    public Object getArcAnalytics(String zoneId, Long arcId) {
        Map<String, Object> arcAnalytics = new HashMap<>();
        arcAnalytics.put("zoneId", zoneId);
        arcAnalytics.put("arcId", arcId);
        arcAnalytics.put("message", "Arc-level analytics not yet implemented");
        return arcAnalytics;
    }
    
    /**
     * Check if server is running.
     * 
     * @return true if server is active
     */
    public boolean isRunning() {
        return running;
    }
}
