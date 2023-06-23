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
package fr.recia.paramuseretab.dao.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fr.recia.paramuseretab.model.Person;
import fr.recia.paramuseretab.service.IUniteAdministrativeImmatriculeService;

import lombok.Getter;
import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "userformatter")
public class LdapUserGroupFormatter implements IUserFormatter, InitializingBean {

    @Autowired
    private IUniteAdministrativeImmatriculeService uaiCode;

    @Getter
    private String userGroupsRegex;
    private Pattern userGroupsRegexPattern;

    public void setUserGroupsRegex(final String userGroupsRegex) {
		this.userGroupsRegex = userGroupsRegex;
		this.userGroupsRegexPattern = Pattern.compile(userGroupsRegex);
	}

    @Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.userGroupsRegexPattern,
				"Attribut groupsRegex wasn't initialized, a regular expression with groups should be passed");
	}

    @Override
    public List<Person> formatPerson(List<Person> inputs) {
        List<Person> formattedPersons = new ArrayList<>();
    
        for (Person input : inputs) {
            List<Map<String, String>> groupes = input.getIsMemberOf();
            List<Map<String, String>> listGroups = new ArrayList<>();
    
            for (Map<String, String> groupe : groupes) {
                for (Map.Entry<String, String> entry : groupe.entrySet()) {

                    String keyMap = entry.getKey();
                    String valueMap = entry.getValue();
                    if (keyMap.equals("etabName") &&  (valueMap != null && !valueMap.isEmpty())) {
                        Matcher groupMatcher = this.userGroupsRegexPattern.matcher(valueMap);
                        if (groupMatcher.find()) {
                            String group2 = groupMatcher.group(2);
                            String group1 = groupMatcher.group(1);
                            String siren = null;
                            Map<String, String> formattedGroupes = new HashMap<>();
                            if (group1 != null) {
                                siren = this.uaiCode.getSiren(group2, null);
                                formattedGroupes.put(keyMap, group1 + " (" + group2 + ")");
                                formattedGroupes.put("idSiren", siren);
                            } else {
                                siren = this.uaiCode.getSiren(null, group2 );
                                formattedGroupes.put("idSiren", siren);
                                formattedGroupes.put(keyMap, group2);                        
                            }
                            listGroups.add(formattedGroupes);
                        }
                    }
                }
            }
    
            Person formattedPerson = new Person(input.getUid(), input.getCurrentStruct(), listGroups);
            formattedPersons.add(formattedPerson);
        }
    
        return formattedPersons;
    }
    
}