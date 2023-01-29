# flightgear-control Environment Setup 
This document describes the steps to set up your linux system with flightgear for running flights with the flightgear-control and thingworx-flightgear-edge projects. The target flightgear version is 2020.3.17.

## Dependencies ##
1. Linux x86_64 running with a windowing system.
1. Git
1. JDK 8
1. GPU preferably with more than 2Gb VRAM.

## Simulator installation ##
This guide covers usage of flightgear with an AppImage, which is the preferred method of running the flightgear simulator. Alternatively, flightgear may be available through your package manager.

----

##### flightgear initial setup #####

1. Use a web browser to navigate to flightgear's release repository at https://sourceforge.net/projects/flightgear/files/release-2020.3
1. Download the following files and place them in a designated folder.
    * `FlightGear-2020.3.17-x86_64.AppImage`
    * `FlightGear-2020.3.17-data.txz`
    * `FlightGear-2020.3.17-update-data.txz`
1. Extract the data archives to the current directory in the following order. Both archives should extract to an `fgdata` directory.
    1. Extract archive `FlightGear-2020.3.17-data.txz`
    1. Extract archive `FlightGear-2020.3.17-update-data.txz`
1. Ensure the binary file FlightGear-2020.3.17-x86_64.AppImage is executable.
1. Add the directory containing the FlightGear AppImage to the PATH. Set this in the shell config files too.
1. Run the simulator setup shell script with:
    `flightgear-control/scripts/fg_launcher.sh`
    
----
##### flightgear aircraft model installation #####
1. After the launcher UI is visible, click 'Okay' on the welcome screen if it appears. 
1. Navigate to the "Aircraft" section. 
1. Select the "Browse" tab near the top center of the UI. Add the default hanger if there is an option to do so.
1. Search (top right of window) for the following models required by the flightgear-control project, and install them if necessary. If a model doesn't appear in the search results, it's possible a minimum ratings requirement is excluding it from results.
    1. Cessna 172P Skyhawk (1982)
    1. F-15C
    1. Lockheed Martin F-35B Lightning II
    1. Alouette-III
1. Exit the simulator UI. Manage aircraft models installed in the simulator by re-running the `fg_launcher.sh` shell script.

----
##### flightgear-control setup #####
1. Clone the flightgear-control project, switch to the project root, and check out the latest tagged release.
1. Use the gradle wrapper and JDK8 to create the application jar for the project
    `JAVA_HOME=/path/to/jdk8 ./gradlew appjar`

----

##### flightgear Protocol Setup #####

1. Locate the flightgear installation directory on your OS. 
1. Copy the project protocol xml files under `flightgear-control/protocol` directory into the flightgear installation data `Protocol` directory without preserving the source directory structure. All xml files should end up in this directory without the source directory structure. For example:
    `cp -v ~/flightgear-control/protocol/input/c172p/c172p*.xml ~/flightgear-2020.3.17/fgdata/Protocol/`
    `cp -v ~/flightgear-control/protocol/input/f15c/f15c*.xml ~/flightgear-2020.3.17/fgdata/Protocol/`

----

##### Launch a simulator instance #####

1. Use the provided shell scripts in `flightgear-control/scripts` to launch a simulator configured for our purposes:
        `~/flightgear-control/scripts/f15c_runway.sh 5220 103 49.19524 -123.18084 f15c_beta`
    * Parameters:
        * 5220 - lower bounds for the port range. The simulator reserves this port and the next 19 ports for various I/O.
        * 103 - initial heading in degrees
        * 49.19524 - initial latitude position
        * -123.18084 - initial longitude position
        * f15c_beta - aircraft nickname
The scripts in `flightgear-control/scripts` are organized into [aircraft]_runway.sh and [aircraft]_flight.sh. 
    
#### Read telemetry from the simulator output stream ####

1. Launch a simulator instance according to the previous section.
    * The shell script specifies a socket protocol defined in f15c_output.xml with this line:
        ` --generic=socket,out,45,$TELEM_HOST,$TELEM_OUTPUT_PORT,udp,f15c_output\`
1. Use netcat to output the simulator telemetry stream to the shell. The output stream uses the first port in the range reserved by the simulator.
    `nc -l -u -p 5220 127.0.0.1`

#### Display the simulator view ####

1. Launch a simulator instance via:
    `~/flightgear-control/scripts/f15c_runway.sh 5220 103 49.19524 -123.18084 f15c_beta`
1. Open URL `http://localhost:5222/screenshot?type=jpg` in a web browser and confirm that the simulator view appears. Some of the packaged shell scripts (typically `*_flight.sh`) enable retrieval of the simulator view with line: `--httpd=$CAM_VIEW_PORT\`

#### Generate flightgear-control application jar ####

1. Run the build task `appjar` with the gradle wrapper in the `flightgear-control` project root, specifying JDK 8:
    `JAVA_HOME=/path/to/jdk8 ./gradlew appjar`
1. Check that the application jar `flightgear-control-[version]-app.jar` appears in `build/lib`

#### Run a test flight ####

1. Run a simulator instance via the provided shell scripts:
    `flightgear-control/scripts/f15c_runway.sh 5220 103 49.19524 -123.18084 f15c_beta`
1. Select the config file that corresponds to the port range that will be used by the simulator. For the above example, the lower bounds of the port range is 5220, and the corresponding config file is `scripts/conf/f15c/f15c_beta_flight.properties`
1. Run one of the `flightgear-control` driver programs from the project root:
    `/path/to/jdk8/bin/java -cp build/libs/flightgear-control-[version]-app.jar org.jason.fgcontrol.aircraft.f15c.app.WaypointFlight scripts/conf/f15c/f15c_beta_flight.properties`
    * The properties file specifies which ports the flightgear-control application will use to communicate with the flightgear simulator instance. 
    * Driver programs available for use with the simulator:
        * org.jason.fgcontrol.aircraft.f15c.app.WaypointFlight
        * org.jason.fgcontrol.aircraft.f15c.app.RunwayBurnout
        * org.jason.fgcontrol.aircraft.c172p.app.WaypointFlight
        * org.jason.fgcontrol.aircraft.c172p.app.RunwayBurnout
    * The `*Flight` driver programs will terminate the simulator instance once its flight plan finishes execution.
    
#### Generate flightgear-control library jars ####

1. Run the build tasks `jar` and `sourcesjar` with the gradle wrapper in the `flightgear-control` project root, specifying JDK 8:
    `JAVA_HOME=/path/to/jdk8 ./gradlew jar sourcesjar`
1. Check that the jars `flightgear-control-[version].jar` and `flightgear-control-[version]-src.jar` appear in `build/lib`.
1. Use these jars in other projects.
    
#### IDE ####

1. Ensure your IDE has gradle project support.
1. Import flightgear-control as a gradle project, and configure it to use the included gradle wrapper.

