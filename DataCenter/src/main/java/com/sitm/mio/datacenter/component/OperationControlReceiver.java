package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;
import com.sitm.mio.datacenter.interfaces.IOperationControlReceiver;
import com.sitm.mio.datacenter.model.OperatorCredentials;

/**
 * Receives requests from OperationControl module.
 * Component from deployment diagram: ReceptorControladorOp
 * 
 * Realizes: IOperationControlReceiver
 * Uses: IDataCenterFacade
 */
public class OperationControlReceiver implements IOperationControlReceiver {
    
    private final IDataCenterFacade facade;
    
    public OperationControlReceiver(IDataCenterFacade facade) {
        this.facade = facade;
    }
    
    @Override
    public Object handleAuthRequest(Object credentials) {
        System.out.println("Handling auth request from OperationControl");

        if (!(credentials instanceof OperatorCredentials)) {
            throw new IllegalArgumentException("Expected OperatorCredentials as auth credentials");
        }
        OperatorCredentials creds = (OperatorCredentials) credentials;

        //use facade to do the auth
        Object result = facade.authenticateOperator(creds.getUsername(), creds.getPassword());

        System.out.println("[OperationControlerReceiver] Auth result: " + result);
        return result;
    }
    
    @Override
    public Object handleZoneQuery(String zoneId) {
        System.out.println("[OperationControlReceiver] Handing zone query for zone");
        return facade.getZoneStatistics(zoneId);
    }
    
    @Override
    public Object handleSystemQuery() {
        System.out.println("[OperationControlReceiver] Handing system-wide statistics");
        return facade.getSystemStatistics();
    }
}
