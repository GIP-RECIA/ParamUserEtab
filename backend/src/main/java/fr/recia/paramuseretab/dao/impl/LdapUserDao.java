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
/**
 *
 */
package fr.recia.paramuseretab.dao.impl;

import java.util.List;
import java.util.Set;

import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fr.recia.paramuseretab.dao.IUserDao;
import fr.recia.paramuseretab.model.Person;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.model.UniteAdministrativeImmatriculee;

/**
 * @author GIP RECIA 2013 - Maxime BOSSARD.
 *
 */
//@Repository
@Component
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "userdao")
public class LdapUserDao implements IUserDao, InitializingBean {

	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(LdapUserDao.class);

	@NonNull
	private String userIdTemplate = "%u";

	@Autowired
	private LdapTemplate ldapTemplate;

	/** User base Ldap dn. */
	@NonNull
	private String userDn;

	/** Current struct Id LDAP key. */
	@NonNull
	private String currentStructIdLdapKey;
	/** Current struct Code LDAP key. */
	@NonNull
	private String currentEtabCodeLdapKey;

	@NonNull
	private String personFiltre;

	@NonNull
	private String uid;

	private Set<String> groupAttributes;


	@Override
	public void saveCurrentStructure(final String userId, final Structure struct) {
		LdapUserDao.LOG.debug("Saving current structure ...");

		final Attribute replaceCurrentStructAttr = new BasicAttribute(this.currentStructIdLdapKey, struct.getId());
		ModificationItem[] mods = null;
		if (struct instanceof UniteAdministrativeImmatriculee) {
			final Attribute replaceCurrentEtabAttr = new BasicAttribute(this.currentEtabCodeLdapKey,
					((UniteAdministrativeImmatriculee) struct).getCode());
			mods = new ModificationItem[2];
			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, replaceCurrentEtabAttr);

		} else {
			mods = new ModificationItem[1];
		}
		final Name dn = LdapNameBuilder.newInstance(this.userDn.replace(this.userIdTemplate, userId)).build();
		mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, replaceCurrentStructAttr);
		this.ldapTemplate.modifyAttributes(dn, mods);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.ldapTemplate, "No LdapTemplate configured !");
		Assert.hasText(this.userDn, "No user dn configured !");
		Assert.hasText(this.userIdTemplate, "No user Id template configured !");
		Assert.hasText(this.currentStructIdLdapKey, "No current struct Id Ldap key configured !");
		Assert.hasText(this.currentEtabCodeLdapKey, "No current etab Code Ldap key configured !");

		Assert.state(this.userDn.contains(this.userIdTemplate), "User dn doesn't contain the user Id template !");
	}

	@Override
	public List<Person> getAllUsersInfo() {
		// edited, adding try catch 
		List<Person> allInfos;
		try {
			allInfos = this.ldapTemplate.search(this.userDn, this.personFiltre, new PersonAttributesMapper(uid, currentStructIdLdapKey, groupAttributes));
		} catch (Exception e) {
			LOG.error("error :", e);
			allInfos = null;
		}
		return allInfos;
		
	}


}