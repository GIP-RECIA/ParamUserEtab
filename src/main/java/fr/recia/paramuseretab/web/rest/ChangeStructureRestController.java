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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IStructureService;
import fr.recia.paramuseretab.service.IUserInfoService;
import fr.recia.paramuseretab.service.IUserService;
import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/changeetab/api")
public class ChangeStructureRestController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IStructureService structureService;

    @Autowired
    private IUserInfoService userInfoService;

    public String decodeJwt(String jwt) throws JsonProcessingException {
        String token = jwt.substring(7);
        String[] chunks = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = null;
        try {
            payload = new String(decoder.decode(chunks[1]));

            return payload;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("The token does not exist. {}", e);
            return payload; // or throw an exception
        }
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getInfos(
            @RequestHeader(name = "Authorization", required = true) String authorizationHeader) {
        try {
            String token = this.decodeJwt(authorizationHeader);
            Map<String, Object> infos = userInfoService.getUserInfos(token);
            return new ResponseEntity<>(infos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changeEtab(
            @RequestHeader(name = "Authorization", required = true) String authorizationHeader,
            @PathVariable("id") final String id) {
        try {
            String userID = null;

            if (id != null && authorizationHeader != null) {
                Structure structure = structureService.retrieveStructureById(id);
                if (structure == null) {

                    log.info("struct is null");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                // userId, StructID
                String token = this.decodeJwt(authorizationHeader);
                Map<String, Object> infos = userInfoService.getUserInfos(token);
                userID = (String) infos.get("id");
                String logoutUrl = (String) infos.get("aud");
                Map<String, ? extends Structure> changeableStructIds = (Map<String, ? extends Structure>) infos
                        .get("sirenStructures");

                if (!changeableStructIds.containsKey(id)) {
                    log.warn("Attempt to switch to a not allowed Structure with id: [{}] !", id);
                    return new ResponseEntity<>("not allowed to switch with id", HttpStatus.BAD_REQUEST);
                }

                userService.changeCurrentStructure(userID, structure);
                log.debug("user {} change the current structure to {}", userID, id);
                // return new ResponseEntity<>(HttpStatus.OK);
                return ResponseEntity.ok()
                        .body("{\"message\": \"Data updated successfully\", \"redirectUrl\": \"" + logoutUrl + "\"}");
            }
            log.info("id {} is null and token {} is null", id, authorizationHeader);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.getMessage();
            log.info("error change etab : ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
