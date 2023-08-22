package com.clepbo.hospital_management_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "hms",
                        email = "oniokikijesu04@gmail.com",
                        url = "https://www.linkedin.com/in/israel-oni-2496a1210/"
                ),
                description = "API for Hospital Management System",
                title = "Hospital Management System",
                version = "1.0"),
        security = {@SecurityRequirement(name = "basicAuth")}
)
@SecurityScheme(
        name = "basicAuth",
        description = "Basic Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "Basic",
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "basic"
)
public class OpenAPIConfig {
}
