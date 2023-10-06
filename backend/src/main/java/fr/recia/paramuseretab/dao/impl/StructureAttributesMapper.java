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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.util.Assert;

import fr.recia.paramuseretab.dao.bean.IStructureFormatter;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.model.UniteAdministrativeImmatriculee;

@Data
@AllArgsConstructor
public class StructureAttributesMapper implements AttributesMapper {

	@NonNull
	private final String idAttrKey;
	@NonNull
	private final String codeAttrKey;
	@NonNull
	private final String nameAttrKey;
	@NonNull
	private final String displayNameAttrKey;
	@NonNull
	private final String descriptionAttrKey;

	private final Set<String> otherAttributes;
	@NonNull
	private final Set<String> classValueStructUAI;
	@NonNull
	@Autowired
	private List<IStructureFormatter> etablissementFormatters;

	private static final String classAttrKey = "objectClass";

	@Override
	@SuppressWarnings("unchecked")
	public Object mapFromAttributes(final Attributes attrs) throws NamingException {

		boolean isEtab = false;
		for (String objclass : classValueStructUAI) {
			if (attrs.get(classAttrKey).contains(objclass)) {
				isEtab = true;
				break;
			}
		}

		Structure struct;

		final String siren = (String) attrs.get(this.idAttrKey).get();
		final String name = (String) attrs.get(this.nameAttrKey).get();
		final String desc = (String) attrs.get(this.descriptionAttrKey).get();
		String displayName = null;
		// managing a UniteAdministrativeImmatriculee displayName if was defined
		Attribute displayNameAttr = attrs.get(this.displayNameAttrKey);
		if (displayNameAttr != null) {
			displayName = (String) displayNameAttr.get();
		}
		// managing a cutomDisplayName from Name if displayName was not set
		// the displayName can be changed in the formatter
		if (displayName == null || displayName.isEmpty()) {
			displayName = name;
		}
		Map<String, List<String>> otherAttrs = new HashMap<>(otherAttributes.size());
		for (String attrName : otherAttributes) {
			List<String> values = new ArrayList<>();
			final Attribute dirAttr = attrs.get(attrName);
			if (dirAttr != null) {
				for (NamingEnumeration<String> ae = (NamingEnumeration<String>) dirAttr.getAll(); ae.hasMore();) {
					values.add(ae.next());
				}
			}
			otherAttrs.put(attrName, values);
		}

		if (isEtab) {
			final String uai = (String) attrs.get(this.codeAttrKey).get();
			struct = new UniteAdministrativeImmatriculee(siren, uai.toUpperCase(), name, displayName, desc, otherAttrs);
		} else {
			struct = new Structure(siren, name, displayName, desc, otherAttrs);
		}

		for (IStructureFormatter formatter : this.etablissementFormatters) {
			struct = formatter.format(struct);
			struct = formatter.formatName(struct);
		}

		Assert.hasText(struct.getId(), "No SIREN attribute found in LDAP for UniteAdministrativeImmatriculee !");
		if (isEtab) {
			Assert.hasText(((UniteAdministrativeImmatriculee) struct).getCode(),
					"No UAI attribute found in LDAP for UniteAdministrativeImmatriculee !");
		}
		Assert.hasText(struct.getName(), "No Name attribute found in LDAP for UniteAdministrativeImmatriculee !");
		Assert.hasText(struct.getDisplayName(),
				"No DisplayName attribute found in LDAP for UniteAdministrativeImmatriculee !");
		/*
		 * Assert.hasText(etab.getDescription(),
		 * "No Description attribute found in LDAP for UniteAdministrativeImmatriculee !"
		 * );
		 */

		return struct;
	}

}
