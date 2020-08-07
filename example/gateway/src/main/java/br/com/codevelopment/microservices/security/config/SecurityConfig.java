package br.com.codevelopment.microservices.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.codevelopment.microservices.common.property.JwtConfiguration;
import br.com.codevelopment.microservices.security.filter.GatewayJWTTokenAuthorizationFilter;
import br.com.codevelopment.microservices.token.converter.TokenConverter;
import br.com.codevelopment.microservices.token.security.config.SecurityTokenConfig;

@EnableWebSecurity
public class SecurityConfig extends SecurityTokenConfig{
	private final TokenConverter tokenConverter;

	public SecurityConfig(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
		super(jwtConfiguration);
		this.tokenConverter = tokenConverter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(new GatewayJWTTokenAuthorizationFilter(jwtConfiguration, tokenConverter), UsernamePasswordAuthenticationFilter.class);
		super.configure(http);
	}
	
	

}
