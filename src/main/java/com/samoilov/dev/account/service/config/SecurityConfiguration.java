package com.samoilov.dev.account.service.config;

import com.samoilov.dev.account.service.handler.RestAuthenticationEntryPoint;
import com.samoilov.dev.account.service.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfiguration {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private final UserAccountService userDetailsService;

    private final ApplicationConfiguration configuration;



    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationManagerBuilder authenticationManagerBuilder
    ) throws Exception {
        http.userDetailsService(userDetailsService)
                .httpBasic() // enables basic auth
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .csrf().disable() // disabling CSRF will allow sending POST request using Postman
                .headers().frameOptions().disable() // database console
                .and()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/actuator/shutdown", "/h2-console", "/h2-console/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST,"/api/auth/signup").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/empl/payment")
                            .hasAnyAuthority("ACCOUNTANT", "ADMINISTRATOR");
                    auth.requestMatchers("/api/acct/payments").hasAnyAuthority("ACCOUNTANT");
                    auth.requestMatchers("/api/admin/**").hasAnyAuthority("ADMINISTRATOR");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session

        authenticationManagerBuilder.authenticationProvider(this.daoAuthenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(configuration.getEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

}
