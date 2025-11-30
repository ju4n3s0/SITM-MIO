package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.IAnalyticsClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * REST client for querying analytics from the Observer module.
 * Component: Cliente de Análisis (AnalyticsClient)
 * 
 * Realizes: IAnalyticsClient
 * 
 * Enables OperationControl to consume analytics processed by Observer.
 * 
 * Connection Flow:
 * OperationControl.AnalyticsClient → (HTTP GET) → Observer.AnalyticsAPIServer
 * 
 * Endpoints (Observer API on port 8081):
 * - GET /analytics/system - System-wide analytics summary
 * - GET /analytics/zone/{zoneId} - Zone-specific analytics
 * - GET /analytics/trends?timeRange={range} - Historical trends
 * - GET /analytics/zone/{zoneId}/arc/{arcId} - Arc-level analytics
 * - GET /analytics/performance - Observer performance metrics
 * 
 * This allows operators to view system-wide analytics computed by Observer,
 * in addition to their zone-specific real-time data.
 */
public class AnalyticsClient implements IAnalyticsClient {
    
    private final String observerBaseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Constructor.
     * @param observerBaseUrl Observer API base URL (e.g., "http://localhost:8081")
     */
    public AnalyticsClient(String observerBaseUrl) {
        this.observerBaseUrl = observerBaseUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get system-wide analytics summary from Observer.
     * Endpoint: GET /analytics/system
     * 
     * @return System analytics object containing:
     *         - Total buses tracked
     *         - Average system speed
     *         - Active zones count
     *         - Total events processed
     */
    @Override
    public Object getSystemAnalytics() {
        try {
            String url = observerBaseUrl + "/analytics/system";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Object.class);
            }
        } catch (Exception e) {
            System.err.println("Failed to get system analytics: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get analytics for a specific zone from Observer.
     * Endpoint: GET /analytics/zone/{zoneId}
     * 
     * @param zoneId Zone identifier
     * @return Zone analytics object containing:
     *         - Average speed in zone
     *         - Bus count in zone
     *         - Traffic density
     *         - Recent events summary
     */
    @Override
    public Object getZoneAnalytics(String zoneId) {
        try {
            String url = observerBaseUrl + "/analytics/zone/" + zoneId;
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Object.class);
            }
        } catch (Exception e) {
            System.err.println("Failed to get zone analytics: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get historical trends from Observer.
     * Endpoint: GET /analytics/trends?timeRange={range}
     * 
     * @param timeRange Time range (e.g., "1h", "24h", "7d")
     * @return Historical analytics containing:
     *         - Speed trends over time
     *         - Traffic pattern changes
     *         - Peak hours analysis
     */
    @Override
    public Object getHistoricalTrends(String timeRange) {
        try {
            String url = observerBaseUrl + "/analytics/trends?timeRange=" + timeRange;
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Object.class);
            }
        } catch (Exception e) {
            System.err.println("Failed to get historical trends: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get arc-level analytics from Observer.
     * Endpoint: GET /analytics/zone/{zoneId}/arc/{arcId}
     * 
     * @param zoneId Zone identifier
     * @param arcId Arc identifier
     * @return Arc analytics containing:
     *         - Average speed on arc
     *         - Travel time on arc
     *         - Bus count on arc
     */
    @Override
    public Object getArcAnalytics(String zoneId, Long arcId) {
        try {
            String url = observerBaseUrl + "/analytics/zone/" + zoneId + "/arc/" + arcId;
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Object.class);
            }
        } catch (Exception e) {
            System.err.println("Failed to get arc analytics: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get Observer system performance metrics.
     * Endpoint: GET /analytics/performance
     * 
     * @return Performance metrics containing:
     *         - Events processed per second
     *         - Analytics update latency
     *         - Data freshness
     *         - Server health status
     */
    @Override
    public Object getObserverPerformance() {
        try {
            String url = observerBaseUrl + "/analytics/performance";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Object.class);
            }
        } catch (Exception e) {
            System.err.println("Failed to get observer performance: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Check if Observer API is reachable.
     * 
     * @return true if Observer API is accessible
     */
    @Override
    public boolean isObserverAvailable() {
        try {
            String url = observerBaseUrl + "/analytics/health";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(3))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
