package br.com.codevelopment.microservices.token.creator;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCreator {
	
	private final JwtConfiguration jwtConfiguration;
	
	@SneakyThrows
	public SignedJWT createSignedToken(Authentication auth) {
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
				.claim("userId", user.getId())
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
	
	public String encryptToken(SignedJWT signedJwt) throws JOSEException  {
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
