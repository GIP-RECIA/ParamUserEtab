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

    public DTOPerson toDTOParamEtab(Person person) throws HandledException {

        final DTOPerson dto = new DTOPerson();

        try {

            List<Map<String, String>> listEtab = new ArrayList<>();

            // TO DO: retrieve the name of etab for getting the idSiren
            if (!person.getIsMemberOf().isEmpty()) {

                List<String> etabNames = person.getIsMemberOf();
                for (String value : etabNames) {
                    String name = null;
                    Map<String, String> itemMap = new HashMap<>();

                    // find the last occurence of '('
                    int startIndex = value.lastIndexOf('(');

                    if (startIndex != -1) {
                        // extract the substring from startIndex + 1 to the end
                        String result = value.substring(startIndex + 1, value.length() - 1);

                        name = this.uai.getSiren(result, null);
                    } else {
                        name = this.uai.getSiren(null, value);
                    }

                    itemMap.put("idSiren", name);
                    itemMap.put("etabName", value);
                    listEtab.add(itemMap);
                }

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
            throw new HandledException("permission-refusee");
        }

        return dto;
    }

    public DTOPerson toDTOChangeEtab(Person person) {

        List<Map<String, String>> result = new ArrayList<>();

        // TO DO: retrieve the siren for getting the displayname of etab (structure)
        if (!person.getSiren().isEmpty()) {

            List<String> ids = person.getSiren();
            String etabName = null;
            for (String idSiren : ids) {
                etabName = this.uai.getEtabName(idSiren);
                Map<String, String> itemMap = new HashMap<>();
                itemMap.put("idSiren", idSiren);
                itemMap.put("etabName", etabName);
                result.add(itemMap);

            }

        }

        final DTOPerson dto = new DTOPerson();
        dto.setUid(person.getUid());
        dto.setCurrentStruct(person.getCurrentStruct());
        dto.setSiren(person.getSiren());
        dto.setIsMemberOf(person.getIsMemberOf());
        dto.setListEtab(result);

        return dto;
    }
}
