package br.com.codevelopment.microservices.core.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.codevelopment.microservices.common.domain.model.Course;
import br.com.codevelopment.microservices.core.repository.CourseRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService{
	private CourseRepository repository;

	public CourseServiceImpl(CourseRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Iterable<Course> listCourses(Pageable pageable) {
		log.info("listing all courses");
		return repository.findAll(pageable);
	}
	
}
