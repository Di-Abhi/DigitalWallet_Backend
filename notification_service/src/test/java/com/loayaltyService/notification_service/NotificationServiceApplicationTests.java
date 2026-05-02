package com.loayaltyService.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"notification.kafka.enabled=false",
		"management.tracing.enabled=false",
		"management.tracing.export.zipkin.enabled=false"
})
class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
