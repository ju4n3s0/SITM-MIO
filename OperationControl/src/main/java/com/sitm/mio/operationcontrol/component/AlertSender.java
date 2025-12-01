package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.IAlertSender;

/**
 * Alert sending component for notifications.
 * Component from deployment diagram: EnvioAlertas
 * 
 * Realizes: IAlertSender
 * 
 * Sends alerts and notifications to relevant parties.
 */
public class AlertSender implements IAlertSender {
    
    @Override
    public void sendZoneAlert(String zoneId, String alertType, String message) {
        // TODO: Implement actual alert sending
        System.out.println("ZONE ALERT [" + zoneId + "] " + alertType + ": " + message);
    }
    
    @Override
    public void sendSystemAlert(String alertType, String message) {
        // TODO: Implement system-wide alert
        System.out.println("SYSTEM ALERT [" + alertType + "]: " + message);
    }
    
    @Override
    public void sendBusAlert(String busId, String alertType, String message) {
        // TODO: Implement bus-specific alert
        System.out.println("BUS ALERT [" + busId + "] " + alertType + ": " + message);
    }
}
