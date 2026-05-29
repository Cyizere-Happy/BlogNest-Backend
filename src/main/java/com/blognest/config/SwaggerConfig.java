package com.blognest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI blogNestOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("BlogNest API")
                        .version("1.0.0")
                        .description(
                                "BlogNest — Creative Writing & Competition Platform\n\n" +
                                "A platform for writers to publish articles, participate in competitions, " +
                                "subscribe to authors, and engage with the community."
                        )
                        .contact(new Contact()
                                .name("BlogNest Team")
                                .email("admin@blognest.com")
                        )
                )
                .tags(List.of(
                        new Tag().name("Users").description("User management"),
                        new Tag().name("Articles").description("Article creation and browsing"),
                        new Tag().name("Comments").description("Article comments"),
                        new Tag().name("Bookmarks").description("User bookmarks"),
                        new Tag().name("Subscriptions").description("Writer subscriptions"),
                        new Tag().name("Notifications").description("In-app notifications"),
                        new Tag().name("Writer Applications").description("Apply to become a writer"),
                        new Tag().name("Competitions").description("Writing competitions"),
                        new Tag().name("Submissions").description("Competition submissions"),
                        new Tag().name("Leaderboard").description("Competition rankings"),
                        new Tag().name("Daily Messages").description("Admin daily broadcasts"),
                        new Tag().name("Invites").description("Admin invite system"),
                        new Tag().name("Admin").description("Admin dashboard")
                ));
    }
}
