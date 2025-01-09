# flightgear-control Operation

Ensure the simulator environment setup is completed. Process documented [here](SETUP_LINUX.md).

----
### Run a test flight ###

1. Run a simulator instance via the provided shell scripts:
    `flightgear-control/scripts/f15c_flight.sh 5220 103 49.19524 -123.18084 f15c_beta`
1. Select the config file that corresponds to the port range that will be used by the simulator. For the above example, the lower bounds of the port range is 5220, and the corresponding config file is `scripts/conf/f15c/f15c_beta_flight.properties`
1. Run the WaypointFlight program of the `flightgear-control` from the project root:
    `/path/to/jdk/bin/java -cp build/libs/flightgear-control-[version]-app.jar org.jason.fgcontrol.aircraft.f15c.app.WaypointFlight scripts/conf/f15c/f15c_beta_flight.properties`
    * The properties file specifies which ports the flightgear-control application will use to communicate with the flightgear simulator instance. 
    * The properties file can optionally specify a flight plan- Use directive `flightPlanName` to specify the flight plan by name. Names are resolved against `org.jason.fgcontrol.flight.position.KnownRoutes.KNOWN_ROUTES`.
    * For flight applications, ensure that the simulator launches with an initial heading/bearing of the first waypoint in the flightplan from the starting position. Failure to ensure this can cause the aircraft to tumble in an unrecoverable manner.
    * Additionally for flight applications, the launch script will pause the simulator state after its startup is completed. It is the responsibility of the `flightgear-control` application to manage the initial paused simulator state as part of its plan. For a simple test, it can be unpaused by pressing 'P'. Runway applications do not start with a paused simulator.
    * The simulator launch scripts will check for the environment variable DISPLAY, and will invoke the simulator with this display if defined. 

### Operating Supported Aircrafts ###
* [C172P](c172p.md)
* [F15C](f15c.md)
* [F35-B 2](f35b2.md)
* [Alouette 3](alouette3.md)

### Sample and Proof-of-Concept Applications ###
        
##### Aircraft #####
Aircraft flight and runway operations:
* org.jason.fgcontrol.aircraft.f15c.app.WaypointFlight
    Fly an F-15C through a set of waypoints.
* org.jason.fgcontrol.aircraft.f15c.app.RunwayBurnout
    Fuel up an F-15C on a runway, start the engine, apply the parking brake, await exhaustion of fuel.
* org.jason.fgcontrol.aircraft.c172p.app.WaypointFlight
    Fly an Cessna 172P through a set of waypoints.
* org.jason.fgcontrol.aircraft.c172p.app.RunwayBurnout
    Fuel up an Cessna 172P on a runway, start the engine, apply the parking brake, await exhaustion of fuel.
* org.jason.fgcontrol.aircraft.f35b2.app.SustainedFlight
    Fly an F-35-B 2 on a specified bearing.
The `*Flight` driver programs will terminate the simulator instance once its flight plan finishes execution.

-----
##### Telemetry I/O #####

Read the telemetry output and write control input:
* org.jason.fgcontrol.connection.sockets.app.ControlInputWrite
* org.jason.fgcontrol.connection.sockets.app.TelemetryRead
* org.jason.fgcontrol.connection.sockets.app.TelemetryReadControlInputWrite
* org.jason.fgcontrol.connection.telnet.app.TelnetTelmetryLoop
* org.jason.fgcontrol.connection.telnet.app.TelnetTelmetryTest

-----
##### Camera Streaming #####

Read from the camera stream:
* org.jason.fgcontrol.view.app.ReadCameraViewerOnce
* org.jason.fgcontrol.view.app.ReadCameraViewerMultiple
* org.jason.fgcontrol.view.mjpeg.app.MJPEGStreamerApp

-----
##### SSHD #####

Operate an embedded SSH Server:
* org.jason.fgcontrol.sshd.app.SSHServerApp

