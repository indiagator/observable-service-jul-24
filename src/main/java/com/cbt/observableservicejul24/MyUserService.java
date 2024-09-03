package com.cbt.observableservicejul24;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MyUserService
{

    @Autowired
    @Qualifier("webClient_1")
    WebClient webClient_1;

    @Autowired
    @Qualifier("webClient_2")
    WebClient webClient_2;

    private static final Logger log = LoggerFactory.getLogger(MyUserService.class);

    private final Random random = new Random();

    // Example of using an annotation to observe methods
    // <user.name> will be used as a metric name
    // <getting-user-name> will be used as a span  name
    // <userType=userType2> will be set as a tag for both metric & span
    @Observed(name = "user.name",
            contextualName = "getting-user-name",
            lowCardinalityKeyValues = {"userType", "userType2"})
    String userName(String userId)
    {
        String response = webClient_1.get().
                retrieve().bodyToMono(String.class).block(); // SYNCHRONOUS
        log.info(response+" from the peer service");
        try
        {
            Thread.sleep(random.nextLong(200L)); // simulates latency
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return "This is from OBSERVABLE | "+response;
    }

    @Observed(name = "user.name",
            contextualName = "getting-user-name",
            lowCardinalityKeyValues = {"userType", "userType2"})
    String getUserId(String userId)
    {
        Mono<String> responseMono = webClient_2.get().
                retrieve().bodyToMono(String.class); // ASYNCHRONOUS

        final String[] finalResponse = {null};

        responseMono.subscribe(
                response -> {
                                 log.info(response+" from the peer service");
                                 finalResponse[0] = response;
                            },
                error ->
                            {
                                log.info("error processing the response "+error);
                            });


        try
        {
            Thread.sleep(random.nextLong(200L)); // simulates latency
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return "This is from OBSERVABLE | "+finalResponse[0];
    }

}
