package com.sitm.mio.observer.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sitm.mio.observer.interfaces.IEventReceiver;
import com.sitm.mio.observer.model.BusPositionUpdatedEvent;
import com.sitm.mio.observer.model.ZoneStatisticsUpdatedEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * WebSocket client for receiving real-time events from ProxyCache.
 * Connects to ProxyCache.SubscriptionManager via WebSocket.
 * 
 * Component from deployment diagram.
 * Realizes: IEventReceiver
 * 
 * WebSocket Flow:
 * 1. Connect to ProxyCache event stream
 * 2. Receive real-time bus position and zone statistics events
 * 3. Notify registered event handlers (AnalyticsUpdater)
 * 
 * Note: Observer subscribes to all zones for comprehensive analytics.
 */
public class EventReceiver implements IEventReceiver {
    
    private final String wsUrl;
    private final ObjectMapper objectMapper;
    private final CopyOnWriteArrayList<Consumer<Object>> eventHandlers;
    private WebSocket webSocket;
    private boolean connected;
    private final StringBuilder messageBuffer;
    
    public EventReceiver(String wsUrl) {
        this.wsUrl = wsUrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.eventHandlers = new CopyOnWriteArrayList<>();
        this.messageBuffer = new StringBuilder();
        this.connected = false;
    }
    
    @Override
    public void connect() {
        try {
            System.out.println("Connecting to WebSocket: " + wsUrl);
            
            HttpClient client = HttpClient.newHttpClient();
            WebSocket.Builder builder = client.newWebSocketBuilder();
            
            webSocket = builder.buildAsync(URI.create(wsUrl), new WebSocket.Listener() {
                @Override
                public void onOpen(WebSocket webSocket) {
                    System.out.println("Observer WebSocket connected");
                    connected = true;
                    webSocket.request(1);
                }
                
                @Override
                public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                    messageBuffer.append(data);
                    
                    if (last) {
                        String message = messageBuffer.toString();
                        messageBuffer.setLength(0);
                        processMessage(message);
                    }
                    
                    webSocket.request(1);
                    return null;
                }
                
                @Override
                public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                    System.out.println("WebSocket closed: " + reason);
                    connected = false;
                    return null;
                }
                
                @Override
                public void onError(WebSocket webSocket, Throwable error) {
                    System.err.println("WebSocket error: " + error.getMessage());
                    connected = false;
                }
            }).join();
            
        } catch (Exception e) {
            System.err.println("Failed to connect to WebSocket: " + e.getMessage());
            connected = false;
        }
    }
    
    @Override
    public void onEvent(Object handler) {
        if (handler instanceof Consumer) {
            eventHandlers.add((Consumer<Object>) handler);
        }
    }
    
    @Override
    public void disconnect() {
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Observer disconnecting");
            connected = false;
        }
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Process incoming WebSocket message.
     * Attempts to parse as BusPositionUpdatedEvent or ZoneStatisticsUpdatedEvent.
     */
    private void processMessage(String message) {
        try {
            // Try to determine event type and parse accordingly
            if (message.contains("\"busId\"")) {
                BusPositionUpdatedEvent event = objectMapper.readValue(message, BusPositionUpdatedEvent.class);
                notifyHandlers(event);
            } else if (message.contains("\"zoneId\"") && message.contains("\"avgSpeed\"")) {
                ZoneStatisticsUpdatedEvent event = objectMapper.readValue(message, ZoneStatisticsUpdatedEvent.class);
                notifyHandlers(event);
            } else {
                System.out.println("Received unknown event type: " + message);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
    
    /**
     * Notify all registered event handlers.
     */
    private void notifyHandlers(Object event) {
        for (Consumer<Object> handler : eventHandlers) {
            try {
                handler.accept(event);
            } catch (Exception e) {
                System.err.println("Error in event handler: " + e.getMessage());
            }
        }
    }
}
