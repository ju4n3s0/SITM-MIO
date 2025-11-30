package proxyserver.service;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SubscriptionManager {

    private final Set<EventSubscriber> subscribers = new CopyOnWriteArraySet<>();

    
    public void subscribe(EventSubscriber subscriber) {
        if (subscriber != null) {
            subscribers.add(subscriber);
        }
    }

    
    public void unsubscribe(EventSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    
    public Set<EventSubscriber> getSubscribers() {
        return subscribers;
    }
}