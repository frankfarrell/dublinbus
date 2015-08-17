package dublinbus;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Point;

/*
	1356998445000000,7,0,00071003,2012-12-31,6106,D1,0,-6.231650,53.317768,0,7019,43004,3222,1
	*/
	/*
	Timestamp micro since 1970 01 01 00:00:00 GMT
	Line ID
	Direction
	Journey Pattern ID
	Time Frame (The start date of the production time table - in Dublin the production time table starts at 6am and ends at 3am)
	Vehicle Journey ID (A given run on the journey pattern)
	Operator (Bus operator, not the driver)
	Congestion [0=no,1=yes]
	Lon WGS84
	Lat WGS84
	Delay (seconds, negative if bus is ahead of schedule)
	Block ID (a section ID of the journey pattern)
	Vehicle ID
	Stop ID
	At Stop [0=no,1=yes]
	*/
	
/*
 * In GTFS 
 * Journey id is of form 1.1714.0-33A-y12-1.155.I
 * 
 */

@Entity
public class BusGPS{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private long timestamp;
	private String lineId;//eg bus number
	private String direction;
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	private String journeyPatternId;//Can be null sometimes
	
	private String timeFrame;
	public String getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(String timeFrame) {
		this.timeFrame = timeFrame;
	}

	private String vehicleJourneyId;//eg instance of bus number
	private boolean congestion;
	
	@JsonSerialize(using = PointToJsonSerializer.class)
	@JsonDeserialize(using = JsonToPointDeserializer.class)
	@Column(columnDefinition="Geometry")
    @Type(type="org.hibernate.spatial.GeometryType")
	private Point coordinates;
	
	private Double delay;//Is this not always zero?No, but well calculate it ourselves
	private String blockId;
	private String vehicleId;//What happens if bus breaks down?
	private String stopId;
	private boolean atStop;
	
	public BusGPS(){
	}

	public Point getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point coordinates) {
		this.coordinates = coordinates;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getJourneyPatternId() {
		return journeyPatternId;
	}

	public void setJourneyPatternId(String journeyPatternId) {
		this.journeyPatternId = journeyPatternId;
	}

	public String getVehicleJourneyId() {
		return vehicleJourneyId;
	}

	public void setVehicleJourneyId(String vehicleJourneyId) {
		this.vehicleJourneyId = vehicleJourneyId;
	}

	public boolean isCongestion() {
		return congestion;
	}

	public void setCongestion(boolean congestion) {
		this.congestion = congestion;
	}

	public Double getDelay() {
		return delay;
	}

	public void setDelay(Double delay) {
		this.delay = delay;
	}

	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getStopId() {
		return stopId;
	}

	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

	public boolean isAtStop() {
		return atStop;
	}

	public void setAtStop(boolean atStop) {
		this.atStop = atStop;
	}
	

}