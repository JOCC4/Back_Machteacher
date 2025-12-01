package MachTeacher.MachTeacher.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                
                .cors(cors -> cors.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                        // --------- PÚBLICO ---------
                        .requestMatchers(
                                "/v3/api-docs", "/v3/api-docs/**",
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/swagger-resources/**", "/webjars/**",
                                "/api/profiles/student",
                                "/api/profiles/mentor",
                                "/api/profiles/student/**",
                                "/api/profiles/mentor/**",
                                "/api/catalog/**",
                                "/api/mentors/search/**",
                                "/api/messages/**",
                                "/api/messages/conversations/**",
                                "/api/reviews/**",
                                "/api/register/**",
                                "/api/auth/register/**",
                                "/api/auth/login",
                                "/api/auth/**",
                                "/api/sos/**")
                        .permitAll()

                        // --------- PROFILES ---------
                        .requestMatchers(HttpMethod.GET, "/api/profiles/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/profiles/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/profiles/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/profiles/**").authenticated()

                        // --------- SESSIONS ---------
                        .requestMatchers(HttpMethod.POST, "/api/sessions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sessions/student/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sessions/mentor/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/sessions/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/sessions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sessions/*").permitAll()

                        // --------- SUBJECTS---------
                        .requestMatchers(HttpMethod.GET, "/api/subjects", "/api/subjects/**").permitAll()

                        
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
