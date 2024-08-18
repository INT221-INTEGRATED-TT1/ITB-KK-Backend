package sit.int221.configs.secondarydbconfig;


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
        basePackages = "sit.int221.repositories.secondary",
        entityManagerFactoryRef = "secondaryEntityManagerFactoryBean",
        transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryJPAConfiguration {

    @Bean
    LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactoryBean(EntityManagerFactoryBuilder entityManagerFactoryBuilder,
                                                                             @Qualifier("secondaryDataSource") DataSource dataSource) {

        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("sit.int221.entities.secondary")
                .build();
    }

    @Bean
    PlatformTransactionManager secondaryTransactionManager(@Qualifier("secondaryEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean emfb) {
        return new JpaTransactionManager(emfb.getObject());
    }
}
