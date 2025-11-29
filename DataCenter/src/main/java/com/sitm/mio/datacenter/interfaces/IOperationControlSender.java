package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for sending responses to OperationControl.
 * Realized by: SenderControladorOp
 */
public interface IOperationControlSender {
    void sendAuthResponse(Object response);
    void sendQueryResponse(Object response);
    void sendAlert(Object alert);
}
