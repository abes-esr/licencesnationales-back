package fr.abes.licencesnationales.web.configuration;

import fr.abes.licencesnationales.core.services.GenererIdAbes;
import fr.abes.licencesnationales.web.security.jwt.JwtAuthenticationEntryPoint;
import fr.abes.licencesnationales.web.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GenererIdAbes genererIdAbes() { return new GenererIdAbes(); }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable().exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/v1/authentification/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/v1/etablissements").permitAll()
                .antMatchers("/v1/etablissements/getType").permitAll()
                .antMatchers("/v1/reinitialisationMotDePasse/**").permitAll()
                .antMatchers("/test").permitAll()
                .antMatchers("/v1/applicationVersion").permitAll()
                .antMatchers("/v1/siren/**").permitAll()
                .antMatchers("/v1/v3/api-docs", "/v1/v3/api-docs/**", "/v1/configuration/ui", "/v1/swagger-resources/**", "/v1/configuration/**", "/v1/swagger-ui.html**", "/v1/swagger-ui/**", "/v1/webjars/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v1/swagger-ui/**", "/v1/v3/api-docs/**");
    }

}

