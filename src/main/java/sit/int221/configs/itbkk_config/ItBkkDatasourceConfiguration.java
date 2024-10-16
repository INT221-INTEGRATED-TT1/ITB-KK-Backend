package sit.int221.configs.itbkk_config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ItBkkDatasourceConfiguration {

    @ConfigurationProperties("spring.datasource.itbkk-shared")
    @Bean
    public DataSourceProperties itBkkDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource itBkkDataSource(){
        return itBkkDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
