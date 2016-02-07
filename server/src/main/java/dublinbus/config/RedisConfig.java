package dublinbus.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dublinbus.entities.Routes_enriched;

import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import dublinbus.entities.BusDelayed;

import java.util.ArrayList;
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
		template.setHashValueSerializer(new Jackson2JsonRedisSerializer<ArrayList>(ArrayList.class){
			@Override
			protected JavaType getJavaType(Class<?> clazz){
				if (List.class.isAssignableFrom(clazz)) {
					ObjectMapper mapper = new ObjectMapper();
					setObjectMapper(mapper);
					return mapper.getTypeFactory().constructCollectionType(ArrayList.class, Routes_enriched.class);
				} else {
					return super.getJavaType(clazz);
				}
			}
		});
		template.setValueSerializer(new Jackson2JsonRedisSerializer<ArrayList>(ArrayList.class){
			@Override
			protected JavaType getJavaType(Class<?> clazz){
				if (List.class.isAssignableFrom(clazz)) {
					ObjectMapper mapper = new ObjectMapper();
					setObjectMapper(mapper);
					return mapper.getTypeFactory().constructCollectionType(ArrayList.class, Routes_enriched.class);
				} else {
					return super.getJavaType(clazz);
				}
			}
		} );

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
