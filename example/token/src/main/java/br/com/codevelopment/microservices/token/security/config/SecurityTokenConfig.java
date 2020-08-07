package br.com.codevelopment.microservices.token.security.config;


import javax.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;

import br.com.codevelopment.microservices.common.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter{
	
	protected final JwtConfiguration jwtConfiguration;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
		.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
			.exceptionHandling()
			.authenticationEntryPoint((req, resp, e ) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
		.and()
		.authorizeRequests()
			.antMatchers(jwtConfiguration.getLoginUrl()).permitAll()
			.antMatchers("/api/v1/course/**").hasRole("ADMIN")
			.anyRequest().authenticated();
	}
}
