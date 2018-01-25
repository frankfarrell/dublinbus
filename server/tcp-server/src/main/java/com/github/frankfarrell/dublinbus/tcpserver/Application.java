package com.github.frankfarrell.dublinbus.tcpserver;

import io.netty.handler.codec.string.StringDecoder;
import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        final ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        final CamelSpringBootApplicationController applicationController =
                applicationContext.getBean(CamelSpringBootApplicationController.class);
        applicationController.run();
    }
}
