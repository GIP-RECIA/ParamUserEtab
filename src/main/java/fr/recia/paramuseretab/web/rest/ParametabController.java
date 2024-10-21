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
import com.google.gson.Gson;
import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.model.Person;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.security.HandledException;
import fr.recia.paramuseretab.service.IImageUrlPath;
import fr.recia.paramuseretab.service.ILogoStorageService;
import fr.recia.paramuseretab.service.IStructureService;
import fr.recia.paramuseretab.service.IUserInfoService;
import fr.recia.paramuseretab.service.impl.LogoStorageServiceImpl;
import fr.recia.paramuseretab.web.DTOPerson;
import fr.recia.paramuseretab.web.DTOStructure;
import fr.recia.paramuseretab.web.Person2DTOPersonImpl;
import fr.recia.paramuseretab.web.Structure2DTOStructureImpl;
import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/parametab/api")
public class ParametabController {

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IStructureService structureService;

    @Autowired
    private Structure2DTOStructureImpl structure2DTOStructure;

    @Autowired
    private Person2DTOPersonImpl person2dtoPersonImpl;

    @Autowired
    private ILogoStorageService logoStorage;

    @Autowired
    private ILogoStorageService logoStorageService;

    @Autowired
    private LogoStorageServiceImpl logoStorageServiceImpl;

    @Autowired
    private IStructureDao structureDao;

    private IImageUrlPath oldUrl;
    private IImageUrlPath newUrl;

    @ExceptionHandler(HandledException.class)
    // @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleHandledException(HandledException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    private IImageUrlPath calculOldImageUrlPath(DTOStructure str) {
        IImageUrlPath url = null;
        if (str != null) {
            String link = str.getStructLogo();
            if (link != null) {
                url = logoStorageService.makeImageUrlPath(link);
            }
        }
        return url;
    }

    private static boolean mkdir(String rep) {
        File file = new File(rep);
        // si le repertoire n'existe pas on le cree.
        boolean success = file.mkdirs() || file.isDirectory();

        // set permission
        if (success) {
            try {
                file.setReadable(true, false);
                file.setExecutable(true, false);

            } catch (Exception e) {
                log.error("error set permission for file {} : {}", rep, e);
            }

        }
        return success;

    }

    private IImageUrlPath calculNewImageUrlPath(DTOStructure str, IImageUrlPath old) {
        IImageUrlPath url = null;
        if (str != null) {
            log.info("str not null");
            int version = 0;
            if (old != null) {
                log.info("old not null");
                version = Integer.parseInt(old.getVersion());
            } else {
                log.info("old is null");
            }
            url = logoStorageService.makeImageUrlPath(str.getId(), version + 1);
            log.debug("pathuser: {}", url.getPathUser());
            if (!mkdir(url.getPathUser())) {
                url = null;
            }
        }
        return url;
    }

    public String decodeJwt(String jwt) throws JsonProcessingException {
        if (jwt == null || jwt.isEmpty()) {
            throw new IllegalArgumentException("Authorization Header is missing.");
        }
        String token = jwt.substring(7);
        String[] chunks = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> claims = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
            String sub = (String) claims.get("sub");
            return sub;
        } catch (IOException e) {
            // Handle the exception, e.g., token parsing error
            e.printStackTrace();
            return null; // or throw an exception
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> toDTOParametab(
            @RequestHeader(name = "Authorization", required = true) String authorizationHeader) {

        try {
            String userId = decodeJwt(authorizationHeader);

            Person person = userInfoService.getPersonDetails(userId);
            Map<String, Structure> getStructs = this.structureService.retrieveAllStructures();
            DTOPerson dto = person2dtoPersonImpl.toDTOParamEtab(person, getStructs);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (HandledException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            log.error("error: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /etablissments -> Get all the etablissement of a person
     */

    // @GetMapping("/")
    // public ResponseEntity<Person> getEtabs(
    // @RequestHeader(name = "Authorization", required = true) String
    // authorizationHeader) {
    // try {
    // String userId = decodeJwt(authorizationHeader);
    // return new ResponseEntity<>(userInfoService.getPersonDetails(userId),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    // }

    /**
     * Get info of an etablissement
     */
    @GetMapping("/{id}")
    public ResponseEntity<DTOStructure> convertToDTO(@PathVariable("id") final String id) {

        try {
            Structure structure = structureService.retrieveStructureById(id);
            if (structure == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            DTOStructure dtoStructure = structure2DTOStructure.toDTO(structure);
            return new ResponseEntity<>(dtoStructure, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("error get etab : ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update form etablissement
     */

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateV2(
            @RequestHeader(name = "Authorization", required = true) String authorizationHeader,
            @PathVariable("id") final String id,
            @RequestBody DTOStructure structDto) {

        try {
            String userId = decodeJwt(authorizationHeader);
            Person person = userInfoService.getPersonDetails(userId);

            if (id != null && person != null) {
                Structure structure = structureService.retrieveStructureById(id);
                if (structure == null) {

                    log.info("struct is null");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                DTOStructure dtoStructure = structure2DTOStructure.toDTO(structure);
                dtoStructure.setStructCustomDisplayName(structDto.getStructCustomDisplayName());
                dtoStructure.setStructSiteWeb(structDto.getStructSiteWeb());
                structureService.updateStructure(dtoStructure, dtoStructure.getStructCustomDisplayName(),
                        dtoStructure.getStructSiteWeb(), null, dtoStructure.getId());
                // structureService.invalidateStructureById(id); // refresh cache
                return new ResponseEntity<>(dtoStructure, HttpStatus.OK);
            } else {
                throw new HandledException("perte-connexion");
            }
        } catch (HandledException ex) {
            log.error("Verification failed: ", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.getMessage();
            log.info("error update : ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    // upload logo to disk, database, and ldap
    @PostMapping("/logoupload/{id}")
    public ResponseEntity<?> uploadFile(
            @RequestHeader(name = "Authorization", required = true) String authorizationHeader,
            @RequestPart(name = "file", required = false) MultipartFile file, @PathVariable("id") final String id) {

        try {
            String userId = decodeJwt(authorizationHeader);
            Person person = userInfoService.getPersonDetails(userId);

            if (person != null) {
                if (id != null) {
                    log.debug("idStruct: {}", id);
                    Structure structure = structureService.retrieveStructureById(id);
                    DTOStructure dtoUpd = structure2DTOStructure.toDTO(structure);

                    oldUrl = calculOldImageUrlPath(dtoUpd);
                    newUrl = calculNewImageUrlPath(dtoUpd, oldUrl);
                    String pathName = newUrl.getPathName();
                    String getNewURL = newUrl.getUrl();
                    if (newUrl != null) {
                        log.info("newUrl : {}", pathName);
                        // save the logo to disk
                        logoStorage.saving(pathName, file, id);
                    } else {
                        log.info("newUrl is null");
                        return new ResponseEntity<>("erreur : impossible de sauvegarder l'image !",
                                HttpStatus.BAD_REQUEST);
                    }

                    dtoUpd.setStructLogo(getNewURL);

                    oldUrl = newUrl;
                    structureService.updateStructure(dtoUpd, null, null, dtoUpd.getStructLogo(), id);
                    // structureService.invalidateStructureById(id); // refresh cache
                    return new ResponseEntity<>(getNewURL, HttpStatus.OK);
                }
                return new ResponseEntity<>("Erreur : l'établissement n'est pas défini !", HttpStatus.BAD_REQUEST);
            } else {
                throw new HandledException("perte-connexion");
            }
        } catch (HandledException ex) {
            log.error("Verification failed: ", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.getMessage();
            log.info("Error upload file : ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
