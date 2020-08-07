package br.com.codevelopment.microservices.token.security.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import br.com.codevelopment.microservices.common.domain.model.ApplicationUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityContextUtil {
	private SecurityContextUtil() {
		
	}
	
	public static void setSecurityContext(SignedJWT signedJWT) {
		
		try {
			JWTClaimsSet claimset = signedJWT.getJWTClaimsSet();
			String username = claimset.getSubject();
			if (username == null) {
				throw new JOSEException("Username not found in JWT");
			}
			List<String> authorities = claimset.getStringListClaim("authorities");
			
			ApplicationUser applicationUser = ApplicationUser
					.builder()
					.id(claimset.getLongClaim("userId"))
					.username(username)
					.role(String.join(",", authorities))
					.build();
			
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(applicationUser, null, createAuthorities(authorities));
			auth.setDetails(signedJWT.serialize());
			SecurityContextHolder.getContext().setAuthentication(auth);
			
		} catch(Exception e) {
			log.error("Failed to set security context: {}", e);
			SecurityContextHolder.clearContext();
		}
		
	}
	
	private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {
		return authorities
				.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}
}
