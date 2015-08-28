package dublinbus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import dublinbus.entities.BusGPS;
import dublinbus.entities.Routes_enriched;
import dublinbus.repositories.EnrichedRouteRepository;

@Service
public class RouteEstimatorService {

	private EnrichedRouteRepository enrichedEnrichedRouteRepository;
	
	@Autowired
    public RouteEstimatorService(EnrichedRouteRepository enrichedEnrichedRouteRepository) {

		this.enrichedEnrichedRouteRepository = enrichedEnrichedRouteRepository;
    }
	
	//@Cacheable Is there a way to cache for certain values? Yes, could change to args Trip_id, LineId, etc
	public List<Routes_enriched> getEstimateRouteForGps(BusGPS busGPS){
		
		final String lineId = busGPS.getLineId();
		
		final String vehicleJourneyId = busGPS.getVehicleJourneyId();
		
		//final Instant = 
		
		List<Routes_enriched> stopsForTrip = enrichedEnrichedRouteRepository.findByTripId(vehicleJourneyId);
		
		Routes_enriched sampleRoute =  stopsForTrip.get(0);
		String routeName  = sampleRoute.getRouteShortName();

		if(routeName.equals(lineId)){
			return stopsForTrip;
		}
		//GPS Data is incorrect
		else{
			List<Routes_enriched> topRoute =  enrichedEnrichedRouteRepository.findByRouteShortName(lineId, busGPS.getCoordinates().getX(), busGPS.getCoordinates().getY(), busGPS.getTimestamp()/1000000);
			
			if(topRoute.size()>0){
				return enrichedEnrichedRouteRepository.findByTripId(topRoute.get(0).getTripId());
			}
			
			else{
				return null;
			}

		}
		
		
		//We check if line id matches. 
		//Trip_id on gps data can be wrong. 
		//If so, we search enriched route repo by line id, and pick the set that is most likely. 
		//How to establish most likely?
		//Closest in time?
		
		
	}
	
}
