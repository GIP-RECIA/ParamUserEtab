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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fr.recia.paramuseretab.model.Person;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "userformatter")
public class LdapUserGroupFormatter implements IUserFormatter, InitializingBean {

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
    public Person formatPerson(Person input) {
        if (input != null) {
            input.setIsMemberOf(format(input.getIsMemberOf()));
        }

        return input;
    }

    /*
     * Use regex to format the 'isMemberOf' values in order to extract the name of
     * the 'etablissement' and the UAI.
     * 
     */
    private List<String> format(List<String> inputs) {

        List<String> formattedList = new ArrayList<>();

        for (String input : inputs) {
            if (input != null && !input.isEmpty()) {
                Matcher groupMatcher = this.userGroupsRegexPattern.matcher(input);
                if (groupMatcher.find()) {
                    String group2 = groupMatcher.group(2);
                    String group1 = groupMatcher.group(1);
                    String etabName = "";
                    List<String> listEtab = new ArrayList<>();
                    if (group1 != null) {
                        etabName = group1 + " (" + group2 + ")";
                    } else {
                        etabName = group2;
                    }
                    log.debug("Matcher found groups isMemberOf, and applied replacement value is : {}", etabName);

                    listEtab.add(etabName);
                    formattedList.addAll(listEtab);
                }
            }
        }

        return formattedList.isEmpty() ? null : formattedList;
    }
}