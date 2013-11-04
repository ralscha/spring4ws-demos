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

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Rob Winch
 * 
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//@formatter:off
		http
		    .csrf().disable()
			.authorizeRequests()
				.antMatchers("/portfoliodemo/login.css").permitAll()
				.antMatchers("/portfoliodemo/**").authenticated()
				.and()
			.logout()
				.logoutSuccessUrl("/portfoliodemo/login.html?logout")
				.logoutUrl("/portfoliodemo/logout.html")
				.permitAll()
				.and()
			.formLogin()
				.defaultSuccessUrl("/portfoliodemo/index.html")
				.loginPage("/portfoliodemo/login.html")
				.failureUrl("/portfoliodemo/login.html?error")
				.permitAll();
		//@formatter:on
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//@formatter:off
		auth
			.inMemoryAuthentication()
				.withUser("fabrice").password("fab123").roles("USER").and()
				.withUser("paulson").password("bond").roles("ADMIN","USER");
		//@formatter:on
	}
}