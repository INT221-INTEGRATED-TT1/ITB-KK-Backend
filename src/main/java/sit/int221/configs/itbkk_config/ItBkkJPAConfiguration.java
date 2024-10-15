package sit.int221.configs.itbkk_config;


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
        basePackages = "sit.int221.repositories.itbkk_shared",
        entityManagerFactoryRef = "itBkkEntityManagerFactoryBean",
        transactionManagerRef = "itBkkTransactionManager"
)
public class ItBkkJPAConfiguration {

    @Bean
    LocalContainerEntityManagerFactoryBean itBkkEntityManagerFactoryBean(EntityManagerFactoryBuilder entityManagerFactoryBuilder,
                                                                             @Qualifier("itBkkDataSource") DataSource dataSource) {

        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("sit.int221.entities.itbkk_shared")
                .build();
    }

    @Bean
    PlatformTransactionManager itBkkTransactionManager(@Qualifier("itBkkEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean emfb) {
        return new JpaTransactionManager(emfb.getObject());
    }
}
