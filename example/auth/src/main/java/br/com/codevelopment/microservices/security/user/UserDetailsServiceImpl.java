package br.com.codevelopment.microservices.security.user;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.codevelopment.microservices.common.domain.model.ApplicationUser;
import br.com.codevelopment.microservices.common.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{
	
	private final ApplicationUserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		log.info("Searching user on database");
		ApplicationUser user = repository.findByusername(username);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User not found: %s", username));
		}
		return new UserDetailsCustom(user);
	}
	
	public static final class UserDetailsCustom extends ApplicationUser implements UserDetails {

		private static final long serialVersionUID = 1L;

		UserDetailsCustom(@NotNull ApplicationUser user) {
			super(user);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + this.getRole());
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

	}

}
