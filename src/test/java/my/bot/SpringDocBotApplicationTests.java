package my.bot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringDocBotApplicationTests.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringDocBotApplicationTests {



		@LocalServerPort           // shorthand for @Value("${local.server.port}")
		private Integer port;


		@Autowired
		private TestRestTemplate restTemplate;

		@Test
		public void contextLoads() {
			assertThat(this.restTemplate.getForObject("http://127.0.0.1:" + String.valueOf(port) + "/",
					String.class)).contains("profile");
		}

	}
