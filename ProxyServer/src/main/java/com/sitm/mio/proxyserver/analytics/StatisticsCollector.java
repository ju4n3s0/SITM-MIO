package com.sitm.mio.proxyserver.analytics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collects and tracks statistics for ProxyServer operations.
 * Thread-safe implementation using atomic operations.
 */
public class StatisticsCollector {
    
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    
    /**
     * Record a request with cache hit.
     * @param responseTimeMs Response time in milliseconds
     */
    public void recordCacheHit(long responseTimeMs) {
        totalRequests.incrementAndGet();
        cacheHits.incrementAndGet();
        totalResponseTime.addAndGet(responseTimeMs);
    }
    
    /**
     * Record a request with cache miss.
     * @param responseTimeMs Response time in milliseconds
     */
    public void recordCacheMiss(long responseTimeMs) {
        totalRequests.incrementAndGet();
        cacheMisses.incrementAndGet();
        totalResponseTime.addAndGet(responseTimeMs);
    }
    
    /**
     * Increment active connection count.
     */
    public void incrementActiveConnections() {
        activeConnections.incrementAndGet();
    }
    
    /**
     * Decrement active connection count.
     */
    public void decrementActiveConnections() {
        activeConnections.decrementAndGet();
    }
    
    /**
     * Get total number of requests processed.
     */
    public int getTotalRequests() {
        return totalRequests.get();
    }
    
    /**
     * Get number of cache hits.
     */
    public int getCacheHits() {
        return cacheHits.get();
    }
    
    /**
     * Get number of cache misses.
     */
    public int getCacheMisses() {
        return cacheMisses.get();
    }
    
    /**
     * Calculate cache hit rate as percentage.
     */
    public double getCacheHitRate() {
        int total = totalRequests.get();
        if (total == 0) {
            return 0.0;
        }
        return (cacheHits.get() * 100.0) / total;
    }
    
    /**
     * Calculate average response time in milliseconds.
     */
    public double getAverageResponseTime() {
        int total = totalRequests.get();
        if (total == 0) {
            return 0.0;
        }
        return totalResponseTime.get() / (double) total;
    }
    
    /**
     * Get current number of active connections.
     */
    public int getActiveConnections() {
        return activeConnections.get();
    }
    
    /**
     * Reset all statistics.
     */
    public void reset() {
        totalRequests.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
        totalResponseTime.set(0);
        activeConnections.set(0);
    }
    
    @Override
    public String toString() {
        return "StatisticsCollector{" +
                "totalRequests=" + getTotalRequests() +
                ", cacheHits=" + getCacheHits() +
                ", cacheMisses=" + getCacheMisses() +
                ", cacheHitRate=" + String.format("%.2f%%", getCacheHitRate()) +
                ", avgResponseTime=" + String.format("%.2fms", getAverageResponseTime()) +
                ", activeConnections=" + getActiveConnections() +
                '}';
    }
}
