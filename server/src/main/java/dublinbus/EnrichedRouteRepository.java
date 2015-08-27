package dublinbus;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dublinbus.entities.Routes_enriched;

@RepositoryRestResource
public interface EnrichedRouteRepository extends CrudRepository<Routes_enriched, Routes_enriched.RouteId>{

	//Does this work here?
	@Cacheable
	List<Routes_enriched> findByTripId(@Param("tripId") String trip_id);
	
	@Cacheable
	@Query(value ="SELECT * FROM public.routes_enriched WHERE route_short_name =:routeShortName ORDER BY st_distance(geom, ST_SetSRID(St_MakePoint(:lon, :lat),4326)) ASC,  EXTRACT(EPOCH FROM to_timestamp(:timestamp ) - to_timestamp(timestamp ,'HH24:MI:SS'));", nativeQuery = true)
	List<Routes_enriched> findByRouteShortName(
			@Param("routeShortName") String routeShortName, 
			@Param("lon") double lonCoord, 
			@Param("lat") double latCoord, 
			@Param("timestamp") long timestamp);
	
}
