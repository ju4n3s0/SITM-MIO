package com.sitm.mio.operationcontrol.interfaces;

/**
 * Interface for delegating calculations to worker strategies.
 * Defines the contract for the master component to delegate to calculation strategies.
 * 
 * Original name from deployment diagram: "Delega calculos"
 * 
 * Used by: WorkerCalculator (master)
 */
public interface ICalculationDelegator {
    
    /**
     * Delegate calculation to appropriate strategy.
     * @param calculationType Type of calculation (average time, average speed, etc.)
     * @param parameters Calculation parameters
     * @return Calculation result or task ID
     */
    Object delegateCalculation(String calculationType, Object parameters);
    
    /**
     * Execute calculation synchronously.
     * @param calculationType Type of calculation
     * @param parameters Calculation parameters
     * @return Calculation result
     */
    Object executeCalculation(String calculationType, Object parameters);
    
    /**
     * Execute calculation asynchronously.
     * @param calculationType Type of calculation
     * @param parameters Calculation parameters
     * @return Task ID for result retrieval
     */
    String executeCalculationAsync(String calculationType, Object parameters);
}
