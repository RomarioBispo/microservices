package br.com.codevelopment.microservices.docs;


import org.springframework.context.annotation.Configuration;

import br.com.codevelopment.microservices.common.docs.BaseSwaggerConfig;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerCoreConfig extends BaseSwaggerConfig{

	public SwaggerCoreConfig() {
		super("br.com.codevelopment.microservices.web.controller");
	}

}
