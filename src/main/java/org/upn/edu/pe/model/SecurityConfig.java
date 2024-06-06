package org.upn.edu.pe.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/index", "/carrito", "/registrar", "/login", "/css/**", "/js/**", "/img/**").permitAll() // Permitir acceso a recursos estáticos
                .anyRequest().authenticated() // Requerir autenticación para todas las demás páginas
                .and()
            .formLogin()
                .loginPage("/login").permitAll() // Permitir acceso a la página de inicio de sesión
                .defaultSuccessUrl("/index") // Redirigir al usuario a la página de inicio después de iniciar sesión exitosamente
                .failureUrl("/login?error=true") // Redirigir al usuario de nuevo a la página de inicio de sesión en caso de error de inicio de sesión
                .and()
            .logout()
                .logoutUrl("/logout") // URL de la página de cierre de sesión
                .logoutSuccessUrl("/login?logout=true") // Redirigir al usuario a la página de inicio de sesión después de cerrar sesión
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID") // Invalidar la sesión y eliminar las cookies
                .permitAll()
                .and()
            .rememberMe() // Configurar "Remember Me"
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(86400) // Duración de la sesión en segundos (1 día)
                .and()
            .sessionManagement()
                .maximumSessions(1) // Permitir solo una sesión por usuario
                .expiredUrl("/login?expired") // Redirigir a la página de inicio de sesión cuando la sesión ha expirado
                .and()
                .and()
            .exceptionHandling()
                .accessDeniedPage("/acceso-denegado")
                .and()
            .csrf().disable(); // Deshabilitar CSRF para permitir el registro a través de formularios HTML
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        InMemoryTokenRepositoryImpl memory = new InMemoryTokenRepositoryImpl();
        return memory;
    }
}
