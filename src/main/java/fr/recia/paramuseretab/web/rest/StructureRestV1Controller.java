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
package fr.recia.paramuseretab.web.rest;

import fr.recia.paramuseretab.model.UniteAdministrativeImmatriculee;
import fr.recia.paramuseretab.service.IUniteAdministrativeImmatriculeService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/rest/v1/structures")
@Slf4j
public class StructureRestV1Controller {

    @Autowired
    private IUniteAdministrativeImmatriculeService structureService;

    /*
     * Return always Json data (Accept Http Header value has no impact)
     * example of call : /CONTEXT-PATH/rest/v1/structures/etab/UAI
     */
    @GetMapping(value = "/etab/{code}", produces = "application/json")
    public ResponseEntity<UniteAdministrativeImmatriculee> retrieveEtabInJson(@PathVariable("code") final String code,
            HttpServletRequest request) {
        if (code == null || code.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UniteAdministrativeImmatriculee etab = structureService.retrieveEtablissementByCode(code.toUpperCase());
        if (etab == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No etablissement found for the provided code.");
        }
        return new ResponseEntity<>(etab, HttpStatus.OK);
    }

    /*
     * Return always Json data (Accept Http Header value has no impact)
     * example of call : /CONTEXT-PATH/rest/v1/structures/etabs/?codes=UAI1,UAI2
     */
    @GetMapping(value = "/etabs/", produces = "application/json")
    public ResponseEntity<Map<String, UniteAdministrativeImmatriculee>> retrieveEtabsInJson(
            @RequestParam("codes") final List<String> codes, HttpServletRequest request) {

        if (codes == null || codes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Collection<String> converted = new ArrayList<>(codes.size());
        for (String code : codes) {
            converted.add(code.toUpperCase());
        }

        Map<String, UniteAdministrativeImmatriculee> etabs = structureService.retrieveEtablissementsByCodes(converted);
        if (etabs == null || etabs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No etablissement found for the provided codes.");
        }

        return new ResponseEntity<>(etabs, HttpStatus.OK);
    }
}
