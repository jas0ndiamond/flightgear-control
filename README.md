# flightgear-control

Control and interact with virtual aircraft through the FlightGear simulator generically using telnet and sockets. 

Easily and economically simulate a complex real-world machine with mechanical causality.

Generically fly aircraft by executing the plane's engine startup script and diligently correcting drift and deviation from a flight path by enforcing orientation constraints within the simulator environment along a set of waypoints. 


<!---
 "loop" and "autoplay" aren't supported by github markdown rendering but maybe one day.
-->

<video controls="controls" muted="muted" loop="loop" autoplay="autoplay" src="https://github.com/jas0ndiamond/flightgear-control/assets/7103526/1934b311-00de-4316-be74-1b0301427464" >
  Your browser does not support the video tag.
</video>

----
#### Requirements ####

1. Mac OSX, Windows 10, or Linux x86_64 running with a windowing system.
1. Git
1. JDK 11
1. GPU preferably with more than 1Gb of VRAM.

----
#### Setup ####

* [Linux](doc/SETUP_LINUX.md)
* [Windows](doc/SETUP_WINDOWS.md)
* [MacOS](doc/SETUP_MAC.md)

----
#### Building ####

Use tasks `jar`, `appjar`, and `sourcesjar` to generate the jars for this project.
* `jar` - The primary output jar for incorporating into other projects.
* `appjar` - Driver applications for testing functionality locally.
* `sourcesjar` - Sources jar to accompany the main jar for incorporating into other projects.

----
#### Running Flights ####

Documented [here](doc/OPERATION.md).
