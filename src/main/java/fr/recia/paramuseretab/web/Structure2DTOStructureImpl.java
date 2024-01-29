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

import fr.recia.paramuseretab.model.Structure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Structure2DTOStructureImpl {

    public DTOStructure toDTO(Structure struct) {

        String structCustomDisplayName = null;
        if (!struct.getName().equals(struct.getDisplayName())) {
            structCustomDisplayName = struct.getDisplayName();
        }

        Map<String, List<String>> otherAttributes = struct.getOtherAttributes();
        String structSiteWeb = null;
        String structLogo = null;
        for (Map.Entry<String, List<String>> entry : otherAttributes.entrySet()) {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();

            if (key.equals("ESCOStructureLogo") && valueList != null && !valueList.isEmpty()) {
                structLogo = valueList.get(0);
            }
            if (key.equals("ENTStructureSiteWeb") && valueList != null && !valueList.isEmpty()) {
                structSiteWeb = valueList.get(0);
            }
        }

        // log.debug(structLogo, structCustomDisplayName, structSiteWeb);

        final DTOStructure dto = new DTOStructure();
        dto.setId(struct.getId());
        dto.setName(struct.getName());
        dto.setDisplayName(struct.getDisplayName());
        dto.setDescription(struct.getDescription());
        dto.setOtherAttributes(struct.getOtherAttributes());
        dto.setStructCustomDisplayName(structCustomDisplayName);
        dto.setStructLogo(structLogo);
        dto.setStructSiteWeb(structSiteWeb);

        return dto;

    }
}
