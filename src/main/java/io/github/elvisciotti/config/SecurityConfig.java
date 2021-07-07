package io.github.elvisciotti.config;

import io.github.elvisciotti.auth.AuthenticationFilter;
import io.github.elvisciotti.auth.AuthorizationFilter;
import io.github.elvisciotti.auth.UserDetailServiceJpa;
import io.github.elvisciotti.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailServiceJpa uds;

    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(uds)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users/signup").permitAll()
                .antMatchers(HttpMethod.POST, "/users/password-reset").permitAll()
                .antMatchers(HttpMethod.GET, "/health-check").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // **permit OPTIONS call to all**
                .anyRequest().authenticated()
                .and()
                .addFilter(new AuthenticationFilter(this.authenticationManager()))
                .addFilter(new AuthorizationFilter(this.authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration()
                .applyPermitDefaultValues();
        for (HttpMethod h : HttpMethod.values()) {
            config.addAllowedMethod(h);
        }

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}