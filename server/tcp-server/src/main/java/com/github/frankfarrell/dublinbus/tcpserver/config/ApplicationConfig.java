package com.github.frankfarrell.dublinbus.tcpserver.config;

import io.netty.handler.codec.string.StringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class ApplicationConfig {

    @Bean
    public StringDecoder stringDecoder(){
        return new StringDecoder();
    }

}
