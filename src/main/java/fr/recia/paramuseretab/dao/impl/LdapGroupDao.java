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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

import fr.recia.paramuseretab.dao.IGroupDao;
import fr.recia.paramuseretab.model.Groups;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "groupdao")
public class LdapGroupDao implements IGroupDao {

    @Autowired
    private LdapTemplate ldapTemplate;

    @NonNull
    private String groupBase;
    @NonNull
    private String allGroupFilter;
    @NonNull
    private String groupCN;

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends Groups> findAllGroups() {
        log.debug("Finding all groups ...");

        List<Groups> allGroups;
        try {
            allGroups = this.ldapTemplate.search(this.groupBase, this.allGroupFilter,
                    new GroupAttributesMapper(this.groupCN));
        } catch (final Exception e) {
            log.error("Error while searching for groups in LDAP !", e);
            allGroups = Collections.emptyList();
        }

        log.debug("{} groups found.", allGroups.size());

        return allGroups;

    }
}
