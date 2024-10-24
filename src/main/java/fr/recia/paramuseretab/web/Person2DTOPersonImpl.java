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
package fr.recia.paramuseretab.web;

import fr.recia.paramuseretab.model.Person;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.security.HandledException;
import fr.recia.paramuseretab.service.IUniteAdministrativeImmatriculeService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class Person2DTOPersonImpl {

    @Autowired
    private IUniteAdministrativeImmatriculeService uai;

    public DTOPerson toDTOParamEtab(Person person, Map<String, Structure> structs) throws HandledException {

        final DTOPerson dto = new DTOPerson();

        try {

            List<Map<String, String>> listEtab = new ArrayList<>();

            // TO DO: retrieve the name of etab for getting the idSiren
            if (!person.getIsMemberOf().isEmpty()) {

                List<String> etabNames = person.getIsMemberOf();
                for (String value : etabNames) {
                    Map<String, String> itemMap = new HashMap<>();
                    Map<String, String> getInfoEtab;

                    // find the last occurence of '('
                    int startIndex = value.lastIndexOf('(');
                    String codeUAI = null;

                    if (startIndex != -1) {
                        // extract the substring from startIndex + 1 to the end
                        codeUAI = value.substring(startIndex + 1, value.length() - 1);
                    }
                    getInfoEtab = this.uai.getSiren(codeUAI, value, structs);

                    itemMap.put("idSiren", getInfoEtab.get("id"));
                    itemMap.put("etabName", getInfoEtab.get("name"));
                    listEtab.add(itemMap);
                }

                // Remove maps with null "etabName" or "idSiren" before sorting
                listEtab.removeIf(map -> map.get("etabName") == null || map.get("idSiren") == null);

                Collections.sort(listEtab,
                        (map1, map2) -> map1.get("etabName").compareToIgnoreCase(map2.get("etabName")));

            }

            dto.setUid(person.getUid());
            dto.setCurrentStruct(person.getCurrentStruct());
            dto.setSiren(person.getSiren());
            dto.setIsMemberOf(person.getIsMemberOf());
            dto.setListEtab(listEtab);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("info person : {}", dto.toString());
            }
            log.error("error DTOPerson parametab: {}", e);
            throw new HandledException("permission-refusee");
        }

        return dto;
    }

    public DTOPerson toDTOChangeEtab(Person person) {

        final DTOPerson dto = new DTOPerson();

        try {

            List<Map<String, String>> listChangeEtab = new ArrayList<>();

            if (!person.getSiren().isEmpty()) {

                List<String> idSirens = person.getSiren();
                String etabName = null;
                for (String id : idSirens) {
                    etabName = this.uai.getEtabName(id);
                    Map<String, String> itemMap = new HashMap<>();
                    itemMap.put("idSiren", id);
                    itemMap.put("etabName", etabName);
                    listChangeEtab.add(itemMap);

                }
            }

            dto.setUid(person.getUid());
            dto.setCurrentStruct(person.getCurrentStruct());
            dto.setSiren(person.getSiren());
            dto.setIsMemberOf(person.getIsMemberOf());
            dto.setListEtab(listChangeEtab);

        } catch (Exception e) {
            log.error("error DTOPerson changeEtab: {}", e);
        }

        return dto;
    }
}
