package sit.int221.configs.primarydbconfig;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class PrimaryDatasourceConfiguration {

    @ConfigurationProperties("spring.datasource.primary")
    @Bean
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource primaryDataSource(){
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
