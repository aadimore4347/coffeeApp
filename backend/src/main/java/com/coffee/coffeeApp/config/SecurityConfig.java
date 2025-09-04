// package com.coffee.coffeeApp.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import com.coffee.coffeeApp.security.JwtAuthFilter;

// import lombok.RequiredArgsConstructor;

// @Configuration
// @RequiredArgsConstructor
// public class SecurityConfig {

// 	private final JwtAuthFilter jwtAuthFilter;

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
//     	return configuration.getAuthenticationManager();
//     }

//     @Bean 
//     public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
//     	httpSecurity
//     			.csrf(csrfConfig->csrfConfig.disable())
//     			.sessionManagement(sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//     			.authorizeHttpRequests(auth->auth
//     					.requestMatchers("/api/auth/**").permitAll()
//     					.anyRequest().authenticated()
// //    					.requestMatchers("/admin/**").hasRole("ADMIN")
// //    					.requestMatchers("/facility/**").hasRole("FACILITY")
//     			)
//     			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//     	return httpSecurity.build();
//     }
// }

package com.coffee.coffeeApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.coffee.coffeeApp.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf(csrfConfig -> csrfConfig.disable())
				.cors().and() // 🔥 enable CORS
				.sessionManagement(
						sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers(org.springframework.http.HttpMethod.GET,
								"/api/health/**",
								"/api/facilities/**",
								"/api/machines/**",
								"/api/usage/**",
								"/api/alerts/**").permitAll()
						.requestMatchers(org.springframework.http.HttpMethod.POST,
								"/api/alerts/*/acknowledge",
								"/api/machines/*/status",
								"/api/machines/*/levels",
								"/api/machines/*/refill",
								"/api/machines/brew").permitAll()
						.requestMatchers(org.springframework.http.HttpMethod.PUT,
								"/api/machines/**").permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

	// 🔥 Global CORS configuration
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000")); // your React app
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
