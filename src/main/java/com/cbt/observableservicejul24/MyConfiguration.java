package com.cbt.observableservicejul24;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
class MyConfiguration {
    // To have the @Observed support we need to register this aspect
    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }


    @Bean
    public WebClient webClient_1(WebClient.Builder webClientBuilder)
    {
        return webClientBuilder
                .baseUrl("http://localhost:8072/observable-service-peer/hello")
                .filter(new LoggingWebClientFilter())
                .build();
    }

    @Bean
    public WebClient webClient_2(WebClient.Builder webClientBuilder)
    {
        return webClientBuilder
                .baseUrl("http://localhost:8072/observable-service-peer/async-hello")
                .filter(new LoggingWebClientFilter())
                .build();
    }

}
