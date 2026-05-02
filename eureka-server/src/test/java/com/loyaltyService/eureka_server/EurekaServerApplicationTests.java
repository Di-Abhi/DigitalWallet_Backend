package com.loyaltyService.eureka_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"server.port=0",
		"eureka.client.register-with-eureka=false",
		"eureka.client.fetch-registry=false",
		"management.tracing.enabled=false",
		"management.tracing.export.zipkin.enabled=false"
})
class EurekaServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
