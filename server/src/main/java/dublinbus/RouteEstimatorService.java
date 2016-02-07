package dublinbus;

import java.util.Comparator;
import java.util.List;

import dublinbus.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import dublinbus.entities.BusGPS;
import dublinbus.entities.Routes_enriched;
import dublinbus.repositories.EnrichedRouteRepository;

@Service
public class RouteEstimatorService {

	private EnrichedRouteRepository enrichedEnrichedRouteRepository;

	private RedisService redisService;
	
	@Autowired
    public RouteEstimatorService(EnrichedRouteRepository enrichedEnrichedRouteRepository, RedisService redisService) {

		this.redisService = redisService;
		this.enrichedEnrichedRouteRepository = enrichedEnrichedRouteRepository;
    }

	
	//@Cacheable Is there a way to cache for certain values? Yes, could change to args Trip_id, LineId, etc
	public List<Routes_enriched> getEstimateRouteForGps(BusGPS busGPS){
		
		final String lineId = busGPS.getLineId();
		
		final String vehicleJourneyId = busGPS.getVehicleJourneyId();

		//Check if route is cached
		if(redisService.isOnWhiteList(lineId, vehicleJourneyId)){
			return redisService.getRoute(lineId, vehicleJourneyId);
		}

		//Else query repository for vehicleJourneyId
		List<Routes_enriched> stopsForTrip = enrichedEnrichedRouteRepository.findByTripId(vehicleJourneyId);

		//Sort on distance travelled
		stopsForTrip.sort(new Comparator<Routes_enriched>()
			  {
				  @Override
				  public int compare(Routes_enriched x, Routes_enriched y){
					  if(x.getDistance_travlled() > y.getDistance_travlled()){
						  return 1;
					  }
					  else{
						  return 0;
					  }
				  }
			  }
		);


		Routes_enriched sampleRoute =  stopsForTrip.get(0);
		String routeName  = sampleRoute.getRouteShortName();

		//Do sanity check
		if(routeName.equals(lineId)){

			redisService.cacheRoute(lineId, vehicleJourneyId, stopsForTrip);

			return stopsForTrip;
		}
		//GPS Data is incorrect
		else{
			redisService.addBusToBlackList(lineId+"."+vehicleJourneyId);
			return null;

			//Lets take a guess
			/*
			TODO Following service takes a guess but is very inefficient for now
			//We check if line id matches.
			//Trip_id on gps data can be wrong.
			//If so, we search enriched route repo by line id, and pick the set that is most likely.
			//How to establish most likely?
			//Closest in time?
			List<Routes_enriched> topRoute =  enrichedEnrichedRouteRepository.findByRouteShortName(lineId, busGPS.getCoordinates().getX(), busGPS.getCoordinates().getY(), busGPS.getTimestamp()/1000000);
			
			if(topRoute.size()>0){
				return enrichedEnrichedRouteRepository.findByTripId(topRoute.get(0).getTripId());
			}
			else{
				//TODO Push to Black List and return null
				redisService.addBusToBlackList(lineId);
				return null;
			}
			*/
		}
		
		

		
		
	}
	
}
