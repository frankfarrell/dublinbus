package dublinbus;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.io.net.ChannelStream;
import reactor.io.net.ReactorChannelHandler;
import reactor.rx.Streams;

public class BusDataChannel implements ReactorChannelHandler<BusGPS, BusGPS, ChannelStream<BusGPS, BusGPS>>{

	
	BusGPSRepository repository;
	
	public BusDataChannel(BusGPSRepository repository){
		
		this.repository = repository;
	}
	
	
	@Override
	public Publisher<Void> apply(ChannelStream<BusGPS, BusGPS> channel) {
		channel.consume(data -> {
			
			//repository.save(data);
			
			System.out.println("Saving " + data.getJourneyPatternId());
			
		});
		return Streams.empty();
	}}
