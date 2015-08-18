package dublinbus;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import dublinbus.entities.Route_materialized;

@RepositoryRestResource
public interface RouteRepository extends CrudRepository<Route_materialized,String> {

}
