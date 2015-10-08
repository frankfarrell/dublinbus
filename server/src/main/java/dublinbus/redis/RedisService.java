package dublinbus.redis;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.vividsolutions.jts.geom.Point;

import dublinbus.entities.BusDelayed;
import dublinbus.entities.BusGPS;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, BusDelayed> redisTemplate;

	/*
	 * We manage a list of lineId_tripId, with data in the list
	 */
	// inject the template as ListOperations
	//@Resource(name="redisTemplate")
	//private ListOperations<String, String> listOps;
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("busdelayed.*"));

		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage");
		adapter.setSerializer(new JacksonJsonRedisSerializer< BusDelayed >(BusDelayed.class));
		return adapter;
	}

	@Bean
	Receiver receiver(SocketIOServer webSocketServer) {
		return new Receiver(webSocketServer);
	}
	
	@Bean
    public SocketIOServer webSocketServer() {

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
 
        return server;

    }
	
	public void sendRedisMessage(String lineId, String tripId, double delay, Point coordinates){
		//listOps.rightPush(userId, url.toExternalForm());
		
		//Is there a way to filter on this in the service, eg so that we only send over socket data about buses of interest?
		redisTemplate.convertAndSend("busdelayed."+lineId, new BusDelayed(coordinates, delay, lineId, tripId));
	}
}