package com.sitm.mio.proxyserver.service;


public interface EventSubscriber {
   
    void handleEvent(Object event);
}