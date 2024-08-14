package sit.int221.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeRequests(authorize -> authorize.requestMatchers("/authentications/**").permitAll()
                        .requestMatchers("/v2/tasks/**").permitAll()
//                        .requestMatchers("/v2/statuses/**").permitAll()
                        .anyRequest().authenticated()).httpBasic(withDefaults());
        return httpSecurity.build();
    }
}
