package vn.ttg.roadmap.weatherapiservice.conf;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for RestTemplate bean with custom timeouts.
 *
 *  @author ttg
 */
@Configuration
public class RestTemplateConfig {

    @Value("${weather.api.connect-timeout}")
    private Duration connectTimeout;

    @Value("${weather.api.read-timeout}")
    private Duration readTimeout;

    @Bean
    RestTemplate restTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder.connectTimeout(connectTimeout);
        builder.readTimeout(readTimeout);
        return builder.build();
    }

}
