package dublinbus;

import dublinbus.entities.BusGPS;
import reactor.Environment;
import reactor.fn.Consumer;
import reactor.io.buffer.Buffer;
import reactor.io.codec.json.JsonCodec;
import reactor.io.net.ChannelStream;
import reactor.io.net.NetStreams;
import reactor.io.net.ReactorChannelHandler;
import reactor.io.net.tcp.TcpServer;
import reactor.rx.Streams;

import java.util.concurrent.*;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

@SpringBootApplication
public class Application implements CommandLineRunner {
	
	
	@Autowired
	BusGPSRepository repository;
	
	@Autowired
	BusGPSRealtimeService realtimeService;
	
	
	public static void main(String[] args) throws InterruptedException{
		
		SpringApplication.run(Application.class, args);
		
	}

	@Override
	public void run(String... args) throws Exception {
		CountDownLatch latch = new CountDownLatch(10);
		
		final Logger logger = LoggerFactory.getLogger(Application.class);
		
		Environment env = Environment.initializeIfEmpty().assignErrorJournal();
		
		JsonCodec<BusGPS, BusGPS> codec = new JsonCodec<BusGPS, BusGPS>(BusGPS.class);
		
		final TcpServer<Buffer, Buffer> server = NetStreams.tcpServer(s ->
							s
								.env(env)
								.listen("127.0.0.1",5001)
								.dispatcher(Environment.cachedDispatcher())
		);
		
		final CountDownLatch countDownLatch = new CountDownLatch(10);
		
		server.start(input -> {
		        input
		        	.decode(codec)
		        	.consume( data -> {
		        		//I think this means that the bus is not in service, ie its isen route to destination start, for instance in the morning at 6am its common
		        		if(!data.getJourneyPatternId().equals("null")){
		        			repository.save(data); 
		        		try {
							realtimeService.submityBusGPS(data);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		}
		        		
		        	});
		        return Streams.never();
	
		}).await();

		countDownLatch.await();
		
	}

}

/*
 * ch -> {
    ch.consume(trip -> {
        trips.onNext(trip);
    });
    return Streams.never();
 */

/*
new Consumer<ChannelStream<BusGPS, BusGPS>>() {
@Override
public void accept(ChannelStream<BusGPS,BusGPS> channel) {
    channel.log("channel").consume(new Consumer<BusGPS>() {
        @Override
        public void accept(A data) {
            System.out.printf("Receiving data from client -> %s on thread %s%n", data.getId(), Thread.currentThread());
        }
    });
}
}
*/