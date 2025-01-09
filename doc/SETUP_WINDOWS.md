# flightgear-control Environment Setup 
This document describes the steps to set up your Windows system with flightgear for running flights with the flightgear-control and thingworx-flightgear-edge projects. The target flightgear version is 2020.3.17.



## Dependencies ##
1. Windows 10.
1. Git
1. JDK 11
1. GPU preferably with more than 1Gb of VRAM.

## Simulator installation ##
This guide covers usage of a standard installation of flightgear on Windows 10.

----

##### flightgear simulator initial setup #####

1. Use a web browser to navigate to flightgear's release repository for version 2020.3.17 at https://sourceforge.net/projects/flightgear/files/release-2020.3
1. Download the following files.
    * `FlightGear-2020.3.17.exe`
    * `FlightGear-2020.3.17-data.txz`
    * `FlightGear-2020.3.17-update-data.txz`
1. Run the FlightGear installer `FlightGear-2020.3.17.exe` and install to the default directory.
1. Extract the data archives to the current directory in the following order. Both archives should extract to an `fgdata` directory. Ensure that an `fgdata` directory does not exist inside another `fgdata` directory.
    1. path to fgdata is hardcoded in Windows under user profile [Example >C:\Users\user1\fgdata]. Make sure the fgdata is stored here. If it is not stored under %USERPROFILE% directory , You may not able to launch the fg_launcher .
    1. Extract archive `FlightGear-2020.3.17-data.txz`
    1. Extract archive `FlightGear-2020.3.17-update-data.txz`
1. Add the directory containing the FlightGear executable to the Windows Path Environment Variable.[Example :C:\Program Files\FlightGear 2020.3.17\bin]
1. The simulator scripts used by the `flightgear-control` establish and depend on a consistent FG_HOME and FG_ROOT, otherwise simulator assets like aircraft will have to be redownloaded each time the simulator is launched.
1. Clone the `flightgear-control` project, and check out the latest tagged release.
1. Enable powershell script execution if necessary with `Set-ExecutionPolicy RemoteSigned`
1. Run the simulator setup powershell script with:
    `flightgear-control/scripts/fg_launcher.ps1`
    
----

##### flightgear aircraft model installation #####
1. After the launcher UI is visible, click 'Okay' on the welcome screen if it appears. Enlarge the window if the welcome screen doesn't appear to show an 'Okay' button.
1. If inclined, navigate to "Settings" and disable crash and error reporting.
1. Navigate to the "Aircraft" section. 
1. Select the "Browse" tab near the top center of the UI. Add the default hanger if there is an option to do so.
1. Search (top right of window) for the following models required by the `flightgear-control` project, and install them if necessary. If a model doesn't appear in the search results, it's possible a minimum ratings requirement is excluding it from results.
    1. Cessna 172P Skyhawk (1982)
    1. F-15C
    1. Lockheed Martin F-35B Lightning II
    1. Alouette-III
1. Exit the simulator UI. Manage aircraft models installed in the simulator by re-running the `fg_launcher.ps1` shell script.

----

##### flightgear Protocol Setup #####

1. Locate the flightgear installation directory on your OS. 
1. Copy the project protocol xml files under `flightgear-control/protocol` directory into the flightgear installation data `Protocol` directory without preserving the source directory structure. All xml files should end up in this directory without the source directory structure. For example:
	* `Get-ChildItem ".\protocol\" -Include *.xml -Recurse | Copy-Item -Destination  "$Home/fgdata/Protocol" -Verbose`

----

##### Launch a simulator instance #####

1. Use the provided powershell script in `flightgear-control/scripts` to launch a simulator configured for our purposes:
        `flightgear-control/scripts/f15c_flight.ps1 5220 103 49.19524 -123.18084 f15c_beta`
    * Parameters:
        * 5220 - lower bounds for the port range. The simulator reserves this port and the next 19 ports for various I/O.
        * 103 - initial heading in degrees
        * 49.19524 - initial latitude position
        * -123.18084 - initial longitude position
        * f15c_beta - aircraft nickname
    * A table of port ranges mapped to application config files is available [here](PORT_RANGES.md)
    * The scripts in `flightgear-control/scripts` are organized into [aircraft]_runway.* and [aircraft]_flight.*.
    
----
    
#### Read telemetry from the simulator output stream ####

1. Launch a simulator instance according to the previous section.
    * The shell script specifies a socket protocol defined in f15c_output.xml with this line:
        ` --generic=socket,out,45,$TELEM_HOST,$TELEM_OUTPUT_PORT,udp,f15c_output\`
1. Optionally use netcat to output the simulator telemetry stream to the shell. The output stream uses the first port in the range reserved by the simulator.

----

#### Display the simulator view ####

1. Launch a simulator instance via:
    `flightgear-control/scripts/f15c_flight.ps1 5220 103 49.19524 -123.18084 f15c_beta`
1. Optionally open URL `http://localhost:5222/screenshot?type=jpg` in a web browser and confirm that the simulator view appears. Some of the packaged shell scripts (typically `*_flight.ps1`) enable retrieval of the simulator view with line: `--httpd=$CAM_VIEW_PORT\`
    
----
    
#### Generate flightgear-control library jars ####

1. Switch to the project root of the `flightgear-control` project, and ensure the latest release or development branch is checked out.
1. Run the build tasks `jar` and `sourcesjar` with the gradle wrapper, specifying JDK 11:
	`$Env:JAVA_HOME = "C:/Users/user/Desktop/openjdk-11.0.2"`
	`./gradlew jar sourcesjar`
1. Check that the jars `flightgear-control-[version].jar` and `flightgear-control-[version]-src.jar` appear in `build/lib`.
1. Use these jars in other projects.
    
----

#### Generate flightgear-control application jar ####

1. Switch to the project root of the `flightgear-control` project, and ensure the latest release or development branch is checked out.
1. Run the build tasks `appjar` with the gradle wrapper, specifying JDK 11:
	`$Env:JAVA_HOME = "C:/Users/user/Desktop/openjdk-11.0.2"`
	`./gradlew jar appjar`
1. Check that the application jar `flightgear-control-[version]-app.jar` appears in `build/lib`
    
----
    
#### IDE ####

1. Ensure your IDE has gradle project support.
1. Import flightgear-control as a gradle project, and configure it to use the included gradle wrapper.

----

#### Notes ####

* Simulator startup appears to have trouble synchronizing terrain assets with the remote server on Windows. If the simulator appears to exit without error upon launch, try re-running it. It typically succeeds after several (5-15) attempts.
* The project powershell scripts specify a `--download-dir` parameter when the simulator is invoked, which defaults to a path outside of FG_ROOT.
* Windows setup places the simulator data assets in the user home directory rather than the default simulator install directory in `C:\Program Files\`.
* Simulator arguments with spaces and some special characters (@) are not properly escaped/handled by the project powershell scripts. 

----

#### Run flights ####

Documented [here](OPERATION.md).
