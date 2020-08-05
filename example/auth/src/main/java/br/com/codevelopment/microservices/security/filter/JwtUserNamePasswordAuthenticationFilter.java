package br.com.codevelopment.microservices.security.filter;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import br.com.codevelopment.microservices.common.domain.model.ApplicationUser;
import br.com.codevelopment.microservices.common.property.JwtConfiguration;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class JwtUserNamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter{ 
	private AuthenticationManager authenticationManager;
	private JwtConfiguration jwtConfiguration;
	
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
		
		SignedJWT signedJWT = createSignedToken(authResult);
		String encryptedToken = encryptToken(signedJWT);
		
		log.info("token generated: {}", encryptedToken);
		
		response.addHeader("Acess-Control-Expose-Header", "XSRF-TOKEN, " + jwtConfiguration.getHeader().getName());
		
		response.addHeader(jwtConfiguration.getHeader().getName(), jwtConfiguration.getHeader().getPrefix() + " "+ encryptedToken);
		
	}
	
	@SneakyThrows
	private SignedJWT createSignedToken(Authentication auth) {
		log.info("starting jws...");
		ApplicationUser user = (ApplicationUser) auth.getPrincipal();
		JWTClaimsSet claimSet = createJwtClain(auth, user);
		KeyPair keyPair = generateKeyPair();
		log.info("building JWK from RSA keys...");
		JWK jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).keyID(UUID.randomUUID().toString()).build();
		
		SignedJWT signedJwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
				.jwk(jwk)
				.type(JOSEObjectType.JWT)
				.build(), claimSet);
		log.info("Signing token with private key...");
		
		 RSASSASigner signer = new RSASSASigner(keyPair.getPrivate());
		 
		 signedJwt.sign(signer);
		 
		 return signedJwt;
		 
	}
	
	private JWTClaimsSet createJwtClain(Authentication auth, ApplicationUser user) {
		log.info("creating jwt clains...");
		return new JWTClaimsSet
				.Builder()
				.subject(user.getUsername())
				.claim("authorities", auth.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList()))
				.issuer("http://codevelopment.com")
				.issueTime(new Date())
				.expirationTime(new Date(System.currentTimeMillis() + jwtConfiguration.getExpiration() * 1000))
				.build();
		
	}
	
	@SneakyThrows
	private KeyPair generateKeyPair() {
		log.info("generate RSA 2048 bit keys");
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		return generator.genKeyPair();
		
	}
	
	private String encryptToken(SignedJWT signedJwt) throws JOSEException  {
		log.info("encrypting signed token...");
		DirectEncrypter encrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());
		JWEObject jweObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
				.contentType("JWT")
				.build(), new Payload(signedJwt));
		
		log.info("encrypting with private key...");
		jweObject.encrypt(encrypter);
		log.info("serialized token {}", jweObject.serialize());
		return jweObject.serialize();
	}
	
}
