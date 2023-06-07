/*
 * Copyright (C) 2017 GIP RECIA http://www.recia.fr
 * @Author (C) 2013 Maxime Bossard <mxbossard@gmail.com>
 * @Author (C) 2016 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
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
package com.example.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import com.example.dao.IStructureDao;
import com.example.dao.bean.IStructureFormatter;
import com.example.model.Structure;

/**
 * @author GIP RECIA 2013 - Maxime BOSSARD.
 *
 */
//@Repository
@Data
@Slf4j
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "ldap")
public class LdapStructureDao implements IStructureDao/*, InitializingBean*/{

	@NonNull
	private String allStructFilter;
	@NonNull
	private String structIdLdapAttr;
	@NonNull
	private String etabcodeLdapAttr;
	@NonNull
	private String structNameLdapAttr;
	@NonNull
	private String structDisplayNameLdapAttr;
	@NonNull
	private String structDescriptionLdapAttr;
	@NonNull
	private String structDn;

	private Set<String> otherAttributes;
	@NonNull
	private Set<String> classValueStructUAI;

	private String ldapUrl;
	private String managerDn;
	private String managerPassword;
	
	@NonNull
	private String structIdTemplate = "%siren";

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private List<IStructureFormatter> structureFormatters;

	/** Structure Ldap base. */
	@NonNull
	private String structureBase;

	@Override
	@SuppressWarnings("unchecked")
	public Collection<? extends Structure> findAllStructures() {
		log.debug("Finding all structures ...");

		List<Structure> allStructs;
		try {
			allStructs = this.ldapTemplate.search(this.structureBase, this.allStructFilter,
					new StructureAttributesMapper(this.structIdLdapAttr, this.etabcodeLdapAttr,
							this.structNameLdapAttr, this.structDisplayNameLdapAttr, this.structDescriptionLdapAttr,
							this.otherAttributes, this.classValueStructUAI, this.structureFormatters));
		} catch (final Exception e) {
			// We catch all exceptions, cause we don't want our portlet to block the portal.
			log.error("Error while searching for structures in LDAP !", e);
			allStructs = Collections.emptyList();
		}

		log.debug("{} structures found.", allStructs.size());

		return allStructs;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Structure findOneStructureById(final String id) {
		log.debug("Finding one structure with id {}", id);

		AndFilter filter = new AndFilter();
		filter.append(new HardcodedFilter(this.allStructFilter));
		filter.append(new EqualsFilter(this.structIdLdapAttr, id));
		Structure theStruct = null;
		try {
			List<Structure> result = this.ldapTemplate.search(this.structureBase, filter.encode(),
					new StructureAttributesMapper(this.structIdLdapAttr, this.etabcodeLdapAttr,
							this.structNameLdapAttr, this.structDisplayNameLdapAttr, this.structDescriptionLdapAttr,
							this.otherAttributes, this.classValueStructUAI, this.structureFormatters));
			Assert.isTrue(result.size() <= 1, "Looking for one structure and found " + result.size());
			if (result.size() == 1) {
				theStruct = result.get(0);
			}
		} catch (final Exception e) {
			// We catch all exceptions, cause we don't want our portlet to block the portal.
			log.error("Error while searching for structures in LDAP !", e);
		}

		log.debug("Found the structure {}.", theStruct);

		return theStruct;
	}


	@Override
	public void saveStructure(String customName, String siteWeb, String logo, String id) {

		// Validate input
	   if ( id == null || id.isEmpty()) {
		   throw new IllegalArgumentException("Structure ID cannot be null or empty !");
	   }
		   
	   // Update displayName, logo, and siteWeb into Ldap 
	   final Name dn = buildDn(id);
	   List<ModificationItem> mods = new ArrayList<>();
   
	   if (customName != null || siteWeb != null) {
		   updateForm(customName, siteWeb, id, mods);
	   }
   
	   if (logo != null) {
		   //updateLogo(logo, id, mods);
	   }
   
	   this.ldapTemplate.modifyAttributes(dn, mods.toArray(new ModificationItem[mods.size()]));
	   log.info("Structure with ID {} updated in LDAP. Display name: {}. Logo: {}. Site web: {}.",
		   id, customName, logo, siteWeb);
   
   }
	
	private Name buildDn(String idStruct) {
		try {
			if (idStruct != null) {
				return LdapNameBuilder.newInstance(this.structDn.replace(this.structIdTemplate, idStruct)).build();
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid structure ID: " + idStruct, e);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void updateForm1(String customName, String siteWeb, List<ModificationItem> mods) {

		// update in database : sarapis 

		if ( customName != null ) {
			Attribute updCustomName = new BasicAttribute(this.structDisplayNameLdapAttr, customName);
			mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, updCustomName));
		}

		if ( siteWeb != null ) {
			for (String attr : otherAttributes) {

				if (attr.equals("ENTStructureSiteWeb")) {
					Attribute updSiteWeb = new BasicAttribute(attr, siteWeb);
					mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, updSiteWeb));
				}
			}
		}
	}

	//@SuppressWarnings("unused")
	private void updateLogo(String logo, String id, List<ModificationItem> mods) {
		// save in database 
		updateDataInDatabase(null, null, logo, id);

		// save in ldap
		if ( logo != null ) {
			for (String attr : otherAttributes) {

				if (attr.equals("ESCOStructureLogo")) {
					Attribute updLogo = new BasicAttribute(attr, logo);
					mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, updLogo));
				}
			}
		}
	}


	//@SuppressWarnings("unused")
	private void updateForm(String customName, String siteWeb, String id, List<ModificationItem> mods) {
		// save in database 
		updateDataInDatabase(customName, siteWeb, null, id);

		// save in ldap
		if ( customName != null ) {
			Attribute updCustomName = new BasicAttribute(this.structDisplayNameLdapAttr, customName);
			mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, updCustomName));
		}

		if ( siteWeb != null ) {
			for (String attr : otherAttributes) {

				if (attr.equals("ENTStructureSiteWeb")) {
					Attribute updSiteWeb = new BasicAttribute(attr, siteWeb);
					mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, updSiteWeb));
				}
			}
		}
	}

	private void updateDataInDatabase(String customName, String siteWeb, String logo, String id) {
		String updateQuery = "UPDATE astructure SET ";
		List<Object> params = new ArrayList<>();
	
		if (customName != null) {
			updateQuery += "astructure.nomCourt = ?, ";
			params.add(customName);
		}
	
		if (siteWeb != null) {
			updateQuery += "astructure.siteWeb = ?, ";
			params.add(siteWeb);
		}

		if (logo != null) {
			updateQuery += "astructure.logo = ?, ";
			params.add(logo);
		}
	
		// Remove the trailing comma and space from the updateQuery
		updateQuery = updateQuery.substring(0, updateQuery.length() - 2);
		
		// Add the WHERE condition to the updateQuery
		updateQuery += " WHERE id = ?";
		params.add(id);

		System.out.println("updateQuery : " + updateQuery);
		System.out.println("params : " + params.toString());

	}
}
