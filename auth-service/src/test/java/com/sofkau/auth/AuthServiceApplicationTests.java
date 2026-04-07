package com.sofkau.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
