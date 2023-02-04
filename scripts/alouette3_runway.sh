#!/bin/bash

#Usage: ./alouette3_runway.sh START_PORT_RANGE NAME

FG_AIRCRAFT=org.flightgear.fgaddon.stable_2020.Alouette-III

#############################
#find the AppImage on the path
#keep consistent with fg_launcher.sh

APPIMAGE_FILE=FlightGear-2020.3.17-x86_64.AppImage

FG_BIN_PATH=`whereis -b $APPIMAGE_FILE | awk '{print $2}'`

if [ -z "$FG_BIN_PATH" ]; then
    echo "Could not find FlightGear AppImage on path. Ensure the FlightGear AppImage location is in \$PATH."
    exit 1
else
    echo "Found FlightGear AppImage at $FG_BIN_PATH"
    
    FG_BIN_DIR=`dirname $FG_BIN_PATH`
    
    if [ -z "$FG_BIN_DIR" ]; then
        echo "Could not determine parent directory for FlightGear AppImage"
        exit 1
    else
        echo "Found FlightGear directory at $FG_BIN_DIR"
        
        FG_HOME_DIR=$FG_BIN_DIR/fgfs 
        FG_ROOT_DIR=$FG_BIN_DIR/fgdata 
    fi
fi

#############################

#works for external and internal input
INPUT_HOST="0.0.0.0"

TELEM_HOST="localhost"

#############################
#this is the window geometry, not the sim video resolution
#for visuals use larger geometry: --geometry=1024x768\
#for apps user smaller geometry: --geometry=320x200\

X_RES=1024
Y_RES=768

RES_GEOMETRY_STR=""$X_RES"x"$Y_RES

################
#ports
START_PORT_RANGE=${1:-6500}

#check port range constraints (not too low, not above max)

#TODO: selectively enable httpd

#port population:
#START_PORT_RANGE   => output
#+1         => telnet
#+2     => httpd => not used with runway operation
#+3     => input 1
#+4     => input 2
#...
TELEM_OUTPUT_PORT=$START_PORT_RANGE
TELNET_PORT=$((START_PORT_RANGE+1))
CONSUMABLES_INPUT_PORT=$((START_PORT_RANGE+3))
CONTROLS_INPUT_PORT=$((START_PORT_RANGE+4))
ENGINES_INPUT_PORT=$((START_PORT_RANGE+5))
FDM_INPUT_PORT=$((START_PORT_RANGE+6))
ORIENTATION_INPUT_PORT=$((START_PORT_RANGE+7))
POSITION_INPUT_PORT=$((START_PORT_RANGE+8))
SIM_INPUT_PORT=$((START_PORT_RANGE+9))
SIM_FREEZE_INPUT_PORT=$((START_PORT_RANGE+10))
SIM_MODEL_INPUT_PORT=$((START_PORT_RANGE+11))
SIM_SPEEDUP_INPUT_PORT=$((START_PORT_RANGE+12))
SIM_TIME_INPUT_PORT=$((START_PORT_RANGE+13))
SYSTEMS_INPUT_PORT=$((START_PORT_RANGE+14))
VELOCITIES_INPUT_PORT=$((START_PORT_RANGE+15))

########
#name of this aircraft
#mostly for the log directory so multiple simulators aren't logging to the same place
DATE_STR="$(date +%s)"
DEFAULT_NAME="Alouette3_"$DATE_STR
NAME=${2:-$DEFAULT_NAME}

#log directory from the aircraft name
LOG_DIR=$FG_HOME_DIR/log/fgfs_$NAME

#create the directory if it doesnt already exist
if [ ! -d $LOG_DIR ]; then
    mkdir -p $LOG_DIR
    
    #check that the log directory exists now
    if [ ! -d $LOG_DIR ]; then
        echo "Error creating the simulator log directory"
        exit 1
    fi
fi

########

#switch "--enable-terrasync" with "--disable-terrasync" for offline use

#use --airport=CYVR for location

FG_HOME=$FG_HOME_DIR $APPIMAGE_FILE\
 --verbose\
 --ignore-autosave\
 --enable-terrasync\
 --timeofday=noon\
 --disable-rembrandt\
 --aircraft=$FG_AIRCRAFT\
 --fog-fastest\
 --airport=CYVR\
 --fg-root=$FG_ROOT_DIR\
 --generic=socket,out,45,$TELEM_HOST,$TELEM_OUTPUT_PORT,udp,alouette3_output\
 --generic=socket,in,45,$INPUT_HOST,$CONSUMABLES_INPUT_PORT,udp,alouette3_input_consumables\
 --generic=socket,in,45,$INPUT_HOST,$CONTROLS_INPUT_PORT,udp,alouette3_input_controls\
 --generic=socket,in,45,$INPUT_HOST,$ENGINES_INPUT_PORT,udp,alouette3_input_engines\
 --generic=socket,in,45,$INPUT_HOST,$FDM_INPUT_PORT,udp,alouette3_input_fdm\
 --generic=socket,in,45,$INPUT_HOST,$ORIENTATION_INPUT_PORT,udp,alouette3_input_orientation\
 --generic=socket,in,45,$INPUT_HOST,$POSITION_INPUT_PORT,udp,alouette3_input_position\
 --generic=socket,in,45,$INPUT_HOST,$SIM_INPUT_PORT,udp,alouette3_input_sim\
 --generic=socket,in,45,$INPUT_HOST,$SIM_FREEZE_INPUT_PORT,udp,alouette3_input_sim_freeze\
 --generic=socket,in,45,$INPUT_HOST,$SIM_MODEL_INPUT_PORT,udp,alouette3_input_sim_model\
 --generic=socket,in,45,$INPUT_HOST,$SIM_SPEEDUP_INPUT_PORT,udp,alouette3_input_sim_speedup\
 --generic=socket,in,45,$INPUT_HOST,$SIM_TIME_INPUT_PORT,udp,alouette3_input_sim_time\
 --generic=socket,in,45,$INPUT_HOST,$SYSTEMS_INPUT_PORT,udp,alouette3_input_systems\
 --generic=socket,in,45,$INPUT_HOST,$VELOCITIES_INPUT_PORT,udp,alouette3_input_velocities\
 --telnet=$TELNET_PORT\
 --log-dir=$LOG_DIR\
 --disable-ai-traffic\
 --disable-sound\
 --disable-real-weather-fetch\
 --geometry=$RES_GEOMETRY_STR\
 --texture-filtering=4\
 --disable-anti-alias-hud\
 --enable-auto-coordination\
 --prop:/environment/weather-scenario=Fair\ weather\
 --prop:/nasal/local_weather/enabled=false\
 --prop:/sim/menubar/autovisibility/enabled=true\
 --prop:/sim/menubar/visibility/enabled=false\
 --prop:/sim/rendering/fps-display=1\
 --prop:/sim/rendering/frame-latency-display=1\
 --prop:/sim/rendering/multithreading-mode=AutomaticSelection\
 --prop:/sim/rendering/redout/enabled=0\
 --prop:/sim/rendering/redout/internal/log/g-force=0\
 --prop:/sim/rendering/particles=false\
 --prop:/sim/rendering/rembrant/enabled=false\
 --prop:/sim/rendering/rembrant/bloom=false\
 --prop:/sim/rendering/shading=false\
 --prop:/sim/rendering/shadow-volume=false\
 --prop:/sim/rendering/shadows/enabled=false\
 --prop:/sim/startup/save-on-exit=false\
 --max-fps=30\
 --disable-clouds3d\
 --disable-specular-highlight\
 --allow-nasal-from-sockets\
 --turbulence=0.0\
 --wind=0\@0

