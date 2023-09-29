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
package fr.recia.paramuseretab.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.dao.impl.LdapStructureDao;

@Configuration
public class WebAppConfiguration implements WebMvcConfigurer {

	@Bean
	public IStructureDao structureDao() {
		return new LdapStructureDao();
	}

	@Value("${ldap.ldap-url}")
	private String ldapUrl;

	@Value("${ldap.manager-dn}")
	private String ldapManagerDn;

	@Value("${ldap.manager-password}")
	private String ldapManagerPassword;

	@Bean
	public LdapTemplate ldapTemplate() throws Exception {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl(ldapUrl);

		contextSource.setUserDn(ldapManagerDn); // comment later, if using ldif
		contextSource.setPassword(ldapManagerPassword); // comment later, if using ldif
		contextSource.afterPropertiesSet();

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		ldapTemplate.setIgnorePartialResultException(true);
		ldapTemplate.setContextSource(contextSource);
		ldapTemplate.afterPropertiesSet();

		// Load the LDIF file from the classpath

		return ldapTemplate;
	}

}
