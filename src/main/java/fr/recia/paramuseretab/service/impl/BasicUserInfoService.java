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
package fr.recia.paramuseretab.service.impl;

import fr.recia.paramuseretab.dao.IUserDao;
import fr.recia.paramuseretab.dao.bean.IUserFormatter;
import fr.recia.paramuseretab.model.Person;
import fr.recia.paramuseretab.security.HandledException;
import fr.recia.paramuseretab.service.IUserInfoService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Slf4j
@Service
@ConfigurationProperties(prefix = "userinfo")
public class BasicUserInfoService implements IUserInfoService, InitializingBean {

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IUserFormatter groupFormatters;

    @NonNull
    private String etabCodesInfoKey;
    @NonNull
    private String currentEtabCodeInfoKey;
    @NonNull
    private String structIdsInfoKey;
    @NonNull
    private String currentStructIdInfoKey;

    private final Map<String, List<String>> basicUserInfoMap = new HashMap<>();

    private final Map<String, List<String>> emptyUserInfoMap = new HashMap<>();

    @SuppressWarnings("unused")
    private static final Map<String, List<String>> nullUserInfoMap = null;

    private final Map<String, List<String>> testUserInfoMap = this.basicUserInfoMap;

    private String msg = "Unable to retrieve {} attribute in Portal UserInfo !";

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.etabCodesInfoKey, "No Etab Codes user info key configured !");
        Assert.hasText(this.currentEtabCodeInfoKey, "No Current Etab Code user info key configured !");
        Assert.hasText(this.structIdsInfoKey, "No Struct Ids user info key configured !");
        Assert.hasText(this.currentStructIdInfoKey, "No Current Struct Id user info key configured !");

        final String[] etabs = System.getProperty("etablissement-swapper.userEtabs", "0450822x,0333333y,0377777U")
            .split(",");
        final String[] current = System.getProperty("etablissement-swapper.userCurrentEtab", "0450822X").split(",");
        final String[] structs = System.getProperty("etablissement-swapper.userStructs",
            "88888888888888,37373737373737,00000000000001").split(",");
        final String[] currentStruct = System.getProperty("etablissement-swapper.userCurrentStruct", "88888888888888")
            .split(",");
        this.basicUserInfoMap.put(this.etabCodesInfoKey, Arrays.asList(etabs));
        this.basicUserInfoMap.put(this.currentEtabCodeInfoKey, Arrays.asList(current));
        this.basicUserInfoMap.put(this.structIdsInfoKey, Arrays.asList(structs));
        this.basicUserInfoMap.put(this.currentStructIdInfoKey, Arrays.asList(currentStruct));
        log.debug("basicUserInfoMap : {}", this.basicUserInfoMap);

        this.emptyUserInfoMap.put(this.etabCodesInfoKey, Arrays.asList(new String[]{"1234567b"}));
        this.emptyUserInfoMap.put(this.currentEtabCodeInfoKey, Arrays.asList(new String[]{"1234567B"}));
        this.emptyUserInfoMap.put(this.structIdsInfoKey, Arrays.asList(new String[]{"88888888888888"}));
        this.emptyUserInfoMap.put(this.currentStructIdInfoKey, Arrays.asList(new String[]{"88888888888888"}));
    }

    @Override
    public Person getPersonDetails(String userId) throws HandledException {
        Person allInfo = null;
        try {
            if (userId != null) {
                allInfo = this.userDao.retrievePersonFromLdap(userId);
                if (log.isDebugEnabled()) {
                    log.debug("uid : {}", userId);
                }
            }
        } catch (Exception e) {
            throw new HandledException("perte-connexion");
        }

        return allInfo;
    }

    // @Override
    // public String getUserID() {

    // String id = null;

    // Person infoPerson = this.getPersonDetails();
    // id = infoPerson.getUid();

    // return id;
    // }

}
