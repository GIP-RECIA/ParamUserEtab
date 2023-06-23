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

import org.springframework.ldap.core.AttributesMapper;

import fr.recia.paramuseretab.model.Person;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonAttributesMapper implements AttributesMapper<Person> {

    private String uid; 
    private String currentStruct;
    private Set<String> groupAttributes;

    @Override
    @SuppressWarnings (value="unchecked")
    public Person mapFromAttributes(Attributes attributes) throws NamingException {
        Person person = new Person();
        person.setUid((String) attributes.get(uid).get());
        person.setCurrentStruct((String) attributes.get(currentStruct).get());

        List<Map<String, String>> listGroups = new ArrayList<>();
        for (String attrName : groupAttributes) {
            final Attribute dirAttr = attributes.get(attrName);
            if (dirAttr != null) {
                for (NamingEnumeration<String> ae = (NamingEnumeration<String>) dirAttr.getAll(); ae.hasMore();) {
                    Map<String, String> grpItem = new HashMap<>();
                    grpItem.put("etabName", ae.next());
                    listGroups.add(grpItem);
                }
            }
        }

        person.setIsMemberOf(listGroups);

        return person;
    }
}