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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

/**
 * Customizes Spring Security configuration.
 *
 * @author Rob Winch
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.csrf().disable()
			.headers()
		   	  .addHeaderWriter(
					new XFrameOptionsHeaderWriter(
							XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
			.and()
			  .formLogin().defaultSuccessUrl("/portfolio/index.html")
			  .loginPage("/portfolio/login.html")
			  .failureUrl("/portfolio/login.html?error").permitAll()
			.and()
			  .logout()
			    .logoutSuccessUrl("/portfolio/login.html?logout")
			    .logoutUrl("/portfolio/logout.html").permitAll()
			.and()
			  .authorizeRequests()
			    .antMatchers("/portfolio/login.css").permitAll()
			    .antMatchers("/portfolio/**").authenticated();
		// @formatter:on
	}

	@Override
	@Bean
	public UserDetailsService userDetailsService() {
		User.UserBuilder users = User.withDefaultPasswordEncoder();
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(
				users.username("fabrice").password("fab123").roles("USER").build());
		manager.createUser(users.username("paulson").password("bond")
				.roles("ADMIN", "USER").build());
		return manager;
	}
}