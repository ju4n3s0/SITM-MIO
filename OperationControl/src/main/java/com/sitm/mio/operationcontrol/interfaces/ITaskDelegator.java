package com.sitm.mio.operationcontrol.interfaces;

/**
 * Interface for task delegation pattern (Master-Slave).
 * Defines the contract for components that delegate calculation tasks to workers.
 * 
 * Original name from deployment diagram: IDelegadorTareas
 * 
 * Realized by: WorkerCalculator (master)
 * Used by: CalcularTiempoPromedio, CalcularVelocidadPromedio, ActualizarEstadisticasDeZona (workers/slaves)
 */
public interface ITaskDelegator {
    
    /**
     * Delegate a calculation task to available workers.
     * @param taskType Type of calculation task
     * @param data Input data for the calculation
     * @return Task ID for tracking
     */
    String delegateTask(String taskType, Object data);
    
    /**
     * Check if workers are available for task delegation.
     * @return true if workers available, false otherwise
     */
    boolean hasAvailableWorkers();
    
    /**
     * Get current task queue size.
     * @return Number of pending tasks
     */
    int getPendingTaskCount();
}
