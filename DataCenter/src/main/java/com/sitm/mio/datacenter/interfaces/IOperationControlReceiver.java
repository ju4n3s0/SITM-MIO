package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for receiving requests from OperationControl.
 * Realized by: ReceptorControladorOp
 */
public interface IOperationControlReceiver {
    Object handleAuthRequest(Object credentials);
    Object handleZoneQuery(String zoneId);
    Object handleSystemQuery();
}
