package com.loyaltyService.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"jwt.secret=dGVzdC1qd3Qtc2VjcmV0LWtleS1mb3ItYXBpLWdhdGV3YXktdGVzdHM=",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"management.tracing.enabled=false",
		"management.tracing.export.zipkin.enabled=false"
})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
