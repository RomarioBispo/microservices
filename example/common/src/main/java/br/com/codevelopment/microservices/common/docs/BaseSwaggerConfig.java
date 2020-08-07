package br.com.codevelopment.microservices.common.docs;

import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

public class BaseSwaggerConfig {
	
	private final String basePackage;
	public BaseSwaggerConfig(String basePackage) {
		super();
		this.basePackage = basePackage;
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
		.select()
		.apis(RequestHandlerSelectors.basePackage(basePackage))
		.build()
		.apiInfo(metadata());
	}
	
	private ApiInfo metadata() {
		return new ApiInfoBuilder().
				title("Courses API")
				.description("A simple API using microservices architecture")
				.contact(new Contact("codevelopment", "codevelopment.com.br", null))
				.license("This project was made on spring boot microservices course by devdojo at youtube")
				.build();
	}
}
