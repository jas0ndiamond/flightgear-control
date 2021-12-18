#!/bin/bash

#TODO: generate some of this from a template, since the ports are defined in the source

#for visuals use larger geometry: --geometry=1024x768\
#for apps user smaller geometry: --geometry=320x200\

#set /sim/time to noon in summer to avoid icing problems in colder climates

fgfs \
 --verbose\
 --prop:/nasal/local_weather/enabled=false\
 --metar=XXXX 012345Z 15003KT 12SM SCT041 FEW200 20/08 Q1015 NOSIG\
 --prop:/environment/weather-scenario=Fair weather\
 --timeofday=noon\
 --disable-rembrandt\
 --aircraft-dir=/usr/share/games/flightgear/Aircraft/c172p\
 --aircraft=c172p\
 --state=auto\
 --fog-fastest\
 --fg-scenery=/usr/share/games/flightgear/Scenery\
 --fg-aircraft=/usr/share/games/flightgear/Aircraft\
 --airport=CYVR\
 --generic=socket,out,45,localhost,6501,udp,c172p_output\
 --generic=socket,in,45,localhost,6601,udp,c172p_input_consumables\
 --generic=socket,in,45,localhost,6602,udp,c172p_input_controls\
 --generic=socket,in,45,localhost,6603,udp,c172p_input_fdm\
 --generic=socket,in,45,localhost,6604,udp,c172p_input_orientation\
 --generic=socket,in,45,localhost,6605,udp,c172p_input_position\
 --generic=socket,in,45,localhost,6606,udp,c172p_input_sim\
 --generic=socket,in,45,localhost,6607,udp,c172p_input_sim_freeze\
 --generic=socket,in,45,localhost,6608,udp,c172p_input_sim_speedup\
 --generic=socket,in,45,localhost,6609,udp,c172p_input_system\
 --generic=socket,in,45,localhost,6610,udp,c172p_input_velocities\
 --telnet=5501\
 --disable-ai-traffic\
 --disable-sound\
 --disable-real-weather-fetch\
 --geometry=1024x768\
 --texture-filtering=4\
 --disable-anti-alias-hud\
 --enable-auto-coordination\
 --prop:/sim/rendering/multithreading-mode=AutomaticSelection\
 --prop:/sim/time/gmt=2021-07-03T20:00:00\
 --prop:/engines/active-engine/winter-kit-installed=true\
 --prop:/engines/active-engine/complex-engine-procedures=false\
 --allow-nasal-from-sockets\
 --turbulence=0.0\
 --wind=0\@0
