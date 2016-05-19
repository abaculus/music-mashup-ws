Prerequisite
--------------------

 Maven 3+
 Java 8


Building
--------------------

 mvn clean install


Running
--------------------

 mvn clean install spring-boot:run

   or (after building)

 java -jar target/music-1.0.0-SNAPSHOT.jar


CHOICES MADE
--------------------

 - Spring Boot
 - Spring Cloud Hystrix facing 3rd party (MusicBrainz, Wikipedia, CoverArt) mostly because nice fallback mechanism
 - API versioning in URL instead of HTTP headers
 - Custom JSON error object (could have used Spring default and restricted info though)
 - Caching of response (artist descriptions and released albums don't tend to change much)
 - Async calls towards Wikipedia and CoverArt
 - If failing request towards 3rd party, always an "empty response" returns (not good, but ok)
 - Logging pattern key/value (best practice for e.g. Splunk)
 - Keeping internal model separated from external model (could have used @JsonView on internal model for external serialization, but got messy so I skipped it)
 - slf4j/logback logging
 - Simple HC Fluent clients (if necessary of more delicate config one would prefer other client)
 - Boiler-plate validation (could have used JSR-303)



TODO
--------------------

 - More unit and integration tests (verify json paths, exception handling etc)
 - Javadoc
 - Maven Site
 - Exception handling/error codes
 - Could make use of edge/dispatcher in front of application using Eureka/Zuul/Ribbon etc for clustering/client-loadbalancing.
 - Current cache provider is very simple, no TTLs etc
 - Additional metrics
 - Export metrics to Graphite, InfluxDB etc
 - Run som more perf testing; quick JMeter test showed difference using (in favor of) not using DeferredResult or Callable in controller

