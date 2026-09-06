/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.rasc.s4ws.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSocketSecurity
public class WebSecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.ignoringRequestMatchers("/sockjs/**", "/snakesockjs/**", "/smoothieSockJS/**"))
			.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
			.formLogin(login -> login.loginPage("/portfolio/login.html")
				.loginProcessingUrl("/portfolio/login.html")
				.defaultSuccessUrl("/portfolio/index.html", true)
				.failureUrl("/portfolio/login.html?error").permitAll())
			.logout(logout -> logout.logoutUrl("/portfolio/logout.html")
				.logoutSuccessUrl("/portfolio/login.html?logout").permitAll())
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/portfolio/login.css").permitAll()
				.requestMatchers("/portfolio/**").authenticated()
				.anyRequest().permitAll());
		return http.build();
	}

	@Bean
	AuthorizationManager<Message<?>> messageAuthorizationManager(
			MessageMatcherDelegatingAuthorizationManager.Builder messages) {
		return messages
			.nullDestMatcher().permitAll()
			.simpSubscribeDestMatchers("/app/positions", "/user/queue/**").authenticated()
			.simpSubscribeDestMatchers("/topic/**", "/queue/tennis/bet/**").permitAll()
			.simpDestMatchers("/app/trade").authenticated()
			.simpDestMatchers("/topic/chat", "/app/tennis/bet/**").permitAll()
			.anyMessage().denyAll().build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(PasswordEncoder encoder) {
		return new InMemoryUserDetailsManager(
			User.withUsername("fabrice").password(encoder.encode("fab123")).roles("USER").build(),
			User.withUsername("paulson").password(encoder.encode("bond")).roles("ADMIN", "USER").build());
	}
}
