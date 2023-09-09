package com.moreffnest.anonchat.unit.models;

import com.moreffnest.anonchat.models.Application;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    static List<Application> applicationList() {
        return List.of(new Application("male", 21,
                        "male", new Application.IntRange(18, 29), "2"),
                new Application("female", 26,
                        "male", new Application.IntRange(24, 31), "3"),
                new Application("no", 34,
                        "male", new Application.IntRange(25, 40), "4"),
                new Application("no", 29,
                        "no", new Application.IntRange(29, 29), "5"));
    }

    @ParameterizedTest
    @MethodSource("applicationList")
    void matches(Application otherApplication) {
        Application application = new Application("male", 29,
                "no", new Application.IntRange(21, 34), "1");
        assertTrue(application.matches(otherApplication));
    }
}