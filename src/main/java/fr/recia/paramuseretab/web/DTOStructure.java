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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DTOStructure extends Structure {

    @Size(max = 25, message = "custom name must not exceed 25 characters.")
    private String structCustomDisplayName;

    private String structLogo;

    private String structSiteWeb;

    public DTOStructure(String id, String name, String displayName, String description, Map<String, List<String>> otherAttributes, String structCustomDisplayName, String structLogo, String structSiteWeb) {
        super(id, name, displayName, description, otherAttributes);
        this.structCustomDisplayName = structCustomDisplayName;
        this.structLogo = structLogo;
        this.structSiteWeb = structSiteWeb;
    }

}
