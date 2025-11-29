package com.sitm.mio.operationcontrol.interfaces;

/**
 * Interface for receiving calculation results.
 * Defines the contract for components that receive asynchronous results.
 * 
 * Original name from deployment diagram: IReceptorResultados
 * 
 * Realized by: EventReceiver
 * Used by: Controller, WorkerCalculator
 */
public interface IResultReceiver {
    
    /**
     * Receive calculation result.
     * @param resultId Result identifier
     * @param result Calculation result data
     */
    void receiveResult(String resultId, Object result);
    
    /**
     * Receive error for a calculation.
     * @param resultId Result identifier
     * @param error Error information
     */
    void receiveError(String resultId, String error);
    
    /**
     * Check if ready to receive results.
     * @return true if ready, false otherwise
     */
    boolean isReadyToReceive();
}
