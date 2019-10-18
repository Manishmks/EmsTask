package emstask.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@EnableSwagger2
@Configuration
public class SwaggerConfig
{
    @Bean
    public Docket productApi()
    {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("emstask.spring"))
                .paths(PathSelectors.regex("/rest.*"))
                .build()
                .apiInfo(metaInfo());
    }

    private ApiInfo metaInfo()
    {
        return new ApiInfo(
                "EMS Spring Project",
                "EMS API to access and Manipulate data",
                "1.0",
                "Terms of service",
                new Contact("Manish Sharma", "manishksharma@fico.com", "manishmks11496@gmail.com"),
                "License of API", "API license URL", Collections.emptyList());
    }
}
