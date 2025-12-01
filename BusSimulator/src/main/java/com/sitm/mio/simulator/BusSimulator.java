package com.sitm.mio.simulator;

import java.sql.SQLException;

/**
 * Bus Simulator - Replays real bus data from datagrams_history database.
 * 
 * Supports two modes:
 * - DATABASE: Insert datagrams into database (historic mode)
 * - UDP: Send datagrams via UDP to DataCenter (real-time mode)
 * 
 * NOT intended for production deployment - development/testing only.
 */
public class BusSimulator {
    
    private final Object sender; // DatagramReplayer or UDPDatagramSender
    private final String mode;
    private final long intervalMs;
    private volatile boolean running = false;
    
    public BusSimulator() throws Exception {
        this.mode = ConfigLoader.getSimulationMode();
        this.intervalMs = ConfigLoader.getIntervalMs();
        
        if ("UDP".equals(mode)) {
            String host = ConfigLoader.getDataCenterHost();
            int port = ConfigLoader.getDataCenterPort();
            this.sender = new UDPDatagramSender(host, port);
            System.out.println("[BusSimulator] UDP MODE - Sending to " + host + ":" + port);
        } else {
            this.sender = new DatagramReplayer();
            System.out.println("[BusSimulator] DATABASE MODE - Inserting to database");
        }
    }
    
    /**
     * Start the simulation.
     */
    public void start() {
        if (running) {
            System.out.println("[BusSimulator] Already running");
            return;
        }
        
        running = true;
        System.out.println("=".repeat(60));
        System.out.println("Bus Simulator Started (" + mode + " mode)");
        System.out.println("=".repeat(60));
        System.out.println("Interval: " + intervalMs + " ms");
        System.out.println("Database: " + ConfigLoader.getDatabaseUrl());
        System.out.println("=".repeat(60));
        System.out.println();
        
        Thread simulationThread = new Thread(this::simulationLoop, "SimulationThread");
        simulationThread.setDaemon(false);
        simulationThread.start();
    }
    
    /**
     * Main simulation loop - replays real datagrams from database.
     */
    private void simulationLoop() {
        int iteration = 0;
        
        while (running) {
            try {
                iteration++;
                System.out.println("\n[Iteration #" + iteration + "] " + java.time.Instant.now());
                
                // Send/replay next batch of datagrams
                int count;
                if (sender instanceof UDPDatagramSender) {
                    count = ((UDPDatagramSender) sender).sendNextBatch();
                    if (count == 0) {
                        System.out.println("  → No more datagrams, restarting...");
                        ((UDPDatagramSender) sender).reset();
                        count = ((UDPDatagramSender) sender).sendNextBatch();
                    }
                } else {
                    count = ((DatagramReplayer) sender).replayNextBatch();
                    if (count == 0) {
                        System.out.println("  → No more datagrams, restarting...");
                        ((DatagramReplayer) sender).reset();
                        count = ((DatagramReplayer) sender).replayNextBatch();
                    }
                }
                
                System.out.println("  → Processed " + count + " datagrams");
                
                // Wait for next iteration
                Thread.sleep(intervalMs);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (SQLException e) {
                System.err.println("[BusSimulator] Database error: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("[BusSimulator] Unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("\n[BusSimulator] Simulation stopped");
    }
    
    /**
     * Stop the simulation.
     */
    public void stop() {
        running = false;
        if (sender instanceof UDPDatagramSender) {
            ((UDPDatagramSender) sender).close();
        } else {
            ((DatagramReplayer) sender).close();
        }
    }
    
    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║         SITM-MIO Bus Telemetry Simulator              ║");
        System.out.println("║         Real-time Data Generation for Testing         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            BusSimulator simulator = new BusSimulator();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n[BusSimulator] Shutting down...");
                simulator.stop();
            }));
            
            // Start simulation
            simulator.start();
            
            // Keep main thread alive
            Thread.currentThread().join();
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            System.err.println("   Check config.properties for correct database settings");
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("[BusSimulator] Interrupted");
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
