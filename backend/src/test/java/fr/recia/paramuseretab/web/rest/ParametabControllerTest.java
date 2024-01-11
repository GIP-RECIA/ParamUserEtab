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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.recia.paramuseretab.ParametabApplication;
import fr.recia.paramuseretab.model.Person;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IImageUrlPath;
import fr.recia.paramuseretab.service.IStructureService;
import fr.recia.paramuseretab.service.IUserInfoService;
import fr.recia.paramuseretab.service.IUserService;
import fr.recia.paramuseretab.web.DTOStructure;
import fr.recia.paramuseretab.web.Structure2DTOStructureImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

@Slf4j
@SpringBootTest(classes = ParametabApplication.class, properties = "spring.config.name=application-test")
@WebAppConfiguration
public class ParametabControllerTest {

        private MockMvc mvc;

        @Mock
        private IStructureService structureService;

        @Mock
        private IUserService iUserService;

        @Mock
        private IUserInfoService iUserInfoService;

        @Mock
        private Structure2DTOStructureImpl structure2dtoStructure;

        @Spy
        @InjectMocks
        private ParametabController parametabController;

        @InjectMocks
        private ChangeStructureRestController changeStructureRestController;

        private Person person;
        private Structure struct;
        private Map<String, List<String>> otherAttrs = new HashMap<>();
        private List<Map<String, String>> listEtab = new ArrayList<>();
        private List<String> siren = new ArrayList<>();
        private List<String> isMemberOf = new ArrayList<>();

        private Map<String, String> groupe1 = new HashMap<>();
        private Map<String, String> groupe2 = new HashMap<>();
        private Map<String, String> groupe3 = new HashMap<>();

        private static ObjectMapper mapper = new ObjectMapper();

        private MockMultipartFile file;
        private String detailsJson;
        private MockMultipartHttpServletRequestBuilder multipartReq;
        private String token;

        @PostConstruct
        public void setup() {

                /**
                 * Initialize an etablissement
                 * 
                 */
                groupe1.put("idSiren", "410xx");
                groupe1.put("etabName", "Etablissement 1");

                groupe2.put("idSiren", "4250xx");
                groupe2.put("etabName", "Etablissement 2");

                groupe3.put("idSiren", "ABCD123");
                groupe3.put("etabName", "Etablissement 3");

                listEtab.add(groupe1);
                listEtab.add(groupe2);
                listEtab.add(groupe3);

                otherAttrs.put("ESCOStructureLogo", Arrays.asList("image0.png"));
                otherAttrs.put("ENTStructureSiteWeb", Arrays.asList("etab01aze.com"));
                struct = new Structure("ABCD123", "Etablissement A1", "Etab A1", "Etab public", otherAttrs);
                person = new Person("admin123", "4250xx", siren, isMemberOf);
                mvc = MockMvcBuilders.standaloneSetup(parametabController).build();

                file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

                // Prepare the detailJson
                detailsJson = "{ \"id\":\"ABCD123\",\"name\":\"Etablissement A1\",\"displayName\":\"Etab A1\",\"description\":\"Etab public\",\"otherAttributes\":{\"ENTStructureSiteWeb\":[\"etab01aze.com\"],\"ESCOStructureLogo\":[\"image0.png\"]},\"structCustomDisplayName\":null,\"structLogo\":null,\"structSiteWeb\":null}";

                multipartReq = MockMvcRequestBuilders.multipart("/parametab/fileUpload/" + struct.getId());

                SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                token = Jwts.builder()
                                .setSubject("admin123")
                                .claim("role", "admin")
                                .signWith(secretKey)
                                .compact();
        }

        /**
         * Test : updateForm -> DisplayName et SiteWeb
         */
        @Test
        public void testUpdateForm() throws Exception {
                log.info("*** Test update form ***");
                log.debug("struct id: ", struct.getId());

                DTOStructure updateForm = new DTOStructure();
                updateForm.setId(struct.getId());
                updateForm.setName(struct.getName());
                updateForm.setDisplayName(struct.getDisplayName());
                updateForm.setDescription(struct.getDescription());
                updateForm.setOtherAttributes(struct.getOtherAttributes());
                updateForm.setStructCustomDisplayName("EX etab A1");
                updateForm.setStructSiteWeb("etab.com");

                // Mockito.when(structureDao.findOneStructureById(dtoStructure.getId())).thenReturn(dtoStructure);

                String someDecodedToken = "admin123";
                Mockito.when(parametabController.decodeJwt(token)).thenReturn(someDecodedToken);

                when(iUserInfoService.getPersonDetails(someDecodedToken)).thenReturn(person);

                when(structureService.retrieveStructureById(struct.getId())).thenReturn(struct);
                when(structure2dtoStructure.toDTO(struct)).thenReturn(updateForm);

                doNothing().when(structureService).updateStructure(updateForm, updateForm.getStructCustomDisplayName(),
                                updateForm.getStructSiteWeb(), null, updateForm.getId());

                doNothing().when(structureService).invalidateStructureById(updateForm.getId());

                MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders
                                .put("/test/api/updateV2/" + struct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(mapper.writeValueAsString(updateForm))
                                .header("Authorization", "Bearer " + token);

                log.info("mockReq : ", mockReq);

                mvc.perform(mockReq)
                                .andDo(print())
                                .andExpect(status().isOk());

                verify(structureService).updateStructure(updateForm, updateForm.getStructCustomDisplayName(),
                                updateForm.getStructSiteWeb(), null, updateForm.getId());

        }

    /**
     * Test : Get info of an etablissement 
     * @throws Exception
     */
    @Test
    public void testGetInfoEtab() throws Exception {

        when(structureService.retrieveStructureById(struct.getId())).thenReturn(struct);
        MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders.get("/test/api/parametab/" + struct.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8")
        .content(mapper.writeValueAsString(struct));
        mvc.perform(mockReq)
            .andExpect(status().isOk())
            .andDo(print());

    }

        @Test
        public void testUploadFile_Success() throws Exception {

                ParametabController spyParam = spy(parametabController);

                IImageUrlPath newURL = mock(IImageUrlPath.class);
                // doReturn(newURL).when(spyParam).calculNewImageUrlPath(any(DTOStructure.class),
                // any(IImageUrlPath.class));

                // Perform the file upload request
                multipartReq.part(new MockPart("details", detailsJson.getBytes()));

                MvcResult res = mvc.perform(multipartReq.file(file)).andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Assert the response
                Assertions.assertEquals(HttpStatus.OK.value(), res.getResponse().getStatus());
        }

        @Test
        public void testUploadFile_MissingDetailsJson() throws Exception {

                // Mock the required dependencies and set up the test environnement
                MvcResult res = mvc.perform(multipartReq.file(file))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andReturn();

                // Assert the response
                Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), res.getResponse().getStatus());

        }

        @Test
        public void testUploadFile_ErrorSavingImage() throws Exception {

                // Perform the file upload request
                multipartReq.part(new MockPart("details", detailsJson.getBytes()));

                // Mock the required dependencies and set up the test environnement
                MvcResult res = mvc.perform(multipartReq.file(file))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andReturn();

                // Assert the response
                Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), res.getResponse().getStatus());

        }

        @Test
        public void testChangeEtab() throws Exception {

                log.info("*** Test changing the current etab ***");
                String userID = null;

                // when(iUserInfoService.getUserID()).thenReturn(userID);
                when(structureService.retrieveStructureById(struct.getId())).thenReturn(struct);

                doNothing().when(iUserService).changeCurrentStructure(userID, struct);
                log.info("person : " + person.getUid());

                MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders.put("/rest/change/" + struct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(mapper.writeValueAsString(struct))
                                .header("Authorization", "Bearer " + token);

                mvc = MockMvcBuilders.standaloneSetup(changeStructureRestController).build();

                mvc.perform(mockReq)
                                .andExpect(status().isOk())
                                .andDo(print());

        }

}
