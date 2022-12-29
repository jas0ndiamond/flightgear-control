package org.jason.fgcontrol.view.mjpeg;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.jason.fgcontrol.view.CameraViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: possibly migrate to javax.xml.ws.http
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class StreamHandler implements HttpHandler {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(MJPEGStreamer.class);
	
	//private final static int BUFFER_SIZE = 100;
	
	private static final String NL = "\r\n";
    private static final String BOUNDARY = "--boundary";
    private static final String HEAD = NL + NL + BOUNDARY + NL +
          "Content-Type: image/jpeg" + NL +
          "Content-Length: ";

	private boolean running;

	//TODO: refactor as a generic Streamable object 
	private CameraViewer cameraViewer;

	private final static int MAX_TIMEOUT_COUNT = 10;
	
	//sleep time after writing image
	private final static int FRAME_SLEEP = 100;
    
    public StreamHandler(CameraViewer cameraViewer) {
    	super();
    	
    	this.cameraViewer = cameraViewer;
    	
    	running = true;
    }
    
    public boolean isRunning() {
    	return this.running;
    }
    
    public void shutdown() {
    	LOGGER.info("Shutting down StreamHandler");
    	this.running = false;
    }
    
	/**
	 *	Handle an http exchange - start reading from the camera and writing to the exchange outputstream.
	 *
	 *	TODO: check exception flow
	 */
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		
		//is this in use?
		//2022-12-19 00:28:50.677-0800 [L: INFO] [O: o.j.f.v.m.MJPEGStreamer] [M: handle] [T: Thread-3] Closing StreamHandler output stream

		
		this.running = true;
		
		//set these ahead of the stream read loop
		Headers h = httpExchange.getResponseHeaders();
		h.set("Cache-Control", "no-cache, private");
		h.set("Content-Type", "multipart/x-mixed-replace;boundary=" + BOUNDARY);
		httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

		OutputStream os = httpExchange.getResponseBody();

		LOGGER.info("Entering StreamHandler run loop");
		
		byte[] imgData;
		int bytesReceived;
		
		try {
			//read from the CameraViewer and write the data to the http response output stream
			int timeoutCount = 0;
			while (this.running) {
				// System.out.print("Outputting...");
				// byte[] img = imageSource.getImage();
	
	//for typical use cases we might not need to buffer the images
	//			for(byte[] imgData : cameraViewer.readBuffer(BUFFER_SIZE)) {
	//
	//				os.write((HEAD + imgData.length + NL + NL).getBytes());
	//				os.write(imgData);
	//				os.flush();
	//			}
				
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("StreamHandler.handle loop iteration starting");
				}
				
				try {
					imgData = cameraViewer.readCurrentView();
					
					if(imgData == null) {
						LOGGER.warn("Received null image data from camera view. Skipping stream write.");
					}
					else {
					
						bytesReceived = imgData.length;
						
						if(LOGGER.isTraceEnabled()) {
							LOGGER.trace("Received {} bytes from readCurrentView", bytesReceived);
						}
						
						//TODO: create a double NL to save a concat- 
						if(bytesReceived > 0) {
							os.write((HEAD + bytesReceived + NL + NL).getBytes());
							os.write(imgData);
							os.flush();
						}
						
						Thread.sleep(FRAME_SLEEP);
						
						timeoutCount = 0;
					}
				}
				catch(IOException e) {
					
					//TODO: are we sure about this?
					if(
						e.getMessage().equalsIgnoreCase("broken pipe") ||
						e.getMessage().equalsIgnoreCase("stream is closed")						
					) {
						//likely shutdown
						
						//TODO: maybe don't shut down here if we need to re-use
						LOGGER.warn("IOException likely signalling shutdown: " + e.getMessage());
						
						shutdown();
					} else {
						LOGGER.warn("IOException likely related to a timed-out read- Continuing", e);
						timeoutCount++;
						
						if(timeoutCount >= MAX_TIMEOUT_COUNT ) {
							LOGGER.error("Too many timeouts, bailing.");
							shutdown();
						}
					}
				} catch (InterruptedException e) {
					LOGGER.warn("FrameSleep interrupted", e);
				}
			}
	
			LOGGER.info("Exiting StreamHandler run loop");
		}	
		finally {
			if (os != null) {
				LOGGER.info("Closing StreamHandler output stream");
				os.close();
			}
		}
	}
}
