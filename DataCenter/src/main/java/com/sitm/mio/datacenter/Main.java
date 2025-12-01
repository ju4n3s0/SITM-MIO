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

        // 5. Controller suscrito a eventos (with Facade for data access)
        IController controller = new Controller(eventBus, arcZoneResolver, facade);
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

        // 8. ICE Setup
        // Load ICE configuration from ConfigLoader
        com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();
        initData.properties = com.zeroc.Ice.Util.createProperties();
        
        // Load properties from ConfigLoader
        java.util.Properties iceProps = com.sitm.mio.datacenter.config.ConfigLoader.getIceProperties();
        for (String key : iceProps.stringPropertyNames()) {
            initData.properties.setProperty(key, iceProps.getProperty(key));
        }
        
        System.out.println("[ICE] Configuration loaded via ConfigLoader");
        
        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, initData)) {
            
            // Create ICE servants
            com.sitm.mio.datacenter.ice.DataCenterI dataCenterServant = 
                new com.sitm.mio.datacenter.ice.DataCenterI(facade);
            
            com.sitm.mio.datacenter.ice.DataCenterEventPublisherI eventPublisher = 
                new com.sitm.mio.datacenter.ice.DataCenterEventPublisherI();
            
            com.sitm.mio.datacenter.ice.AuthenticatorI authenticatorServant = 
                new com.sitm.mio.datacenter.ice.AuthenticatorI(authenticator);
            
            // Connect controller to ICE publisher
            controller.setIcePublisher(eventPublisher);
            
            // Create object adapter using configuration
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("DataCenterAdapter");
            
            // Add servants to adapter
            adapter.add(dataCenterServant, com.zeroc.Ice.Util.stringToIdentity("DataCenter"));
            adapter.add(eventPublisher, com.zeroc.Ice.Util.stringToIdentity("DataCenterEventPublisher"));
            adapter.add(authenticatorServant, com.zeroc.Ice.Util.stringToIdentity("Authenticator"));
            
            // Activate adapter
            adapter.activate();
            
            System.out.println("=".repeat(60));
            System.out.println("DataCenter ICE Server Started");
            System.out.println("=".repeat(60));
            System.out.println("ICE Port: 10003");
            System.out.println("DataCenter Service: DataCenter:default -p 10003");
            System.out.println("Event Publisher: DataCenterEventPublisher:default -p 10003");
            System.out.println("Authenticator Service: Authenticator:default -p 10003");
            System.out.println("Active subscribers: " + eventPublisher.getSubscriberCount());
            System.out.println("=".repeat(60));
            
            // Wait for shutdown
            communicator.waitForShutdown();
        }
    }

}
