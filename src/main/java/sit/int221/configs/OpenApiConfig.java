package sit.int221.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Task API",
                description = "Doing CRUD Operation",
                summary = "This task-api will add, delete, read, update",
                termsOfService = "HEHEBOI",
                contact = @Contact(
                        name = "Chob",
                        email = "chob1234@email.com"
                ),
                license = @License(
                        name = "YOUR LICENSE NO"
                ),
                version = "v0"
        )
)
public class OpenApiConfig {
}
