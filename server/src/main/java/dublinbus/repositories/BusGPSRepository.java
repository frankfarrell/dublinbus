package dublinbus.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dublinbus.entities.BusGPS;

@Repository
public interface BusGPSRepository  extends CrudRepository<BusGPS,Long>{
}