package com.sitm.mio.operationcontrol.interfaces;

/**
 * Interface for querying DataCenter services.
 * Defines the contract for components that query DataCenter data.
 * 
 * Original name from deployment diagram: IConsultaDataCenter
 * 
 * Realized by: ReportController (ControladorDeReportes)
 * Used by: Controller
 */
public interface IDataCenterQuery {
    
    /**
     * Query historical zone data from DataCenter.
     * @param zoneId Zone identifier
     * @param timeRange Time range for query
     * @return Historical data
     */
    Object queryHistoricalZoneData(String zoneId, String timeRange);
    
    /**
     * Query bus trajectory history.
     * @param busId Bus identifier
     * @param timeRange Time range for query
     * @return Trajectory data
     */
    Object queryBusTrajectory(Integer busId, String timeRange);
    
    /**
     * Query system-wide metrics.
     * @param metricType Type of metric to query
     * @param timeRange Time range for query
     * @return Metric data
     */
    Object querySystemMetrics(String metricType, String timeRange);
    
    /**
     * Query arc statistics.
     * @param arcId Arc identifier
     * @param timeRange Time range for query
     * @return Arc statistics
     */
    Object queryArcStatistics(Long arcId, String timeRange);
}
