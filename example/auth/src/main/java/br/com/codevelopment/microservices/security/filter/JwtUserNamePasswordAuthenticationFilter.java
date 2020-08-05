package br.com.codevelopment.microservices.security.filter;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;

import br.com.codevelopment.microservices.common.domain.model.ApplicationUser;
import br.com.codevelopment.microservices.common.property.JwtConfiguration;
import br.com.codevelopment.microservices.token.creator.TokenCreator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class JwtUserNamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter{ 
	private AuthenticationManager authenticationManager;
	private JwtConfiguration jwtConfiguration;
	private final TokenCreator tokenCreator;
	
	@Override
	@SneakyThrows
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		log.info("Attempting authentication...");
		ApplicationUser applicationUser = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
		if (applicationUser == null) {
			throw new UsernameNotFoundException("User not found"); 
		}
		
		log.info("creating authentication...");
		UsernamePasswordAuthenticationToken userAuthenticationToken = new UsernamePasswordAuthenticationToken(applicationUser.getUsername(), applicationUser.getPassword(), Collections.emptyList());
		userAuthenticationToken.setDetails(applicationUser);
		return authenticationManager.authenticate(userAuthenticationToken);
	}

	@Override
	@SneakyThrows
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		log.info("Authentication successfull..., generating token {}", authResult.getName());
		
		SignedJWT signedJWT = tokenCreator.createSignedToken(authResult);
		String encryptedToken = tokenCreator.encryptToken(signedJWT);
		
		log.info("token generated: {}", encryptedToken);
		
		response.addHeader("Acess-Control-Expose-Header", "XSRF-TOKEN, " + jwtConfiguration.getHeader().getName());
		
		response.addHeader(jwtConfiguration.getHeader().getName(), jwtConfiguration.getHeader().getPrefix() + " "+ encryptedToken);
		
	}
}
