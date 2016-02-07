package dublinbus.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;

import serialization.JsonToPointDeserializer;
import serialization.PointToJsonSerializer;

import com.vividsolutions.jts.geom.Point;

@Entity
@IdClass(Routes_enriched.RouteId.class)
public class Routes_enriched {

	@Id
	@Column(name="trip_id")
	private String tripId;
	
	@Id
	@Column(name="stop_id")
	private String stopId;
	
	@Id
	@Column(name="route_short_name")
	private String routeShortName;
	

	public String getRouteShortName() {
		return routeShortName;
	}

	public void setRouteShortName(String routeShortName) {
		this.routeShortName = routeShortName;
	}

	@JsonSerialize(using = PointToJsonSerializer.class)
	@JsonDeserialize(using = JsonToPointDeserializer.class)
	@Column(columnDefinition="Geometry")
    @Type(type="org.hibernate.spatial.GeometryType")
	private Point geom;
	
	private double distance_travlled;
	
	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public String getStopId() {
		return stopId;
	}

	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

	public Point getGeom() {
		return geom;
	}

	public void setGeom(Point geom) {
		this.geom = geom;
	}


	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public double getDistance_travlled() {
		return distance_travlled;
	}

	public void setDistance_travlled(double distance_travlled) {
		this.distance_travlled = distance_travlled;
	}

	private String timestamp;
	
	public static class RouteId implements Serializable{

		public RouteId(){
			
		}
		
		public static final long serialVersionUID = -7325216785955201470L;

		private String tripId;
		
		private String stopId;

		public String getTripId() {
			return tripId;
		}

		public void setTripId(String tripId) {
			this.tripId = tripId;
		}

		public String getStopId() {
			return stopId;
		}

		public void setStopId(String stopId) {
			this.stopId = stopId;
		}

	}
}
