package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.IOperationControlSender;

/**
 * Sends responses to OperationControl module.
 * Component from deployment diagram: SenderControladorOp
 * 
 * Realizes: IOperationControlSender
 */
public class OperationControlSender implements IOperationControlSender {
    
    @Override
    public void sendAuthResponse(Object response) {
        // TODO: Send authentication response
        System.out.println("Sending auth response to OperationControl");
    }
    
    @Override
    public void sendQueryResponse(Object response) {
        // TODO: Send query response
        System.out.println("Sending query response to OperationControl");
    }
    
    @Override
    public void sendAlert(Object alert) {
        // TODO: Send alert
        System.out.println("Sending alert to OperationControl");
    }
}
