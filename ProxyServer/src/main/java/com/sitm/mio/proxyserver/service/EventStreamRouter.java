package com.sitm.mio.proxyserver.service;

import java.util.Objects;


public class EventStreamRouter {

    private final SubscriptionManager subscriptionManager;

    public EventStreamRouter(SubscriptionManager subscriptionManager) {
        this.subscriptionManager = Objects.requireNonNull(subscriptionManager);
    }

    public void publishEvent(Object event) {
        for (EventSubscriber subscriber : subscriptionManager.getSubscribers()) {
            try {
                subscriber.handleEvent(event);
            } catch (Exception e) {
                
            }
        }
    }
}