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
    
    public TaskDelegator() {
        this.executorService = Executors.newFixedThreadPool(4);
        this.pendingTasks = new ConcurrentHashMap<>();
        this.taskIdCounter = new AtomicInteger(0);
    }
    
    @Override
    public String delegateTask(String taskType, Object data) {
        String taskId = "TASK-" + taskIdCounter.incrementAndGet();
        
        Future<?> future = executorService.submit(() -> {
            try {
                System.out.println("Executing task: " + taskId + " (type: " + taskType + ")");
                // TODO: Execute actual task logic based on taskType
                Thread.sleep(100); // Simulate work
                System.out.println("Task completed: " + taskId);
            } catch (Exception e) {
                System.err.println("Task failed: " + taskId + " - " + e.getMessage());
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
