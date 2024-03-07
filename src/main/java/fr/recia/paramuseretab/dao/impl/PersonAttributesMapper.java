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

import fr.recia.paramuseretab.dao.bean.IUserFormatter;
import fr.recia.paramuseretab.model.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class PersonAttributesMapper implements AttributesMapper<Person> {

    private String uid;
    private String currentStruct;
    private String structIdsInfoKey;
    private String groupAttributes;
    private IUserFormatter etabFormatter;

    @Override
    @SuppressWarnings(value = "unchecked")
    public Person mapFromAttributes(Attributes attributes) throws NamingException {
        Person person = new Person();
        person.setUid((String) attributes.get(uid).get());
        person.setCurrentStruct((String) attributes.get(currentStruct).get());

        List<String> listIsMemberOf = new ArrayList<>();
        List<String> listSiren = new ArrayList<>();
        final Attribute dirAttr = attributes.get(groupAttributes);
        if (dirAttr != null) {
            NamingEnumeration<String> ae = (NamingEnumeration<String>) dirAttr.getAll();
            while (ae.hasMore()) {
                listIsMemberOf.add(ae.next());
            }
        }

        final Attribute attrStruct = attributes.get(structIdsInfoKey);
        if (attrStruct != null) {
            NamingEnumeration<String> ae = (NamingEnumeration<String>) attrStruct.getAll();
            while (ae.hasMore()) {
                listSiren.add(ae.next());
            }
        }

        person.setSiren(listSiren);
        person.setIsMemberOf(listIsMemberOf);

        try {
            person = etabFormatter.formatPerson(person);
        } catch (Exception e) {
            log.error("Error during format person: {} ", e.getMessage());
        }

        return person;
    }
}