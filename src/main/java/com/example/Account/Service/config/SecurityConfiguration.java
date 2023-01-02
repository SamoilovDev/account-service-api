package com.example.Account.Service.config;

import com.example.Account.Service.handler.RestAuthenticationEntryPoint;
import com.example.Account.Service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
@EnableWebSecurity(debug = true)
public class SecurityConfiguration {

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private ApplicationConfiguration configuration;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        http
                .userDetailsService(userDetailsService)
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
                    auth.requestMatchers("api/acct/payments").hasAnyAuthority("ACCOUNTANT");
                    auth.requestMatchers("api/admin/**").hasAnyAuthority("ADMINISTRATOR");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
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
