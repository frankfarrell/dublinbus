package dublinbus.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;

import serialization.JsonToPointDeserializer;
import serialization.LineToJsonSerializer;
import serialization.PointToJsonSerializer;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Lineal;

@Entity
public class Route_materialized {
	
	@Id
	private String trip_id;


	public String getTrip_id() {
		return trip_id;
	}

	public void setTrip_id(String trip_id) {
		this.trip_id = trip_id;
	}

	@JsonSerialize(using = LineToJsonSerializer.class)
	//@JsonDeserialize(using = JsonToPointDeserializer.class)
	@Column(columnDefinition="Geometry")
    @Type(type="org.hibernate.spatial.GeometryType")
	private LineString route;


	public LineString getRoute() {
		return route;
	}

	public void setRoute(LineString route) {
		this.route = route;
	}
}
