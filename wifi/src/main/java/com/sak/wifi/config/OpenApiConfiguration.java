package com.sak.wifi.config;

import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
