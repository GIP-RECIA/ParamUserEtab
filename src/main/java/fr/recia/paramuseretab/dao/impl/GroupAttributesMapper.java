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

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import fr.recia.paramuseretab.model.Groups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class GroupAttributesMapper implements AttributesMapper {

    @NonNull
    private final String nameAttrKey;

    @Override
    public Object mapFromAttributes(final Attributes attributes) throws NamingException {

        Groups group;

        final String name = (String) attributes.get(this.nameAttrKey).get();

        group = new Groups(name);

        return group;
    }

}
