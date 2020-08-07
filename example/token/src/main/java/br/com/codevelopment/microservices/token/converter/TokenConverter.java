package br.com.codevelopment.microservices.token.converter;

import java.nio.file.AccessDeniedException;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import br.com.codevelopment.microservices.common.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenConverter {
	private final JwtConfiguration jwtConfiguration;
	
	@SneakyThrows
	public String decryptToken(String encryptedToken){
		
		log.info("decrypting token...");
		JWEObject jweObject = JWEObject.parse(encryptedToken);
		
		DirectDecrypter decrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
		
		jweObject.decrypt(decrypter);
		
		log.info("token decrypted, returning...");
		return jweObject.getPayload().toSignedJWT().serialize();
	}
	
	@SneakyThrows
	public void validateTokenSignature(String signedToken) {
		log.info("validating token signature...");
		SignedJWT signedJWT = SignedJWT.parse(signedToken);
		RSAKey rsaParseKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
		log.info("RSA key retrieved... validating ");
		
		if (!signedJWT.verify(new RSASSAVerifier(rsaParseKey))) {
			throw new AccessDeniedException("invalid key...");
		}
		
		log.info("token has a valid signature...");
	}

}
