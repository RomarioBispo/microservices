package br.com.codevelopment.microservices.endpoint.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.codevelopment.microservices.endpoint.domain.model.Course;

public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {

}
