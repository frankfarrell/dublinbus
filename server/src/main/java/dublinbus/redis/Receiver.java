package dublinbus.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import reactor.core.support.MultiValueMap;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import dublinbus.entities.BusDelayed;

public class Receiver{
 
	@Autowired
	public Receiver(SocketIOServer server) {
		this.server = server;
    }
    
	private SocketIOServer server; 

	
    @PostConstruct
    public void start() {
        System.out.println("starting socket server");
        
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
            	
            	
            	//clients.add(client);
            }
        });
        
        server.addEventListener("subscribebus", String.class, new DataListener<String>() {
      
			@Override
			public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
				
				HashSet<SocketIOClient> listOfClients = clients.get(data);
				//etc here
				if(listOfClients == null){
					listOfClients = new HashSet<SocketIOClient>();		
				}
				listOfClients.add(client);
				clients.put(data, listOfClients);
				
				ackSender.sendAckData();
			}
        });

        
        server.addDisconnectListener(new DisconnectListener() {

			@Override
			public void onDisconnect(SocketIOClient client) {
				//clients.remove(client);
			}
        });
        
        server.start();
    }

    @PreDestroy
    public void stop() {
    	System.out.println("stopping socket server");
        server.stop();
    }

	private HashMap<String, HashSet<SocketIOClient>> clients = new HashMap<>();

	/*
	 * Clients and filters, filters are line Id
	 */
	
    public void receiveMessage(BusDelayed busInstance) {
    	
    	String lineId = busInstance.getLineId();
    	
    	HashSet<SocketIOClient> clientSet =  clients.get(lineId);

    	if(clientSet != null){
    		for(SocketIOClient client : clientSet){
    			client.sendEvent("busDelayed", busInstance);
    		}
    	}
    	
    }
    
}
