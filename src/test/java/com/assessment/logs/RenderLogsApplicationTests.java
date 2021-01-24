package com.assessment.logs;

import com.assessment.logs.controller.RenderLogsController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RenderLogsApplication.class, initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
class RenderLogsApplicationTests {

	@Autowired
	private RenderLogsController renderLogsController;

	@Test
	void contextLoads() {
		assertNotEquals(null, renderLogsController);
	}

}
