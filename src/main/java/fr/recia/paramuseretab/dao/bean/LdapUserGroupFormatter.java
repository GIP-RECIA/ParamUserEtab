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

import fr.recia.paramuseretab.model.Groups;
import fr.recia.paramuseretab.model.Person;
import fr.recia.paramuseretab.service.IGroupService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "userformatter")
public class LdapUserGroupFormatter implements IUserFormatter, InitializingBean {

    @Autowired
    private IGroupService groupService;

    @Getter
    private String userGroupsRegex;
    private Pattern userGroupsRegexPattern;

    @Getter
    private String userENTGroupsRegex;
    private Pattern userENTGroupsRegexPattern;

    public void setUserGroupsRegex(final String userGroupsRegex) {
        this.userGroupsRegex = userGroupsRegex;
        this.userGroupsRegexPattern = Pattern.compile(userGroupsRegex);
    }

    public void setUserENTGroupsRegex(final String userENTGroupsRegex) {
        this.userENTGroupsRegex = userENTGroupsRegex;
        this.userENTGroupsRegexPattern = Pattern.compile(userENTGroupsRegex);
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
    private List<String> format(List<String> groups) {

        List<String> formattedList = new ArrayList<>();
        final Collection<? extends Groups> allGroups = this.groupService.retrieveGroups();

        for (String input : groups) {
            if (input != null && !input.isEmpty()) {
                String etabName = "";

                Matcher groupAdminCentral = this.userENTGroupsRegexPattern.matcher(input);
                if (groupAdminCentral.find()) {
                    String groupBranch = groupAdminCentral.group(1);
                    for (Groups group : allGroups) {
                        String cn = group.getName();
                        String[] parts = cn.split(":");
                        if (parts.length >= 4 && parts[0].equals(groupBranch)) {
                            // Check if the uai part exists
                            String[] secondPartSplit = parts[2].split("_");
                            if (secondPartSplit.length >= 2) {
                                String uai = secondPartSplit[1];
                                etabName = secondPartSplit[0] + " (" + uai + ")";
                            } else {
                                etabName = secondPartSplit[0];
                            }
                            formattedList.add(etabName);
                        }
                    }
                }

                Matcher groupAdminLocal = this.userGroupsRegexPattern.matcher(input);
                if (groupAdminLocal.find()) {
                    String group2 = groupAdminLocal.group(2);
                    String group1 = groupAdminLocal.group(1);
                    if (group1 != null) {
                        etabName = group1 + " (" + group2 + ")";
                    } else {
                        etabName = group2;
                    }
                    log.debug("Matcher found groups isMemberOf, and applied replacement value is : {}", etabName);

                    if (!formattedList.contains(etabName)) {
                        formattedList.add(etabName);
                    }
                }
            }
        }

        return formattedList.isEmpty() ? null : formattedList;
    }
}
