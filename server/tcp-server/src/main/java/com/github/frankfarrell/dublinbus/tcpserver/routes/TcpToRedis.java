package com.github.frankfarrell.dublinbus.tcpserver.routes;

import com.github.frankfarrell.dublinbus.tcpserver.Constants;
import com.github.frankfarrell.dublinbus.tcpserver.processors.GpsProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.redis.RedisConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TcpToRedis extends RouteBuilder {

    private final String host;
    private final Integer port;

    public TcpToRedis(@Value("${redis.host:localhost}")final String host,
                      @Value("${redis.port:6379 }")final Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void configure() throws Exception {
        /*
        http://shengwangi.blogspot.ie/2015/01/camel-netty-hello-world-simple-example.html
         */
        from("netty4:tcp://localhost:7000?sync=true&keepAlive=true&decoder=#stringDecoder")
                .process(new GpsProcessor())
                .multicast()
                .to("log:SimpleLog")
                .to("direct:redis");
                //.to("direct:rabbitmq");

        //https://github.com/TerrenceMiao/camel-spring/tree/master/src/main/java/org/paradise
        from("direct:redis")
                .setHeader(RedisConstants.COMMAND, constant("HSET"))
                .setHeader(RedisConstants.KEY, constant(Constants.LATEST_POSITION))
                .setHeader(RedisConstants.FIELD, header(Constants.BUS_ROUTE_ID)) //route number plus bus number
                .setHeader(RedisConstants.VALUE, header(Constants.GPS_DATA))
                .to("spring-redis://" +host +":" +port.toString());

    }
}
