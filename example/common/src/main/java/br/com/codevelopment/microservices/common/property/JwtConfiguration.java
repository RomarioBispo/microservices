package br.com.codevelopment.microservices.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jwt.config")
@Data
public class JwtConfiguration {
	private String loginUrl = "/login/**";
	private int expiration = 3600;
	private String key = "uUgNNocYCrPmQUgQiyVPvEum25MZ8npR";
	private String type = "encrypted";
	@NestedConfigurationProperty
	private Header header = new Header();
	
	@Data
	public static class Header {
		private String name = "Authorization";
		private String prefix = "Bearer ";
	}
}
