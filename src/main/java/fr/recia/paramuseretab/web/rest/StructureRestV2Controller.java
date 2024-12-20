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

import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IStructureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/rest/v2/structures")
public class StructureRestV2Controller {

    @Autowired
    private IStructureService structureService;

    @Autowired
    private IStructureDao structureDao;

    /*
     * Return always Json data (Accept Http Header value has no impact)
     * example of call : /CONTEXT-PATH/rest/v2/structures/struct/SIREN
     */
    @GetMapping(value = "/struct/{id}", produces = "application/json")
    public ResponseEntity<? extends Structure> retrieveStructbInJson(@PathVariable("id") final String id,
            HttpServletRequest request) {

        Structure struct = structureService.retrieveStructureById(id);
        if (struct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No etablissement found for the provided id.");
        }
        return new ResponseEntity<>(struct, HttpStatus.OK);

    }

    /*
     * Return always Json data (Accept Http Header value has no impact)
     * example of call : /CONTEXT-PATH/rest/v2/structures/structs/?ids=SIREN1,SIREN2
     */
    @GetMapping(value = "/structs/", produces = "application/json")
    public ResponseEntity<Map<String, ? extends Structure>> retrieveStructsInJson(
            @RequestParam("ids") final List<String> ids, HttpServletRequest request) {

        if (ids == null || ids.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Map<String, ? extends Structure> structs = structureService.retrieveStructuresByIds(ids);
        if (structs == null || structs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No etablissement found for the provided ids.");

        }
        return new ResponseEntity<>(structs, HttpStatus.OK);

    }

    /*
     * Return always Json data (Accept Http Header value has no impact)
     * example of call : /CONTEXT-PATH/rest/v2/structures/refresh/SIREN
     */

    @PostMapping(value = "/refresh/{id}", produces = "application/json")
    public ResponseEntity<Void> refresh(@PathVariable("id") final String id, HttpServletRequest request) {
        if (id != null) {
            structureService.invalidateStructureById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
