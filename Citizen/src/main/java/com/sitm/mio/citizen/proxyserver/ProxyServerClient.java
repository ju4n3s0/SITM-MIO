package com.sitm.mio.citizen.proxyserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitm.mio.citizen.dto.GetCitizenInformationRequest;
import com.sitm.mio.citizen.dto.CitizenInformation;
import com.sitm.mio.citizen.config.ConfigLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client for communicating with the remote ProxyServer service.
 * 
 * - HttpClient for HTTP communication
 * - var for type inference (cleaner code)
 * - Enhanced switch expressions for status code handling
 * - Retry logic with exponential backoff
 * - JSON serialization/deserialization (Jackson)
 */
public class ProxyServerClient implements ICacheService {
    
    private final String proxyServerUrl;
    private final int timeout;
    private final int retryAttempts;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ProxyServerClient() {
        // Load from configuration
        this.proxyServerUrl = ConfigLoader.getProxyServerUrl();
        this.timeout = ConfigLoader.getProxyServerTimeout();
        this.retryAttempts = ConfigLoader.getRetryAttempts();
        
        // Initialize HTTP client
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();
        
        this.objectMapper = new ObjectMapper();
        
        System.out.println("ProxyServerClient initialized:");
        System.out.println("  URL: " + proxyServerUrl);
        System.out.println("  Timeout: " + timeout + "s");
        System.out.println("  Retry attempts: " + retryAttempts);
    }
    
    public ProxyServerClient(String proxyServerUrl) {
        this.proxyServerUrl = proxyServerUrl;
        this.timeout = ConfigLoader.getProxyServerTimeout();
        this.retryAttempts = ConfigLoader.getRetryAttempts();
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();
        
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public CitizenInformation getCitizenInformation(GetCitizenInformationRequest request) {
        System.out.println("ProxyServerClient: Querying ProxyServer at " + proxyServerUrl);
        System.out.println("  Origin: " + request.getOriginId());
        System.out.println("  Destination: " + request.getDestinationId());
        
        // Retry logic
        Exception lastException = null;
        for (int attempt = 1; attempt <= retryAttempts; attempt++) {
            try {
                return makeHttpRequest(request);
            } catch (Exception e) {
                lastException = e;
                System.err.println("  Attempt " + attempt + " failed: " + e.getMessage());
                
                if (attempt < retryAttempts) {
                    try {
                        // Exponential backoff: 1s, 2s, 4s...
                        Thread.sleep(1000L * (1L << (attempt - 1)));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return new CitizenInformation("Error: Request interrupted");
                    }
                }
            }
        }
        
        // All retries failed
        System.err.println("ProxyServerClient: All " + retryAttempts + " attempts failed");
        return new CitizenInformation("Error: Unable to connect to ProxyServer - " + 
                                     (lastException != null ? lastException.getMessage() : "Unknown error"));
    }
    
    /**
     * Make the actual HTTP request to ProxyServer.
     */
    private CitizenInformation makeHttpRequest(GetCitizenInformationRequest request) throws Exception {
        // Serialize request to JSON
        var requestJson = objectMapper.writeValueAsString(request);
        
        // Build HTTP request
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(proxyServerUrl + "/query"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(timeout))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();
        
        // Send request and get response
        var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        
        return switch (response.statusCode()) {
            case 200 -> {
                // Parse response JSON
                var info = objectMapper.readValue(response.body(), CitizenInformation.class);
                System.out.println("Response received: " + info.getMessage());
                yield info;
            }
            case 400 -> {
                var errorMsg = "Bad Request: " + response.body();
                System.err.println(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
            case 404 -> {
                var errorMsg = "Not Found: " + response.body();
                System.err.println(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            case 500 -> {
                var errorMsg = "Server Error: " + response.body();
                System.err.println(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            default -> {
                var errorMsg = "HTTP " + response.statusCode() + ": " + response.body();
                System.err.println("Unexpected response: " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
        };
    }
}
