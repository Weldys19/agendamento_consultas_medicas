package br.com.weldyscarmo.agendamento_consultas_medicas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Agendamento de consultas médicas")
                        .description("API de agendendamento para consultas médicas")
                        .version("1"))
                .schemaRequirement("jwt_auth", creatSecurityScheme());
    }

    private SecurityScheme creatSecurityScheme(){
        return new SecurityScheme().name("jwt_auth")
                .scheme("bearer").bearerFormat("JWT").type(SecurityScheme.Type.HTTP);
    }
}
