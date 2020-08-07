package br.com.codevelopment.microservices.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.zuul.context.RequestContext;
import com.nimbusds.jwt.SignedJWT;

import br.com.codevelopment.microservices.common.property.JwtConfiguration;
import br.com.codevelopment.microservices.token.converter.TokenConverter;
import br.com.codevelopment.microservices.token.security.JwtTokenAuthorizationFilter;
import br.com.codevelopment.microservices.token.security.util.SecurityContextUtil;
import lombok.SneakyThrows;

public class GatewayJWTTokenAuthorizationFilter extends JwtTokenAuthorizationFilter{

	
	public GatewayJWTTokenAuthorizationFilter(JwtConfiguration jwtconfiguration, TokenConverter tokenConverter) {
		super(jwtconfiguration, tokenConverter);
	}

	@Override
	@SneakyThrows
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(jwtconfiguration.getHeader().getName()) == null ? "" : request.getHeader(jwtconfiguration.getHeader().getName());
		if (!header.startsWith(jwtconfiguration.getHeader().getPrefix())) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = header.replace(jwtconfiguration.getHeader().getPrefix(), "").trim();
		
		String signedToken = tokenConverter.decryptToken(token);
		
		tokenConverter.validateTokenSignature(signedToken);
		
		SecurityContextUtil.setSecurityContext(SignedJWT.parse(signedToken));
		
		if (jwtconfiguration.getType().equalsIgnoreCase("signed")) {
			RequestContext.getCurrentContext().addZuulRequestHeader("Authorization", jwtconfiguration.getHeader().getPrefix() + signedToken);
		}
		
		filterChain.doFilter(request, response);
	}
}
