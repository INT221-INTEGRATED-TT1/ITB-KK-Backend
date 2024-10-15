package sit.int221.configs.task_base_config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class TaskBaseDatasourceConfiguration {

    @ConfigurationProperties("spring.datasource.task-base")
    @Bean
    public DataSourceProperties taskBaseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource taskBaseDataSource(){
        return taskBaseDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
