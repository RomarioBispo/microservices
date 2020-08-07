package br.com.codevelopment.microservices.token.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.support.RequestContext;

import com.nimbusds.jwt.SignedJWT;

import br.com.codevelopment.microservices.common.property.JwtConfiguration;
import br.com.codevelopment.microservices.token.converter.TokenConverter;
import br.com.codevelopment.microservices.token.security.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {
	
	protected final JwtConfiguration jwtconfiguration;
	
	protected final TokenConverter tokenConverter;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(jwtconfiguration.getHeader().getName()) == null ? "" : request.getHeader(jwtconfiguration.getHeader().getName());
		if (!header.startsWith(jwtconfiguration.getHeader().getPrefix())) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = header.replace(jwtconfiguration.getHeader().getPrefix(), "").trim();
		if (StringUtils.equalsIgnoreCase("signed", jwtconfiguration.getType())){
			SecurityContextUtil.setSecurityContext(validate(token));
		} else {
			SecurityContextUtil.setSecurityContext(decryptValidating(token));
		}
		
		filterChain.doFilter(request, response);
	}
	
	@SneakyThrows
	private SignedJWT decryptValidating(String encryptedToken) {
		String signedToken = tokenConverter.decryptToken(encryptedToken);
		tokenConverter.validateTokenSignature(signedToken);
		return SignedJWT.parse(signedToken);
	}
	
	@SneakyThrows
	private SignedJWT validate(String signedToken) {
		tokenConverter.validateTokenSignature(signedToken);
		return SignedJWT.parse(signedToken);
	}

}
