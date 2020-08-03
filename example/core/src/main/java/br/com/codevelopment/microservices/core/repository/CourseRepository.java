package br.com.codevelopment.microservices.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import br.com.codevelopment.microservices.common.domain.model.Course;

@Repository
public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {

}
