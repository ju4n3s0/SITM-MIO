package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.IProxyClient;
import com.sitm.mio.operationcontrol.model.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.URI;

import java.time.Duration;

/**
 * REST API client for communicating with ProxyCache.
 * Performs both GET and POST requests to access DataCenter services.
 * Component from deployment diagram: ClienteProxy
 * 
 * Realizes: IProxyClient
 * 
 * API Endpoints:
 * - POST /api/auth/login - Authenticate operator
 * - POST /api/auth/logout - Logout operator
 * - GET /api/zone-statistics?zoneId={id} - Query zone statistics
 */
public class ProxyClient implements IProxyClient {
    
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;
    
    public ProxyClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * Authenticate operator via POST request.
     * @param credentials Operator username and password
     * @return Authenticated operator data with token and assigned zones
     */
    @Override
    public AuthenticatedOperatorData authenticate(OperatorCredentials credentials) {
        try {
            String jsonBody = objectMapper.writeValueAsString(credentials);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(10))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                AuthenticatedOperatorData operatorData = objectMapper.readValue(
                    response.body(), 
                    AuthenticatedOperatorData.class
                );
                this.authToken = operatorData.getToken();
                return operatorData;
            } else {
                System.err.println("Authentication failed: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Query zone statistics via GET request.
     * @param zoneId Zone identifier
     * @return Zone statistics including average speed and arc data
     */
    @Override
    public ZoneStatisticsResponse getZoneStatistics(String zoneId) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/zone-statistics?zoneId=" + zoneId))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.ofSeconds(10));
            
            // Add auth token if available
            if (authToken != null) {
                requestBuilder.header("Authorization", "Bearer " + authToken);
            }
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), ZoneStatisticsResponse.class);
            } else {
                System.err.println("Failed to get zone statistics: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error querying zone statistics: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Logout operator via POST request.
     */
    @Override
    public void logout() {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/logout"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(10));
            
            // Add auth token if available
            if (authToken != null) {
                requestBuilder.header("Authorization", "Bearer " + authToken);
            }
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                this.authToken = null;
                System.out.println("Logout successful");
            } else {
                System.err.println("Logout failed: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
