package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.IOperationControlReceiver;
import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;

/**
 * Receives requests from OperationControl module.
 * Component from deployment diagram: ReceptorControladorOp
 * 
 * Realizes: IOperationControlReceiver
 * Uses: IDataCenterFacade
 */
public class ReceptorControladorOp implements IOperationControlReceiver {
    
    private final IDataCenterFacade facade;
    
    public ReceptorControladorOp(IDataCenterFacade facade) {
        this.facade = facade;
    }
    
    @Override
    public Object handleAuthRequest(Object credentials) {
        // TODO: Handle authentication request from OperationControl
        System.out.println("Handling auth request from OperationControl");
        return null;
    }
    
    @Override
    public Object handleZoneQuery(String zoneId) {
        return facade.getZoneStatistics(zoneId);
    }
    
    @Override
    public Object handleSystemQuery() {
        return facade.getSystemStatistics();
    }
}
