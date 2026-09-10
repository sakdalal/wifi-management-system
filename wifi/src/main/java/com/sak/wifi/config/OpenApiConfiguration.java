package com.sak.wifi.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfiguration {

    @Bean
    public OperationCustomizer companyIdHeaderCustomizer() {

        return (operation, handlerMethod) -> {

            String controllerName =
                    handlerMethod.getBeanType().getSimpleName();

            if (controllerName.equals("CompanyController")) {
                return operation;
            }

            operation.addParametersItem(
                    new Parameter()
                            .in("header")
                            .name("X-Company-Id")
                            .description("Company ID for tenant isolation")
                            .required(true)
            );

            return operation;
        };

    }
}
