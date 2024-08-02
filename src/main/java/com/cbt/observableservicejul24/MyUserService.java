package com.cbt.observableservicejul24;

import com.netflix.discovery.converters.Auto;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

@Service
public class MyUserService {

    @Autowired
    WebClient webClient;

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
        String response = webClient.get().
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

}
