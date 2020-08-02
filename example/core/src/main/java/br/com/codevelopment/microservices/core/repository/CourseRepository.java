package br.com.codevelopment.microservices.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.codevelopment.microservices.common.domain.model.Course;

public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {

}
