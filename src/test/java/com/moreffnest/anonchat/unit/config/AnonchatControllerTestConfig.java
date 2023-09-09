package com.moreffnest.anonchat.unit.config;

import com.moreffnest.anonchat.models.Application;
import com.moreffnest.anonchat.services.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@TestConfiguration
public class AnonchatControllerTestConfig {
    @MockBean
    public UserService userService;
    @Bean
    @Primary
    public ConcurrentLinkedQueue<Application> userQueueTest() {
        return new ConcurrentLinkedQueue<>(List.of(
                new Application("no", 22, "no",
                        new Application.IntRange(20, 25), "")
        ));
    }


}
