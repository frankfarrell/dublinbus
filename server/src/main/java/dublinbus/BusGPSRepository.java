package dublinbus;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusGPSRepository  extends CrudRepository<BusGPS,Long>{
}