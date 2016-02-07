package dublinbus.config;

import dublinbus.entities.Routes_enriched;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import dublinbus.entities.BusDelayed;

import java.util.List;

@Configuration
@EnableAutoConfiguration
public class RedisConfig {

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	RedisTemplate< String, BusDelayed > busDelatedRedisTemplate() {
		final RedisTemplate< String, BusDelayed > template =  new RedisTemplate< String, BusDelayed >();
		template.setConnectionFactory( jedisConnectionFactory() );
		template.setKeySerializer( new StringRedisSerializer() );
		template.setHashValueSerializer(new JacksonJsonRedisSerializer< BusDelayed >(BusDelayed.class) );
		template.setValueSerializer( new JacksonJsonRedisSerializer< BusDelayed >(BusDelayed.class) );
		return template;
	}

	@Bean
	RedisTemplate< String, List<Routes_enriched>> routeEnrichedRedisTemplate() {
		final RedisTemplate< String, List<Routes_enriched> > template =  new RedisTemplate< String, List<Routes_enriched> >();
		template.setConnectionFactory( jedisConnectionFactory() );
		template.setKeySerializer( new StringRedisSerializer() );
		return template;
	}

	@Bean
	RedisTemplate< String, String > lineBlacklistTemplate() {
		final RedisTemplate< String, String > template =  new RedisTemplate< String, String >();
		template.setConnectionFactory( jedisConnectionFactory() );
		template.setKeySerializer( new StringRedisSerializer() );
		template.setHashValueSerializer(new StringRedisSerializer() );
		template.setValueSerializer( new StringRedisSerializer());
		return template;
	}

	
}
