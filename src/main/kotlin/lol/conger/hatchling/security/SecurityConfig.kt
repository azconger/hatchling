package lol.conger.hatchling.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val atlasDigestFilter: AtlasDigestAuthenticationFilter,
    private val digestEntryPoint: AtlasDigestAuthenticationEntryPoint
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    // Protected GET endpoint for user profile (requires digest auth)
                    .requestMatchers(HttpMethod.GET, "/api/v1/user/profile").authenticated()
                    // Allow other GET requests without authentication  
                    .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                    // Allow access to OpenAPI/Swagger documentation
                    .requestMatchers("/openapi/**", "/swagger/**", "/v3/api-docs/**").permitAll()
                    // Require authentication for all other API endpoints
                    .requestMatchers("/api/**").authenticated()
                    // Allow everything else
                    .anyRequest().permitAll()
            }
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(digestEntryPoint)
            }
            .addFilterBefore(atlasDigestFilter, UsernamePasswordAuthenticationFilter::class.java)
            .csrf { csrf ->
                csrf.disable()
            }

        return http.build()
    }
}