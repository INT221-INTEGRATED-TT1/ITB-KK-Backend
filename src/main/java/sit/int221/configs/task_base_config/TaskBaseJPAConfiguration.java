package sit.int221.configs.taskbase_config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
@EnableJpaRepositories(
        basePackages = "sit.int221.repositories.task_base",
        entityManagerFactoryRef = "taskBaseEntityManagerFactoryBean",
        transactionManagerRef = "taskBaseTransactionManager"
)
public class TaskBaseJPAConfiguration {
    @Bean
    LocalContainerEntityManagerFactoryBean taskBaseEntityManagerFactoryBean(EntityManagerFactoryBuilder entityManagerFactoryBuilder,
                                                                            @Qualifier("taskBaseDataSource") DataSource dataSource) {
        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("sit.int221.entities.task_base")
                .build();
    }

    @Bean
    PlatformTransactionManager taskBaseTransactionManager(@Qualifier("taskBaseEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean emfb) {
        return new JpaTransactionManager(emfb.getObject());
    }
}
