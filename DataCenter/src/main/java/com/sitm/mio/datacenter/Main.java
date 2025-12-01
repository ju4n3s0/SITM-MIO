package com.sitm.mio.datacenter;

import com.sitm.mio.datacenter.component.ArcZoneResolver;
import com.sitm.mio.datacenter.component.Authenticator;
import com.sitm.mio.datacenter.component.Controller;
import com.sitm.mio.datacenter.component.DataCenterFacade;
import com.sitm.mio.datacenter.component.EventBus;
import com.sitm.mio.datacenter.component.LineRepository;
import com.sitm.mio.datacenter.component.MonitoringConsole;
import com.sitm.mio.datacenter.component.ReceptorDatagramas;
import com.sitm.mio.datacenter.component.ServiceDataCenter;
import com.sitm.mio.datacenter.component.StopRepository;
import com.sitm.mio.datacenter.component.TravelTimeStatsRepository;
import com.sitm.mio.datacenter.interfaces.IArcZoneResolver;
import com.sitm.mio.datacenter.interfaces.IAuthenticator;
import com.sitm.mio.datacenter.interfaces.IController;
import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;
import com.sitm.mio.datacenter.interfaces.IDatagramReceiver;
import com.sitm.mio.datacenter.interfaces.IEventBus;
import com.sitm.mio.datacenter.interfaces.ILineRepository;
import com.sitm.mio.datacenter.interfaces.IMonitoringConsole;
import com.sitm.mio.datacenter.interfaces.IServiceDataCenter;
import com.sitm.mio.datacenter.interfaces.IStopRepository;
import com.sitm.mio.datacenter.interfaces.ITravelTimeStatsRepository;
import com.sitm.mio.datacenter.model.EventNewDatagram;

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

        // 1. Repositorios
        IStopRepository stopRepo = new StopRepository();
        ILineRepository lineRepo = new LineRepository();

        // 2. Servicios núcleo
        IAuthenticator authenticator = new Authenticator();
        IArcZoneResolver arcZoneResolver = new ArcZoneResolver(stopRepo);

        ITravelTimeStatsRepository travelRepo = new TravelTimeStatsRepository();

        IDataCenterFacade facade = new DataCenterFacade(authenticator, stopRepo, lineRepo, travelRepo);

        // 3. Event bus
        IEventBus eventBus = new EventBus();
        eventBus.start();

        // 4. Receptor de datagramas
        IDatagramReceiver datagramReceiver = new ReceptorDatagramas(eventBus);
        datagramReceiver.start();

        // 5. Controller suscrito a eventos
        IController controller = new Controller(eventBus, arcZoneResolver);
        // Suscribimos el controller a eventos EventNewDatagram:
        eventBus.subscribe(
            EventNewDatagram.class,
            (java.util.function.Consumer<Object>) event -> controller.processDatagram(event)
        );

        // 6. Monitoring console
        IMonitoringConsole monitoring = new MonitoringConsole(datagramReceiver, eventBus);
        monitoring.getSystemHealth();

        // 7. Servicio “remoto” (ServiceDataCenter) – por ahora local
        IServiceDataCenter service = new ServiceDataCenter(facade, authenticator, arcZoneResolver);

        System.out.println("DataCenter running (simulado, sin ICE aún)");
    }

}
