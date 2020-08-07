package br.com.codevelopment.microservices.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.codevelopment.microservices.common.property.JwtConfiguration;
import br.com.codevelopment.microservices.security.filter.JwtUserNamePasswordAuthenticationFilter;
import br.com.codevelopment.microservices.token.converter.TokenConverter;
import br.com.codevelopment.microservices.token.creator.TokenCreator;
import br.com.codevelopment.microservices.token.security.JwtTokenAuthorizationFilter;
import br.com.codevelopment.microservices.token.security.config.SecurityTokenConfig;

@EnableWebSecurity
public class SecurityCredentialsConfig extends SecurityTokenConfig{
	
	private UserDetailsService service;
	private TokenCreator tokenCreator;
	private TokenConverter tokenConverter;
	
	
	public SecurityCredentialsConfig(JwtConfiguration jwtConfiguration, UserDetailsService service,
			TokenCreator tokenCreator, TokenConverter tokenConverter) {
		super(jwtConfiguration);
		this.service = service;
		this.tokenCreator = tokenCreator;
		this.tokenConverter = tokenConverter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.addFilter(new JwtUserNamePasswordAuthenticationFilter(authenticationManager(),
					jwtConfiguration, tokenCreator))
		.addFilterAfter(new JwtTokenAuthorizationFilter(jwtConfiguration,
			tokenConverter), UsernamePasswordAuthenticationFilter.class);
	super.configure(http);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(service).passwordEncoder(passwordEncoder());
	}
	
	@Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
