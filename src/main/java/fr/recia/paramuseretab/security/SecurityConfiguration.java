/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.paramuseretab.security;

import fr.recia.notifications.soffit_java_client.SoffitJwtAuthenticationFilter;
import fr.recia.notifications.soffit_java_client.SoffitJwtValidator;
import fr.recia.paramuseretab.config.ParametabProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final ParametabProperties parametabProperties;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    public SecurityConfiguration(ParametabProperties parametabProperties) {
        this.parametabProperties = parametabProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final SoffitJwtAuthenticationFilter filter = new SoffitJwtAuthenticationFilter(
                new SoffitJwtValidator(parametabProperties.getSoffit().getJwtSignatureKey()));

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));
        http.addFilterBefore(filter, BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/health-check", "/rest/**").permitAll()
                .requestMatchers("/parametab/api/**", "/changeetab/api/**").authenticated()
                .anyRequest().denyAll());

        http.sessionManagement(session -> session.sessionFixation().newSession());

        return http.build();
    }

}
