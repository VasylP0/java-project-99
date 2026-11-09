
package hexlet.code.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"app.jwt.secret=test-secret-key-for-hexlet-application-12345678901234567890",
		"sentry.dsn="
})
class AppApplicationTests {

	@Test
	void contextLoads() {
	}
}
