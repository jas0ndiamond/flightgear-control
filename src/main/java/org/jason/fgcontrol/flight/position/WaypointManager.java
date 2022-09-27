package org.jason.fgcontrol.flight.position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dumbly manage a list of waypoints. 
 * 
 * The plane needs to internally compensate for a radical course change, and interpolate intermediate points as needed.
 * 
 * @author jason
 *
 *
 * TODO: offer function to import from gpx file with granularity parameter
 */
public class WaypointManager {

	public final static String WAYPOINT_NAME_FIELD = "Waypoint Name";
	public final static String WAYPOINT_NAME_FIELD_DESC = "Colloquial name of waypoint";
	
    private List<WaypointPosition> waypoints;
    
    public WaypointManager() {
        waypoints = Collections.synchronizedList(new ArrayList<WaypointPosition>());
    }
    
    public WaypointManager(ArrayList<WaypointPosition> positions) {    
        waypoints = Collections.synchronizedList(positions);
    }
    
    //add new waypoint to the end of the flightplan
    public synchronized void addWaypoint(double lat, double lon) {
        addWaypoint(new WaypointPosition(lat, lon));
    }
    
    //add new waypoint to the end of the flightplan
    public synchronized void addWaypoint(WaypointPosition newWaypoint) {        
        waypoints.add(waypoints.size(), newWaypoint);
    }
    
    //add a new waypoint as the next waypoint in the flightplan
    //invoker needs to manage abandonment of current waypoint
    public synchronized void addNextWaypoint(WaypointPosition newWaypoint) {      
    	waypoints.add(0, newWaypoint);
    }
    
    /**
     * Remove the first waypoint that matches the target lat/lon
     * 
     * @param lat
     * @param lon
     */
    public synchronized void removeWaypoints(double lat, double lon) {
    	waypoints.removeIf( waypoint -> waypoint.getLatitude() == lat && waypoint.getLongitude() == lon);
    }
    
    public synchronized WaypointPosition getNextWaypoint() {
        return waypoints.get(0);
    }
    
    public synchronized WaypointPosition getAndRemoveNextWaypoint() {
        return waypoints.remove(0);
    }
    
    public synchronized int getWaypointCount() {
        return waypoints.size();
    }

    public synchronized List<WaypointPosition> getWaypoints() {
        return waypoints;
    }

    public synchronized void setWaypoints(List<WaypointPosition> waypoints) {
    	this.waypoints.clear();
        this.waypoints.addAll(waypoints);
    }
    
    public synchronized void reset() {
    	this.waypoints.clear();
    }

    
    @Override
    public String toString() {
        return "WaypointManager [waypoints=" + waypoints + "]";
    }
}
