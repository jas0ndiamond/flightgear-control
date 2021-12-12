package org.jason.flightgear.flight;

/**
 * Wrapper for a plane position in flight. 
 *
 */
public class WaypointPosition {
	
	private final static String DEFAULT_NAME = "NO_NAME";
	
	private double latitude;
	private double longitude;
	private double altitude;
	private String name;
	
	public final static double DEFAULT_ALTITUDE = 1000;
	
	public WaypointPosition(double latitude, double longitude) {
		this(latitude, longitude, DEFAULT_ALTITUDE, DEFAULT_NAME);
	}
	
	public WaypointPosition(double latitude, double longitude, String name) {
		this(latitude, longitude, DEFAULT_ALTITUDE, name);
	}
	
	public WaypointPosition(double latitude, double longitude, double altitude) {
		this(latitude, longitude, altitude, DEFAULT_NAME);
	}
	
	public WaypointPosition(double latitude, double longitude, double altitude, String name) {
		//TODO: enforce 5 decimal precision
		
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	@Override
	public String toString() {
		return "PlanePosition [latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", name="
				+ name + "]";
	}
}