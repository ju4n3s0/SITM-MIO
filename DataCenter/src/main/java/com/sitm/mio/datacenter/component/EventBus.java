package com.sitm.mio.datacenter.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.sitm.mio.datacenter.interfaces.IEventBus;

/**
 * Central event bus for DataCenter event distribution.
 * Component from deployment diagram: EventBus
 * 
 * Realizes: IEventBus
 */
public class EventBus implements IEventBus {
    
    //eventType --> List of handlers
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    @Override
    public void publish(Object event) {
        if (!running || event == null) {
            return;
        }

        Class<?> eventType = event.getClass();
        List<Consumer<Object>> eventHandlers = handlers.get(eventType);

        if (eventHandlers == null || eventHandlers.isEmpty()) {
            return;
        }

        List<Consumer<Object>> snapshot = new ArrayList<>(eventHandlers);

        for (Consumer<Object> handler : snapshot) {
            try {
                handler.accept(event);
            } catch (Exception e) {
                System.err.println("[EventBus] Error handling event " +
                        eventType.getSimpleName() + ": " + e.getMessage());
                        e.printStackTrace();
            }
        }
        System.out.println("Publishing event: " + event);
    }
    
    @Override
    public void subscribe(Class<?> eventType, Object handler) {

        if (!(handler instanceof Consumer)) {
            throw new IllegalArgumentException("Handler must be a Consumer<Object>, but was: " +
                    (handler != null ? handler.getClass().getName() : "null"));
        }

        @SuppressWarnings("unchecked")
        Consumer<Object> consumer = (Consumer<Object>) handler;

        handlers
            .computeIfAbsent(eventType, k -> new ArrayList<>())
            .add(consumer);
    }
    
    @Override
    public void unsubscribe(Class<?> eventType, Object handler) {
        if (!(handler instanceof Consumer)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Consumer<Object> consumer = (Consumer<Object>) handler;

        List<Consumer<Object>> eventHandlers = handlers.get(eventType);

        if (eventHandlers != null) {
            eventHandlers.remove(consumer);
            if (eventHandlers.isEmpty()) {
                handlers.remove(eventType);
            }
        }
    }
    
    @Override
    public void start() {
        running = true;
        System.out.println("EventBus Started");
    }
    
    @Override
    public void stop() {
        running = false;
        System.out.println("EventBus stopped");
    }
}
