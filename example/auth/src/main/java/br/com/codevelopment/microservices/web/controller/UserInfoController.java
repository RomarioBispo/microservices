package br.com.codevelopment.microservices.web.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.codevelopment.microservices.common.domain.model.ApplicationUser;

@RestController
@RequestMapping("/user")
public class UserInfoController {
	
	@GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApplicationUser> getUserInfo(Principal principal) {
		ApplicationUser user =  (ApplicationUser)((UsernamePasswordAuthenticationToken) principal).getPrincipal();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

}
