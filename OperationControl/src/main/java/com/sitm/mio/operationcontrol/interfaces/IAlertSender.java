package com.sitm.mio.operationcontrol.interfaces;

/**
 * Interface for sending alerts.
 * Defines the contract for components that send alert notifications.
 * 
 * Original name from deployment diagram: IEnvioAlertas
 * 
 * Realized by: ReportController (ControladorDeReportes)
 * Used by: Controller
 */
public interface IAlertSender {
    
    /**
     * Send an alert for a specific zone.
     * @param zoneId Zone identifier
     * @param alertType Type of alert
     * @param message Alert message
     */
    void sendZoneAlert(String zoneId, String alertType, String message);
    
    /**
     * Send a system-wide alert.
     * @param alertType Type of alert
     * @param message Alert message
     */
    void sendSystemAlert(String alertType, String message);
    
    /**
     * Send an alert for a specific bus.
     * @param busId Bus identifier
     * @param alertType Type of alert
     * @param message Alert message
     */
    void sendBusAlert(String busId, String alertType, String message);
}
