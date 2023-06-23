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
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;

import lombok.extern.slf4j.Slf4j;

import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.dao.impl.LdapStructureDao;

@Configuration
//@EnableConfigurationProperties(CachingStructureService.class)
//@ComponentScan("fr.recia.paramuseretab.service.impl")
// @Import(EmbeddedLdapAutoConfiguration.class)
// @Slf4j
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

	private static String defaultPartitionSuffix = "dc=esco-centre,dc=fr"; // added

	private int ldapServerPort = 42539; // added

	private boolean validateSchema = true; // added 
	private String schemaFilePath = ClassLoader.getSystemResource(
		"esco-structure-schema.ldif").getPath(); // added

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

	// added 
	// @Bean
    // public InMemoryDirectoryServer inMemoryDirectoryServer() {

	// 	InMemoryDirectoryServerConfig config;

	// 	try {
	// 		String pathurl = ClassLoader.getSystemResource(
	// 		"init2.ldif").getPath();
	// 		System.out.println("path: " +pathurl);
	// 		log.info("LDAP server " + toString() + " starting...");
	// 		config = new InMemoryDirectoryServerConfig(defaultPartitionSuffix);
	// 		log.info("config");
	// 		if (!validateSchema) {
	// 			config.setSchema(null);
	// 			System.out.println("not vaidateschema");
	// 		} else if (schemaFilePath != null) {
	// 			config.setSchema(Schema.mergeSchemas(Schema.getDefaultStandardSchema(),
	// 					Schema.getSchema(schemaFilePath)));
	// 					System.out.println("schema not null : " + schemaFilePath);
	// 		} else {
	// 			config.setSchema(Schema.getDefaultStandardSchema());
	// 		}

	// 		config.setSchema(null);
	// 		config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("LDAP", ldapServerPort));
	// 		InMemoryDirectoryServer server = new InMemoryDirectoryServer(config);
	// 		try {
	// 			server.importFromLDIF(true, pathurl);
	// 			System.out.println("ok");
	// 		} catch (Exception e) {
	// 			Handle LDIF import error
	// 			System.out.println("not ok ldif : " + e);
	// 		}
	// 		server.startListening();
	// 		log.info("LDAP server " + toString() + " started. Listen on port " + server.getListenPort());

	// 		return server;
	// 	} catch (Exception e) {
	// 		log.info("error : ",e);
	// 		throw new RuntimeException(e);
	// 	}
    // }
	
}
