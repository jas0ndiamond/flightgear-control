package org.jason.fgcontrol.aircraft.f15c;

import java.util.Properties;

import org.jason.fgcontrol.aircraft.config.ConfigDirectives;
import org.jason.fgcontrol.aircraft.config.SimulatorConfig;

public class F15CConfig extends SimulatorConfig {
   
	private final static String DEFAULT_AIRCRAFT_NAME = "F15C_Default";
	private String aircraftName;
	
    public F15CConfig() {
        super(
            SimulatorConfig.DEFAULT_TELNET_HOST, 
            SimulatorConfig.DEFAULT_TELNET_PORT, 
            SimulatorConfig.DEFAULT_SOCKETS_TELEM_HOST, 
            SimulatorConfig.DEFAULT_SOCKETS_TELEM_PORT
        );
        
        aircraftName = DEFAULT_AIRCRAFT_NAME;
    }
    
    public F15CConfig(String telnetHostname, int telnetPort, String socketsHostname, int socketsPort) {
        super(
        	telnetHostname,
        	telnetPort,
        	socketsHostname,
        	socketsPort
        );

        aircraftName = DEFAULT_AIRCRAFT_NAME;
    }
    
    public F15CConfig(Properties configProperties) {
    	super(configProperties);
    	
    	//any f15c-specific config processing happens here
    	if(configProperties.containsKey(ConfigDirectives.AIRCRAFT_NAME_DIRECTIVE)) {
    		aircraftName = configProperties.getProperty(ConfigDirectives.AIRCRAFT_NAME_DIRECTIVE);
    	} 
    	else {
    		aircraftName = DEFAULT_AIRCRAFT_NAME;
    	}
    }
    
    public String getAircraftName() {
		return aircraftName;
	}

	public void setAircraftName(String aircraftName) {
		this.aircraftName = aircraftName;
	}    
}
