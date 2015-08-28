package dublinbus.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import dublinbus.entities.BusDelayed;

@Configuration
@EnableAutoConfiguration
public class RedisConfig {

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	RedisTemplate< String, BusDelayed > redisTemplate() {
		final RedisTemplate< String, BusDelayed > template =  new RedisTemplate< String, BusDelayed >();
		template.setConnectionFactory( jedisConnectionFactory() );
		template.setKeySerializer( new StringRedisSerializer() );
		template.setHashValueSerializer(new JacksonJsonRedisSerializer< BusDelayed >(BusDelayed.class) );
		template.setValueSerializer( new JacksonJsonRedisSerializer< BusDelayed >(BusDelayed.class) );
		return template;
	}
	
}
