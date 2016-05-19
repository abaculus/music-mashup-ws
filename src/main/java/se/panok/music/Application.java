package se.panok.music;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

@EnableCircuitBreaker
@SpringBootApplication
@EnableCaching
@ComponentScan(excludeFilters = @Filter(BeanMock.class))
public class Application {

	public static final String CACHE_ARTISTS = "artists";

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager(CACHE_ARTISTS);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
