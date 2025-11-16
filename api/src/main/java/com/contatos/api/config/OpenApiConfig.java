package com.contatos.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI/Swagger para documentação interativa da API
 * Acesse a documentação em: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Contatos")
                        .description("""
                            Sistema completo de cadastro e gerenciamento de contatos com funcionalidades de:
                            
                            • **Autenticação JWT** - Login seguro e registro de usuários
                            • **CRUD de Contatos** - Criação, leitura, atualização e exclusão com paginação
                            • **Integração ViaCEP** - Busca automática de endereços por CEP
                            • **Geolocalização** - Coordenadas automáticas via Google Maps API
                            • **Validação de CPF** - Validação única por usuário
                            • **Busca e Filtros** - Pesquisa por nome ou CPF
                            
                            ## Como usar
                            1. Registre-se usando o endpoint `/api/auth/registro`
                            2. Faça login em `/api/auth/login` para obter o token JWT
                            3. Clique no botão "Authorize" e insira o token no formato: `Bearer seu-token-aqui`
                            4. Agora você pode usar todos os endpoints protegidos
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Suporte API Contatos")
                                .email("suporte@contatos.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de Desenvolvimento"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de Produção (configurar URL)"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtido no endpoint de login. Use o formato: Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
