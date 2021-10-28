package org.jason.flightgear.sockets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.jason.flightgear.manager.FlightGearInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage a virtual plane in a flightgear sim through a socket connection
 *  
 *
 */
public class FlightGearManagerSockets {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(FlightGearManagerSockets.class);
    
    private String host;
    private int telemetryPort;
    
    private DatagramPacket fgTelemetryPacket;
    
    private byte[] receivingDataBuffer;
    
    private final static int SOCKET_TIMEOUT = 5000;
    private final static int MAX_RECEIVE_BUFFER_LEN = 4096;
    
    //TODO: default values overridable, or used to generate protocol files from templates
    private final static String FG_SOCKET_PROTOCOL_VAR_SEP = ",";
    private final static String FG_SOCKET_PROTOCOL_LINE_SEP = "\n";
    
    //cache this so we don't have to constantly perform lookups when receiving data
    private final static String UTF8_CHARSET_STR = "UTF-8";
    private Charset utf8_charset;
    
    private HashMap<String, FlightGearInput> controlInputs;

    private Pattern telemetryLinePattern;
    
    public FlightGearManagerSockets(String host,  int telemetryPort) 
            throws SocketException, UnknownHostException {
        
    	if(Charset.isSupported(UTF8_CHARSET_STR)) {
    		utf8_charset = Charset.forName(UTF8_CHARSET_STR);
    	} else {
    		throw new SocketException("UTF8 charset not found");
    	}
    	
        //set this here rather than before the match because we don't want to initialize it with every telemetry read
        telemetryLinePattern = Pattern.compile("^[\"/]\\S+[\": ]\\S+[,]?$");
        
        this.host = host;
        this.telemetryPort = telemetryPort;
        
        receivingDataBuffer = new byte[MAX_RECEIVE_BUFFER_LEN];
        
        fgTelemetryPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        
        controlInputs = new HashMap<>();
    }
    
    /**
     * Need a way of resolving an input type to a schema and port
     * 
     * @param key
     * @param input
     */
    public void registerInput(String key, FlightGearInput input) {
        
        LOGGER.info("Registering control input: {}, on port: {}", key, input.getPort());
        
        controlInputs.put(key, input);
    }
    
    
    /**
     * Read output fields from the flightgear output socket.
     * 
     * @return    A JSON string of telemetry data.
     * 
     * @throws IOException
     */
    public String readTelemetry() throws IOException {
        
        LOGGER.trace("Telemetry called for {}:{}", host, telemetryPort);
        
        String output = "";
        
        DatagramSocket fgTelemetrySocket = null;

        
        try {

            //technically a server connection. we connect to the fg port, which starts sending us data
            fgTelemetrySocket = new DatagramSocket( telemetryPort, InetAddress.getByName(host) );
            fgTelemetrySocket.setSoTimeout(SOCKET_TIMEOUT);
            
            fgTelemetrySocket.receive(fgTelemetryPacket);
            
            output = new String(fgTelemetryPacket.getData()).trim();  
            
            LOGGER.trace("Raw telemetry was received from socket.");
            
        } catch (IOException e) {
            //comms errors connecting to fg telemetry socket
            
            //e.printStackTrace();
            LOGGER.warn("IOException reading raw telemetry from socket", e);

            throw e;
        }
        finally {
            if( fgTelemetrySocket != null && !fgTelemetrySocket.isClosed() ) {
                fgTelemetrySocket.close();
            }
            else
            {
                LOGGER.warn("Attempted to close fgTelemetrySocket, but was already closed or null");
            }
        }
                
        /*
         occasionally see this. not sure where those extra digits are coming from
        ...
        "/velocities/groundspeed-kt": 8.734266,
        "/velocities/vertical-speed-fps": -303.683014
        874}
        
        
        ...
        "/velocities/vertical-speed-fps": -163.828430
        8


        0}    
        */
        
        LOGGER.trace("=========================\nRaw telemetry received:\n{}\n=========================\n", output);
        
        String[] lines = output.split(FG_SOCKET_PROTOCOL_LINE_SEP);
        
        //TODO: count accepted lines and compare against schema
        
        LOGGER.trace("Cleaning up raw telemetry data");
        
        int telemetryLineCount = 0;
        StringBuilder cleanOutput = new StringBuilder();
        for(String line : lines) {
            
            //only match lines that look like telemetry
            if( telemetryLinePattern.matcher(line).matches() ) 
            {
                cleanOutput.append(line);
                telemetryLineCount++;
            } else {
                LOGGER.warn("Dropping malformed telemetry line: {}", line);
            }
        }
        
        LOGGER.debug("readTelemetry returning. Read {} lines", telemetryLineCount);
                
        
        //return after adding json braces
        return cleanOutput.insert(0, "{").append("}").toString();
    }
    
    public synchronized void writeInput(LinkedHashMap<String, String> inputHash, int port) {
        
        boolean validFieldCount = true;
        
        StringBuilder controlInput = new StringBuilder();
        controlInput.append(FG_SOCKET_PROTOCOL_LINE_SEP);
        
        //foreach key, write the value into a simple unquoted csv string. fail socket write on missing values
        for( Entry<String, String> entry : inputHash.entrySet()) {
                if(!entry.getValue().equals( "" )) {
                    controlInput.append(entry.getValue());
                }
                else {
                    LOGGER.error("Missing field value: {}" + entry.getKey());
                    
                    //field count check later
                    validFieldCount = false;
                    break;
                }
                controlInput.append(FG_SOCKET_PROTOCOL_VAR_SEP);
        }
        
        //trailing commas appear to be okay
        
        if(validFieldCount) {
            
            controlInput.append(FG_SOCKET_PROTOCOL_LINE_SEP);
        

            LOGGER.debug("Writing control input: {}", controlInput.toString());
                
            writeControlInput(controlInput.toString(), port);
        }
        else
        {
            LOGGER.error("Error writing control input. Missing field values");
        }

    }
    
    public synchronized void writeControlInput(String input, int port) {
        byte[] fgInputPayload = input.getBytes(utf8_charset);

        DatagramSocket fgInputSocket = null;

        LOGGER.debug("Sending input to {}:{}", host, port);
        
        try {
            DatagramPacket fgInputPacket = new DatagramPacket(
                fgInputPayload, 
                fgInputPayload.length, 
                InetAddress.getByName(host), 
                port
            );

            fgInputPacket.setData(input.getBytes(utf8_charset));

            fgInputSocket = new DatagramSocket();
            
            fgInputSocket.setSoTimeout(SOCKET_TIMEOUT);

            fgInputSocket.send(fgInputPacket);

            LOGGER.debug("Completed input write to {}:{}", host, port);
        } 
        catch (SocketException e) {
            //a subclass of IOException. timeouts are thrown here 
            LOGGER.warn("SocketException writing control input", e);
        }
        catch (IOException e) {
            //thrown on send()
            LOGGER.warn("IOException writing control input", e);
        } finally {
            if (fgInputSocket != null && !fgInputSocket.isClosed()) {
                fgInputSocket.close();
            }
            
            LOGGER.debug("writeControlInput for {}:{} returning", host, port);
        }
    }
    
//no stream resources to close
//    public void shutdown() {
//        
//    }
}

