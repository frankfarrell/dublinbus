package dublinbus.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Lineal;

import dublinbus.JsonToPointDeserializer;
import dublinbus.PointToJsonSerializer;

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
