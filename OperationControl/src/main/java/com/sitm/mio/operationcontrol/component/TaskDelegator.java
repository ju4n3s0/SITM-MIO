package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.ITaskDelegator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task delegation component for distributing calculation tasks.
 * Component from deployment diagram: DelegadorDeTareas
 * 
 * Realizes: ITaskDelegator
 * 
 * Implements Master-Slave pattern for parallel task execution.
 * Delegates calculation tasks to worker threads for improved performance.
 */
public class TaskDelegator implements ITaskDelegator {
    
    private final ExecutorService executorService;
    private final Map<String, Future<?>> pendingTasks;
    private final AtomicInteger taskIdCounter;
    private final CalculateAverageTime avgTimeCalculator;
    private final CalculateAverageSpeed avgSpeedCalculator;
    
    public TaskDelegator(CalculateAverageTime avgTimeCalculator, CalculateAverageSpeed avgSpeedCalculator) {
        this.executorService = Executors.newFixedThreadPool(4);
        this.pendingTasks = new ConcurrentHashMap<>();
        this.taskIdCounter = new AtomicInteger(0);
        this.avgTimeCalculator = avgTimeCalculator;
        this.avgSpeedCalculator = avgSpeedCalculator;
    }
    
    @Override
    public String delegateTask(String taskType, Object data) {
        String taskId = "TASK-" + taskIdCounter.incrementAndGet();
        
        Future<?> future = executorService.submit(() -> {
            try {
                System.out.println("[TaskDelegator] Executing task: " + taskId + " (type: " + taskType + ")");
                
                if (taskType.equals("CALCULATE_AVERAGE_TIME")) {
                    // Data should be Map with originStopId, destinationStopId, timeWindow
                    if (data instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> params = (Map<String, Object>) data;
                        long originStopId = ((Number) params.get("originStopId")).longValue();
                        long destinationStopId = ((Number) params.get("destinationStopId")).longValue();
                        int timeWindow = params.containsKey("timeWindow") 
                            ? ((Number) params.get("timeWindow")).intValue() 
                            : 60;
                        
                        if (avgTimeCalculator != null) {
                            avgTimeCalculator.calculateAverageTime(originStopId, destinationStopId, timeWindow);
                        }
                    }
                } else if (taskType.equals("CALCULATE_ARC_SPEEDS")) {
                    // Data should be zoneId string
                    if (data instanceof String) {
                        String zoneId = (String) data;
                        if (avgSpeedCalculator != null) {
                            avgSpeedCalculator.calculateArcSpeedsForZone(zoneId);
                        }
                    }
                } else if (taskType.equals("CALCULATE_ARC_SPEED")) {
                    // Data should be arcId
                    if (data instanceof Long || data instanceof Integer) {
                        Long arcId = ((Number) data).longValue();
                        if (avgSpeedCalculator != null) {
                            avgSpeedCalculator.calculateArcSpeed(arcId);
                        }
                    }
                }
                
                System.out.println("[TaskDelegator] Task completed: " + taskId);
            } catch (Exception e) {
                System.err.println("[TaskDelegator] Task failed: " + taskId + " - " + e.getMessage());
                e.printStackTrace();
            } finally {
                pendingTasks.remove(taskId);
            }
        });
        
        pendingTasks.put(taskId, future);
        return taskId;
    }
    
    @Override
    public boolean hasAvailableWorkers() {
        // Check if executor has capacity for more tasks
        return pendingTasks.size() < 10; // Simple threshold
    }
    
    @Override
    public int getPendingTaskCount() {
        return pendingTasks.size();
    }
    
    /**
     * Shutdown the executor service.
     * Should be called when the application is closing.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
