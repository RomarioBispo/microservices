package br.com.codevelopment.microservices.endpoint.service;

import org.springframework.data.domain.Pageable;

import br.com.codevelopment.microservices.endpoint.domain.model.Course;

public interface CourseService {
	public Iterable<Course> listCourses(Pageable pageable);
}
