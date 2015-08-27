package dublinbus.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import serialization.LineToJsonSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.LineString;

/*
 * Entity class for materialized view
 * Logic of creating line string from timestamped points managed in SQL
 */

@Table(name="route_shapes")
@Entity
public class Route_shapes {

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
	private LineString st_makeline;

	public LineString getSt_makeline() {
		return st_makeline;
	}

	public void setSt_makeline(LineString st_makeline) {
		this.st_makeline = st_makeline;
	}


}
