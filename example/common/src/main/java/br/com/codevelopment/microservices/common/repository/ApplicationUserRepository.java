package br.com.codevelopment.microservices.common.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import br.com.codevelopment.microservices.common.domain.model.ApplicationUser;

@Repository
public interface ApplicationUserRepository extends PagingAndSortingRepository<ApplicationUser, Long> {
	public ApplicationUser findByUserName(String username);
}
