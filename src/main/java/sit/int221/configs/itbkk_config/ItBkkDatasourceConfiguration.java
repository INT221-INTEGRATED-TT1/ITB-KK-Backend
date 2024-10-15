package sit.int221.configs.itbkk_config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SecondaryDatasourceConfiguration {

    @ConfigurationProperties("spring.datasource.itbkk-shared")
    @Bean
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean
    public DataSource secondaryDataSource(){
        return secondaryDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
