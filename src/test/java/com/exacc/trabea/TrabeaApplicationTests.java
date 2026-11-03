package com.exacc.trabea;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.jwt.secret=test-secret-for-jwt-signing-at-least-32-bytes-long")
class TrabeaApplicationTests {

	@Test
	void contextLoads() {
	}

}
