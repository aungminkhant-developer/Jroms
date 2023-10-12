package gp.pyinsa.jroms.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import gp.pyinsa.jroms.services.UserDetailsServiceImpl;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /* Uncomment this to add security */
        http.rememberMe(rm -> rm.rememberMeCookieName("jroms-remember-me").userDetailsService(userDetailsService())
                .tokenValiditySeconds(432000))
                .authorizeHttpRequests(
                        (auth) -> auth.antMatchers("/css/**", "/js/**").permitAll().antMatchers("/mng/**")
                                .authenticated().antMatchers("/**").permitAll().anyRequest().authenticated())
                .formLogin((form) -> form.loginPage("/mng/login").loginProcessingUrl("/login").defaultSuccessUrl("/mng/dashboard")
                        .permitAll())
                .logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll())
                .sessionManagement((sessionManagement) -> sessionManagement.maximumSessions(-1).sessionRegistry(sessionRegistry()));

        /* Uncomment this to remove temporarily security */
        // http.rememberMe(rm ->
        // rm.rememberMeCookieName("jroms-remember-me").userDetailsService(userDetailsService())
        // .tokenValiditySeconds(3600)).authorizeHttpRequests((auth) ->
        // auth.antMatchers("/**").permitAll().antMatchers("/js/**").permitAll())
        // .formLogin((form) ->
        // form.loginPage("/mng/login").loginProcessingUrl("/login").defaultSuccessUrl("/dashboard")
        // .permitAll())
        // .logout((logout) -> logout.logoutRequestMatcher(new
        // AntPathRequestMatcher("/logout")).permitAll())
        // ;
        return http.build();
    }

}