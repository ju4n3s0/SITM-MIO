package com.sitm.mio.datacenter;

import com.sitm.mio.datacenter.component.ArcZoneResolver;
import com.sitm.mio.datacenter.component.Authenticator;
import com.sitm.mio.datacenter.component.DataCenterFacade;
import com.sitm.mio.datacenter.component.LineRepository;
import com.sitm.mio.datacenter.component.OperationControlReceiver;
import com.sitm.mio.datacenter.component.ServiceDataCenter;
import com.sitm.mio.datacenter.component.StopRepository;
import com.sitm.mio.datacenter.interfaces.IArcZoneResolver;
import com.sitm.mio.datacenter.interfaces.IAuthenticator;
import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;
import com.sitm.mio.datacenter.interfaces.ILineRepository;
import com.sitm.mio.datacenter.interfaces.IOperationControlReceiver;
import com.sitm.mio.datacenter.interfaces.IServiceDataCenter;
import com.sitm.mio.datacenter.interfaces.IStopRepository;

/**
 * DataCenter Application Entry Point
 * 
 * DataCenter is the core backend system managing:
 * - Real-time bus data reception (UDP datagrams)
 * - Arc and zone resolution from GPS coordinates
 * - Event processing and distribution via EventBus
 * - Data persistence (stops, lines, statistics)
 * - Authentication services
 * - API endpoints for ProxyCache and clients
 * 
 * Key Responsibilities:
 * - Receive bus telemetry data
 * - Process and enrich data with arc/zone information
 * - Store in PostgreSQL database
 * - Publish events to EventBus
 * - Serve data queries via REST API
 * - Authenticate operators and citizens
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("DataCenter starting...");
        
        // TODO: Initialize and wire up components
        // 1. Initialize repositories (StopRepository, LineRepository)
        // 2. Create EventBus
        // 3. Create ReceptorDatagramas (UDP receiver)
        // 4. Create ResolvedorDeArcosyZonas
        // 5. Create Authenticator
        // 6. Create DataCenterFacade
        // 7. Start REST API server
        // 8. Start UDP datagram receiver
        @SuppressWarnings("unused")
        IAuthenticator auth = new Authenticator();
        IStopRepository stopRepo = new StopRepository();
        ILineRepository lineRepo = new LineRepository();
        IArcZoneResolver arcZoneResolver = new ArcZoneResolver(stopRepo);


        IDataCenterFacade facade = new DataCenterFacade(auth, stopRepo, lineRepo);
        IOperationControlReceiver opReceiver = new OperationControlReceiver(facade);
        IServiceDataCenter servicio = new ServiceDataCenter(
            facade,auth,arcZoneResolver);
        
        System.out.println("DataCenter running on port 9000");
    }

}
