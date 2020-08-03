package br.com.codevelopment.microservices.core.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.codevelopment.microservices.common.domain.model.Course;
import br.com.codevelopment.microservices.core.service.CourseService;

@RestController
@RequestMapping("/api/v1/course")
public class CourseController {
	
	private CourseService service;
	
	public CourseController(CourseService service) {
		super();
		this.service = service;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Course>> listCourses(Pageable pageable) {
		return new ResponseEntity<>(service.listCourses(pageable), HttpStatus.OK);
	}
}
