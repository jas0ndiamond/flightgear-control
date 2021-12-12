package org.jason.flightgear.planes.c172p.app;

import java.io.IOException;

import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.jason.flightgear.exceptions.FlightGearSetupException;
import org.jason.flightgear.flight.WaypointPosition;
import org.jason.flightgear.flight.util.FlightLog;
import org.jason.flightgear.flight.util.FlightUtilities;
import org.jason.flightgear.flight.waypoints.KnownPositions;
import org.jason.flightgear.flight.waypoints.WaypointManager;
import org.jason.flightgear.flight.waypoints.WaypointUtilities;
import org.jason.flightgear.planes.c172p.C172P;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaypointFlight {
		
	private static Logger logger = LoggerFactory.getLogger(WaypointFlight.class);
	
	private final static int TARGET_ALTITUDE = 9000;
	
	private static void launch(C172P plane) throws IOException {
		//assume start unpaused;
		
		//assume already set
		double takeoffHeading = plane.getHeading();
		
		plane.setPause(true);
		
		//place in the air
		plane.setAltitude(TARGET_ALTITUDE);
		
		//high initially to cut down on the plane falling out of the air
		plane.setAirSpeed(200);
		
		plane.setPause(false);
		
		int i = 0;
		while( i < 20) {
			//FlightUtilities.airSpeedCheck(plane, 10, 100);
			
			FlightUtilities.altitudeCheck(plane, 500, TARGET_ALTITUDE);
			FlightUtilities.pitchCheck(plane, 4, 3.0);
			FlightUtilities.rollCheck(plane, 4, 0.0);
			
			//narrow heading check on launch
			FlightUtilities.headingCheck(plane, 4, takeoffHeading);
			
			i++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//set while not paused. this functions more like a boost- 
		//the plane can be acceled or deceled to the specified speed, 
		//but then the fdm takes over and stabilizes the air speed
//		plane.setAirSpeed(100);
//		
//		//initial drop. allow to level off
//		try {
//			Thread.sleep(40*1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		//////
		//initial check that we've leveled off
		FlightUtilities.altitudeCheck(plane, 500, TARGET_ALTITUDE);
		FlightUtilities.pitchCheck(plane, 4, 3.0);
		FlightUtilities.rollCheck(plane, 4, 0.0);
		
		//increase throttle
		plane.setPause(true);
		plane.setThrottle(0.85);
		plane.setPause(false);
	}
	
	private static String telemetryReadOut(C172P plane, WaypointPosition position, double targetBearing) {
				
		return 
			String.format("\nWaypoint: %s", position.getName()) +
			String.format("\nWaypoint Latitude: %s", position.getLatitude()) +
			String.format("\nWaypoint Longitude: %s", position.getLongitude()) +
			String.format("\nDistance remaining to waypoint: %s", 
					WaypointUtilities.distanceBetweenPositions(plane.getPosition(), position)) +
			String.format("\nTarget bearing: %f", targetBearing) +
			String.format("\nCurrent Heading: %f", plane.getHeading()) +
			String.format("\nAir Speed: %f", plane.getAirSpeed()) +
			String.format("\nFuel level: %f", plane.getFuelLevel()) +
			String.format("\nBattery level: %f", plane.getBatteryCharge()) +
			String.format("\nEngine running: %d", plane.getEngineRunning()) + 
			String.format("\nThrottle: %f", plane.getThrottle()) +
			String.format("\nMixture: %f", plane.getMixture()) +
			String.format("\nAltitude: %f", plane.getAltitude()) +
			String.format("\nLatitude: %f", plane.getLatitude()) + 
			String.format("\nLongitude: %f", plane.getLongitude()					
			);
	}
	
	public static void main(String [] args) throws IOException {
		C172P plane = null;
		
		FlightLog flightLog = new FlightLog();
		 
		WaypointManager waypointManager = new WaypointManager();
		
		
		
		//local tour
		//C172P script launches from YVR
		waypointManager.addWaypoint(KnownPositions.STANLEY_PARK);
		waypointManager.addWaypoint(KnownPositions.LONSDALE_QUAY);
		waypointManager.addWaypoint(KnownPositions.WEST_LION);
		waypointManager.addWaypoint(KnownPositions.MT_SEYMOUR);
		waypointManager.addWaypoint(KnownPositions.BURNABY_8RINKS);
		// loop again
		waypointManager.addWaypoint(KnownPositions.VAN_INTER_AIRPORT_YVR);
		waypointManager.addWaypoint(KnownPositions.STANLEY_PARK);
		waypointManager.addWaypoint(KnownPositions.LONSDALE_QUAY);
		waypointManager.addWaypoint(KnownPositions.WEST_LION);
		waypointManager.addWaypoint(KnownPositions.MT_SEYMOUR);
		waypointManager.addWaypoint(KnownPositions.BURNABY_8RINKS);
		waypointManager.addWaypoint(KnownPositions.VAN_INTER_AIRPORT_YVR);
		
		//bc tour
		//C172P script launches from YVR
//		waypointManager.addWaypoint(KnownPositions.ABBOTSFORD);
//		waypointManager.addWaypoint(KnownPositions.PRINCETON);
//		waypointManager.addWaypoint(KnownPositions.PENTICTON);
//		waypointManager.addWaypoint(KnownPositions.KELOWNA);
//		waypointManager.addWaypoint(KnownPositions.KAMLOOPS);
//		waypointManager.addWaypoint(KnownPositions.REVELSTOKE);
//		waypointManager.addWaypoint(KnownPositions.HUNDRED_MI_HOUSE);
//		waypointManager.addWaypoint(KnownPositions.PRINCE_GEORGE);
//		waypointManager.addWaypoint(KnownPositions.DAWSON_CREEK);
//		waypointManager.addWaypoint(KnownPositions.FORT_NELSON);
//		waypointManager.addWaypoint(KnownPositions.JADE_CITY);
//		waypointManager.addWaypoint(KnownPositions.DEASE_LAKE);
//		waypointManager.addWaypoint(KnownPositions.HAZELTON);
//		waypointManager.addWaypoint(KnownPositions.PRINCE_RUPERT);
//		waypointManager.addWaypoint(KnownPositions.BELLA_BELLA);
//		waypointManager.addWaypoint(KnownPositions.PORT_HARDY);
//		waypointManager.addWaypoint(KnownPositions.TOFINO);
//		waypointManager.addWaypoint(KnownPositions.VICTORIA);
//		waypointManager.addWaypoint(KnownPositions.VAN_INTER_AIRPORT_YVR);
		

		
		WaypointPosition startingWaypoint = waypointManager.getNextWaypoint();

		try {
			plane = new C172P();
		
			plane.setDamageEnabled(false);
			
			
			//in case we get a previously lightly-used environment
			plane.refillFuelTank();
			plane.setBatteryCharge(1.0);
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//figure out the heading of our first waypoint based upon our current position
			WaypointPosition startPosition = plane.getPosition();
			double initialHeading = WaypointUtilities.getHeadingToTarget(startPosition, startingWaypoint);			
			
			//head north
			plane.setHeading(initialHeading);
			
			//TODO: check if engine running, plane is in the air, speed is not zero
			
			plane.startupPlane();
	
			//wait for startup to complete and telemetry reads to arrive
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//////////////////
			launch(plane);
	
			plane.setBatterySwitch(false);
			
			//i'm in a hurry and a c172p only goes so fast
			plane.setSpeedUp(8);
		
			double minFuelGal = 4.0;
			double minBatteryCharge = 0.25;
			
			//needs to be tuned depending on aircraft speed, sim speedup, and waypoint closeness
			int bearingRecalcCycleInterval = 5;	
			
			WaypointPosition nextWaypoint;
			double nextWaypointBearing = 0.0; //default north
			int waypointFlightCycles;
			while(waypointManager.getWaypointCount() > 0) {
				
				nextWaypoint = waypointManager.getAndRemoveNextWaypoint();
				
				//possibly slow the simulator down if the next waypoint is close.
				//it's possible that hard and frequent course adjustments are needed
				
				logger.info("Headed to next waypoint: {}", nextWaypoint.toString());
				
				nextWaypointBearing = WaypointUtilities.calcBearingToGPSCoordinates(plane.getPosition(), nextWaypoint);
				
				logger.info("Bearing to next waypoint: {}", nextWaypointBearing);
				
				waypointFlightCycles = 0;
				while( !WaypointUtilities.hasArrivedAtWaypoint(plane.getPosition(), nextWaypoint) ) {
				
					logger.info("======================\nCycle {} start.", waypointFlightCycles);

					flightLog.add(plane.getPosition());
					
					if(waypointFlightCycles % bearingRecalcCycleInterval == 0) {
						//reset bearing incase we've drifted
						nextWaypointBearing = WaypointUtilities.calcBearingToGPSCoordinates(plane.getPosition(), nextWaypoint);
						
						logger.info("Recalculating bearing to waypoint: {}", nextWaypointBearing);
					}
					
					// check altitude first, if we're in a nose dive that needs to be corrected first
					FlightUtilities.altitudeCheck(plane, 500, TARGET_ALTITUDE);

					// TODO: ground elevation check. it's a problem if your target alt is 5000ft and
					// you're facing a 5000ft mountain

					if(waypointFlightCycles % 50 == 0 ) {
						plane.forceStabilize(nextWaypointBearing, 0, 2.0);
					} else {
						FlightUtilities.pitchCheck(plane, 4, 2.0);

						FlightUtilities.rollCheck(plane, 4, 0.0);

						// check heading last-ish, correct pitch/roll first otherwise the plane will
						// probably drift off heading quickly
						
						FlightUtilities.headingCheck(plane, 4, nextWaypointBearing);
					}

					//FlightUtilities.airSpeedCheck(plane, 20, 100);

					// check fuel last last. easy to refuel
					if (plane.getFuelLevel() < minFuelGal) {
						plane.refillFuelTank();
					}
					
					//check battery level
					if (plane.getBatteryCharge() < minBatteryCharge) {
						plane.setBatteryCharge(0.9);
					}

					logger.info("Telemetry Read: {}", telemetryReadOut(plane, nextWaypoint, nextWaypointBearing));
					logger.info("\nCycle {} end\n======================", waypointFlightCycles);
					
					waypointFlightCycles++;
				}
				
				logger.info("Arrived at waypoint {}!", nextWaypoint.toString());
			}
			
			logger.info("No more waypoints. Trip is finished!");
		} catch (FlightGearSetupException e) {
			e.printStackTrace();
		}
		finally {
			if(plane != null) {
				
				plane.shutdown();
				
				try {
					plane.terminateSimulator();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidTelnetOptionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			flightLog.writeGPXFile(System.getProperty("user.dir") + "/c172p_"+System.currentTimeMillis() + ".gpx");
		}
	}
}
