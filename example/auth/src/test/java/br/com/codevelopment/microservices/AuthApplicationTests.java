package br.com.codevelopment.microservices;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AuthApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	void shouldEncrypt() {
		System.out.println("encrypt: " + new BCryptPasswordEncoder().encode("codevelopment"));
	}

}
