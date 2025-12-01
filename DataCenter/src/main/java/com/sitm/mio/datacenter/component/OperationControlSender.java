package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.IOperationControlSender;

/**
 * Sends responses to OperationControl module.
 * Component from deployment diagram: SenderControladorOp
 * 
 * Realizes: IOperationControlSender
 */
public class OperationControlSender implements IOperationControlSender {

    private long authResponsesSent = 0;
    private long queryResponsesSent = 0;
    private long alertsSent = 0;
    
    @Override
    public void sendAuthResponse(Object response) {
        authResponsesSent++;
        log("AUTH_RESPONSE", response);
        // Futuro: enviar al módulo de Control de Operación vía ICE/HTTP
    }
    
    @Override
    public void sendQueryResponse(Object response) {
        queryResponsesSent++;
        log("QUERY_RESPONSE", response);
    }
    
    @Override
    public void sendAlert(Object alert) {
        alertsSent++;
        log("ALERT", alert);
    }

    private void log(String type, Object payload) {
        System.out.println("[OperationControlSender] " + type +
                " | counts(auth=" + authResponsesSent +
                ", query=" + queryResponsesSent +
                ", alerts=" + alertsSent + ")" +
                " | payload=" + (payload != null ? payload : "null"));
    }
}
