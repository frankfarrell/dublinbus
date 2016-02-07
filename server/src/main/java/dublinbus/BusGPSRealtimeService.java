package dublinbus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.geotools.math.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import dublinbus.entities.BusGPS;
import dublinbus.entities.Routes_enriched;
import dublinbus.redis.RedisService;

/*
 * Accepts a new BusGPS data point
 * Queries Repository for Expected Bus Route Linestring. We can mark this with @Cacheable, should work?
 * Finds the closest line segment on route with JTS, and calculates fraction along this line segment bus is. 
 * Gets the difference between times and takes mean. 
 * Establishes how late bus is. 
 * Pushes this value to redis. How to store it?
 * Redis used for pub sub via socketio to client
 */
@Service
public class BusGPSRealtimeService {

	RouteEstimatorService routeEstimatorService;
	RedisService redisService;
	final Logger logger;
	
	@Autowired
    public BusGPSRealtimeService(RouteEstimatorService routeEstimatorService, RedisService redisService) {

		logger = LoggerFactory.getLogger(BusGPSRealtimeService.class);

		this.routeEstimatorService = routeEstimatorService;
		this.redisService = redisService;
	}

	//on a service method
	@HystrixCommand(
			commandProperties = {
					@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
					//Max threads allowed is default of 10
			})
	public void submityBusGPS(BusGPS busGPS) throws ParseException{

		//Get the list of stops, along with coordinates and distance travlled etc.
		if(redisService.isBusInBlacklist(busGPS.getLineId() + "."+ busGPS.getVehicleJourneyId())){
			logger.info("Blacklisted journey id{}", busGPS.getVehicleJourneyId());
			return;
		}

		logger.info("Starting Estimating Delay for journey id{}", busGPS.getVehicleJourneyId());

		//Service caches reslt internally
		List<Routes_enriched> stopsForTrip = routeEstimatorService.getEstimateRouteForGps(busGPS);//enrichedEnrichedRouteRepository.findByTripId(busGPS.getVehicleJourneyId());

		if(stopsForTrip == null){
			return;
		}

		ArrayList<LineSegment> routeLineSegments = lineSegmentsForRoutePoints(stopsForTrip);

		IndexedSegment indexedSegment = getClosestSegment(busGPS, routeLineSegments);

		LineSegment closestSegment = indexedSegment.lineSegment;

		//Index of original route point
		int segStartIndex = indexedSegment.index;

		//If we wanted to create a linestring
		//Coordinate[] coords = new Coordinate[stopsForTrip.size()];
		//coords[i] = new Coordinate(point1.getX(), point1.getY()); 
		//GeometryFactory fact = new GeometryFactory();
		//LineString lineString = fact.createLineString(coords);
		
		//Calculate how far along line segment coord is. Calculate distance and expected time. 

		Coordinate busCoordinates = new Coordinate(busGPS.getCoordinates().getX(), busGPS.getCoordinates().getY()) ;
		Coordinate ratioCoordinate = closestSegment.closestPoint(busCoordinates);
		
		LineSegment ratioSeg = new LineSegment(closestSegment.p0, ratioCoordinate);

		//Eg how far along this segment we have travelled
		double ratio  = ratioSeg.getLength()/ closestSegment.getLength();

		Routes_enriched startStop = stopsForTrip.get(segStartIndex);
		Routes_enriched endStop = stopsForTrip.get(segStartIndex +1);

		//We can assume its today
		//TODO Need to stick in timeframe date, and check if its before 12 2013-01-01
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-DD", Locale.ENGLISH).withZone( ZoneId.of("GMT") );

		DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-DD_HH:mm:ss", Locale.ENGLISH).withZone( ZoneId.of("GMT") );

		//TODO Change this to system.currenttime etc, this assumes time from gps is current time, eg for demo purposes
		Instant nowTime = Instant.ofEpochMilli(busGPS.getTimestamp()/1000);
		
		LocalDateTime now =  LocalDateTime.from(nowTime.atZone(ZoneId.of("GMT")));
		
		Date endDate = timeFormatter.parse(endStop.getTimestamp());
		Date startDate = timeFormatter.parse(startStop.getTimestamp());
		
		LocalDateTime endDateParsed;
		LocalDateTime startDateParsed;
		
		String endStopTimestamp = endStop.getTimestamp();
		String startStopTimestamp = endStop.getTimestamp();
		
		//We need to convert 24 to 00 in hour, etc
		endStopTimestamp = endStopTimestamp.replaceFirst("^24:", "00:");
		startStopTimestamp = startStopTimestamp.replaceFirst("^24:", "00:");
		
		endStopTimestamp = endStopTimestamp.replaceFirst("^25:", "01:");
		startStopTimestamp = startStopTimestamp.replaceFirst("^25:", "01:");
		
		
		//If it is currently after twelve, is less than 6, but the stops are now, set date to yesterday. 
		if(now.get(ChronoField.HOUR_OF_DAY)< 5){
			//If end time is after twelve
			if(endDate.getHours() <5){
				endDateParsed = LocalDateTime.parse(dateFormatter.format(now) + "_"+ endStopTimestamp, fullFormatter);
			}
			else{
				endDateParsed = LocalDateTime.parse(dateFormatter.format(now.minus(1L, ChronoUnit.DAYS)) + "_"+ endStopTimestamp, fullFormatter);
			}
			if(startDate.getHours() <5){
				startDateParsed = LocalDateTime.parse(dateFormatter.format(now) + "_"+ startStopTimestamp, fullFormatter);
			}
			else{
				startDateParsed = LocalDateTime.parse(dateFormatter.format(now.minus(1L, ChronoUnit.DAYS)) + "_"+ startStopTimestamp, fullFormatter);
			}
		}
		//Case where its before 12 buts stops might be after twelve
		else{
			//If end time is after twelve
			if(endDate.getHours() > 4){
				endDateParsed = LocalDateTime.parse(dateFormatter.format(now) + "_"+ endStopTimestamp, fullFormatter);
			}
			else{
				endDateParsed = LocalDateTime.parse(dateFormatter.format(now.plus(1L, ChronoUnit.DAYS)) + "_"+ endStopTimestamp, fullFormatter);
			}
			if(startDate.getHours() > 4){
				startDateParsed = LocalDateTime.parse(dateFormatter.format(now) +"_"+ startStopTimestamp, fullFormatter);
			}
			else{
				startDateParsed = LocalDateTime.parse(dateFormatter.format(now.plus(1L, ChronoUnit.DAYS)) + "_"+ startStopTimestamp, fullFormatter);
			}
		}
		
		Instant startTimestamp = startDateParsed.toInstant(ZoneOffset.UTC);
		Instant endTimestamp = endDateParsed.toInstant(ZoneOffset.UTC);
		
		Duration between = Duration.between(startTimestamp, endTimestamp);
		
		Duration timeAfterStart = between.multipliedBy(Math.round(ratio));
		
		Instant expectedBusTime = startTimestamp.plus(timeAfterStart);
		
		Instant actualBusTime = Instant.ofEpochMilli(busGPS.getTimestamp()/1000);
		
		
		double delayInSeconds = Duration.between(expectedBusTime, actualBusTime).getSeconds();

		logger.info("Delay calculated for journey id : {}, delay : {}", busGPS.getVehicleJourneyId(), Double.valueOf(delayInSeconds));

		//TODO Make this an async que
		redisService.sendRedisMessage(busGPS.getLineId(), busGPS.getVehicleJourneyId() , delayInSeconds,  busGPS.getCoordinates());

	}

	private ArrayList<LineSegment>  lineSegmentsForRoutePoints(List<Routes_enriched> stopsForTrip){
		ArrayList<LineSegment> listOfSegments = new ArrayList<LineSegment>(stopsForTrip.size());

		for(int i=0;i<stopsForTrip.size() -2 ;i++){
			Point point1 = stopsForTrip.get(i).getGeom();
			Point point2 = stopsForTrip.get(i +1).getGeom();

			LineSegment lineSeg = new LineSegment(point1.getX(), point1.getY(), point2.getX(), point2.getY());

			listOfSegments.add(lineSeg);
		}

		return listOfSegments;
	}

	private IndexedSegment getClosestSegment(BusGPS busGPS, ArrayList<LineSegment>  listOfSegments){
		//Null means not set yet
		IndexedSegment closestSegment = null;
		Double distance = null;

		Coordinate busCoordinates = new Coordinate(busGPS.getCoordinates().getX(), busGPS.getCoordinates().getY()) ;

		for(int i=0;i<listOfSegments.size();i++){
			LineSegment lineSeg = listOfSegments.get(i);
			if(closestSegment == null){
				closestSegment = new IndexedSegment(i, lineSeg);
				distance = lineSeg.distance(busCoordinates);
			}
			else{
				double tempDist = lineSeg.distance(busCoordinates);

				if(tempDist < distance){
					closestSegment = new IndexedSegment(i, lineSeg);
					distance = tempDist;
				}
			}
		}
		return closestSegment;
	}

	private class IndexedSegment {
		public final int index;
		public final LineSegment lineSegment;

		public IndexedSegment(int index, LineSegment lineSegment) {
			this.index = index;
			this.lineSegment = lineSegment;
		}
	}

}
