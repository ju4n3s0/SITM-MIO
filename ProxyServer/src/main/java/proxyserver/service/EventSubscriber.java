package proxyserver.service;


public interface EventSubscriber {
   
    void handleEvent(Object event);
}