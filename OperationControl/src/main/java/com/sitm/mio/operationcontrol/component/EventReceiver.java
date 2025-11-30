package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.IEventReceiver;
import com.sitm.mio.operationcontrol.model.BusPositionUpdatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * WebSocket client for receiving real-time events from Observer.
 * Connects to Observer module via WebSocket for event streaming.
 * Component from deployment diagram: RecepcionDeEventos
 * 
 * Realizes: IEventReceiver
 * 
 * Connection: OperationControl.EventReceiver → WebSocket → Observer
 * 
 * WebSocket Flow:
 * 1. Connect with authentication token
 * 2. Send subscription message with assigned zones
 * 3. Receive real-time bus position events from Observer
 * 4. Notify registered event handlers
 */
public class EventReceiver implements IEventReceiver {
    
    private final String wsUrl;
    private final ObjectMapper objectMapper;
    private final List<Consumer<BusPositionUpdatedEvent>> eventHandlers;
    private WebSocket webSocket;
    private boolean connected;
    
    public EventReceiver(String wsUrl) {
        this.wsUrl = wsUrl;
        this.objectMapper = new ObjectMapper();
        this.eventHandlers = new CopyOnWriteArrayList<>();
        this.connected = false;
    }
    
    @Override
    public void connect(String token) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            
            webSocket = client.newWebSocketBuilder()
                .header("Authorization", "Bearer " + token)
                .buildAsync(URI.create(wsUrl), new WebSocket.Listener() {
                    private StringBuilder messageBuilder = new StringBuilder();
                    
                    @Override
                    public void onOpen(WebSocket webSocket) {
                        System.out.println("WebSocket connected to: " + wsUrl);
                        connected = true;
                        webSocket.request(1);
                    }
                    
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        messageBuilder.append(data);
                        
                        if (last) {
                            String message = messageBuilder.toString();
                            messageBuilder.setLength(0);
                            handleMessage(message);
                        }
                        
                        webSocket.request(1);
                        return null;
                    }
                    
                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                        System.out.println("WebSocket closed: " + statusCode + " - " + reason);
                        connected = false;
                        return null;
                    }
                    
                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        System.err.println("WebSocket error: " + error.getMessage());
                        error.printStackTrace();
                        connected = false;
                    }
                })
                .join();
                
        } catch (Exception e) {
            System.err.println("Failed to connect WebSocket: " + e.getMessage());
            e.printStackTrace();
            connected = false;
        }
    }
    
    @Override
    public void subscribeToZones(List<String> zones) {
        if (webSocket != null && connected) {
            try {
                String subscriptionMessage = objectMapper.writeValueAsString(
                    new SubscriptionMessage("subscribe", zones)
                );
                webSocket.sendText(subscriptionMessage, true);
                System.out.println("Subscribed to zones: " + zones);
            } catch (Exception e) {
                System.err.println("Failed to subscribe to zones: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Cannot subscribe: WebSocket not connected");
        }
    }
    
    @Override
    public void onEvent(Object handler) {
        if (handler instanceof Consumer) {
            @SuppressWarnings("unchecked")
            Consumer<BusPositionUpdatedEvent> eventHandler = (Consumer<BusPositionUpdatedEvent>) handler;
            eventHandlers.add(eventHandler);
        }
    }
    
    @Override
    public void disconnect() {
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Client disconnect");
            connected = false;
            System.out.println("WebSocket disconnected");
        }
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Handle incoming WebSocket message.
     * Parse JSON and notify event handlers.
     */
    private void handleMessage(String message) {
        try {
            BusPositionUpdatedEvent event = objectMapper.readValue(message, BusPositionUpdatedEvent.class);
            
            // Notify all registered handlers
            for (Consumer<BusPositionUpdatedEvent> handler : eventHandlers) {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    System.err.println("Error in event handler: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse event message: " + e.getMessage());
        }
    }
    
    /**
     * Internal class for subscription message format.
     */
    private static class SubscriptionMessage {
        public String action;
        public List<String> zones;
        
        public SubscriptionMessage(String action, List<String> zones) {
            this.action = action;
            this.zones = zones;
        }
    }
}
