package br.com.codevelopment.microservices.core.service;

import org.springframework.data.domain.Pageable;

import br.com.codevelopment.microservices.common.domain.model.Course;

public interface CourseService {
	public Iterable<Course> listCourses(Pageable pageable);
}
