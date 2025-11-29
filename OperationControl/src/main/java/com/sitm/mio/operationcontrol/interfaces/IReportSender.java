package com.sitm.mio.operationcontrol.interfaces;

/**
 * Interface for sending reports.
 * Defines the contract for components that generate and send reports.
 * 
 * Original name from deployment diagram: IEnvioReportes
 * 
 * Realized by: ReportController (ControladorDeReportes)
 * Used by: Controller
 */
public interface IReportSender {
    
    /**
     * Generate and send a zone performance report.
     * @param zoneId Zone identifier
     * @param timeRange Time range for the report
     * @return Report ID
     */
    String sendZonePerformanceReport(String zoneId, String timeRange);
    
    /**
     * Generate and send a system-wide report.
     * @param reportType Type of report
     * @param timeRange Time range for the report
     * @return Report ID
     */
    String sendSystemReport(String reportType, String timeRange);
    
    /**
     * Generate and send an operator activity report.
     * @param operatorId Operator identifier
     * @param timeRange Time range for the report
     * @return Report ID
     */
    String sendOperatorReport(Long operatorId, String timeRange);
    
    /**
     * Check report generation status.
     * @param reportId Report identifier
     * @return Status of the report
     */
    String getReportStatus(String reportId);
}
