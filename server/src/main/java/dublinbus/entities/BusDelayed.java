package dublinbus.entities;

import serialization.JsonToPointDeserializer;
import serialization.PointToJsonSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Point;

public class BusDelayed {

	public BusDelayed() {
		super();
	}

	public BusDelayed(Point coordinates, double delay, String lineId,
			String tripId) {
		super();
		//this.coordinates = coordinates;
		
		this.point = "POINT ("+coordinates.getX() + " "+ coordinates.getY() + ")";
		
		this.delay = delay;
		this.lineId = lineId;
		this.tripId = tripId;
	}
	
	public String point;

	//TODO Stategy for serializing this with ObjectMapper
	/*
	private Point coordinates;
	public Point getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Point coordinates) {
		this.coordinates = coordinates;
	}
	*/

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	private double delay;
	
	private String lineId;
	
	private String tripId;
	
}
