package com.cbt.observableservicejul24;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
class MyController {

    private static final Logger log = LoggerFactory.getLogger(MyController.class);
    private final MyUserService myUserService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    MyController(MyUserService myUserService) {
        this.myUserService = myUserService;
    }

    @GetMapping("/user/{userId}")
    String userName(@PathVariable("userId") String userId) {
        log.info("Got a request");
        return myUserService.userName(userId);
    }

    @GetMapping("/asyncuser/{userId}")
    String getUserId(@PathVariable("userId") String userId) {
        log.info("Got a request");
        return myUserService.getUserId(userId);
    }

    @PostMapping("/save")
    public String saveObj(@RequestParam("key") String key, @RequestParam("value") String value)
    {
        redisTemplate.opsForValue().set(key, value);
        return "success";
    }

    @GetMapping("/getip")
    public ResponseEntity<String> handleRequest(@RequestHeader("X-Forwarded-For") String clientIpAddress, HttpServletRequest request)
    {
        if (clientIpAddress == null)
        {
            clientIpAddress = request.getRemoteAddr();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie_Session", "etete5646456456");


        // Use the clientIpAddress value to identify the client
        return ResponseEntity.ok().headers(headers).body("Set The Cookie");
    }



}