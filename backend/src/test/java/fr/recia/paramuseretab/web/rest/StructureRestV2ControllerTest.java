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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import fr.recia.paramuseretab.ParametabApplication;
import lombok.extern.slf4j.Slf4j;
import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.service.IStructureService;
import fr.recia.paramuseretab.service.impl.CachingStructureService;

import org.joda.time.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@SpringBootTest(classes = ParametabApplication.class, properties = "spring.config.name=application-test")
@WebAppConfiguration
@Slf4j
public class StructureRestV2ControllerTest {

    @Mock
    private IStructureDao mockedStructureDao;

    private IStructureService structureService;

    private MockMvc mvc;

    @InjectMocks
    private StructureRestV2Controller structureRestV2Controller;

    private List<Structure> structures = new ArrayList<>();

    private Structure structure1;
    private Structure structure2;
    private Structure structure3;

    private String SIREN_1= "SIREN_1";
    private String SIREN_2= "SIREN_3";
    private String SIREN_3= "SIREN_3";

    private Map<String, List<String>> otherAttrs = new HashMap<>();
    

    @Autowired
    @Qualifier("structuresCache")
    private Cache structureCache;

    @Autowired
    @Qualifier("etabsCodeIdCache")
    private Cache etabsCodeIdCache;

    private AutoCloseable closeable;


//    @BeforeEach
//    public void setup() {
//        closeable = MockitoAnnotations.openMocks(this);
//
//    }

    @PreDestroy
    void closeService() throws Exception {
        closeable.close();
    }


    @PostConstruct
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);

        mvc = MockMvcBuilders.standaloneSetup(structureRestV2Controller).build();

        structureService = new CachingStructureService();
        ReflectionTestUtils.setField(structureService, "structureCache", this.structureCache);
        ReflectionTestUtils.setField(structureService, "etabsCodeIdCache", this.etabsCodeIdCache);
        ReflectionTestUtils.setField(structureService, "structureDao", this.mockedStructureDao);
        ReflectionTestUtils.setField(structureService, "cachingDuration", Duration.millis(Long.valueOf("1000")));
        log.info("Configuring Caching Structure Service {}", structureService);

        ReflectionTestUtils.setField(structureRestV2Controller, "structureService", structureService);
        ReflectionTestUtils.setField(structureRestV2Controller, "structureDao", this.mockedStructureDao);

        otherAttrs.put("street", Arrays.asList("Adresse 1"));
        otherAttrs.put("phone", Arrays.asList("+33 1 10 10 10 10", "+33 2 20 20 20 20"));

        structure1 = new Structure(SIREN_1, "namé1", "nameé1", "descé1", otherAttrs);
        structure2 = new Structure(SIREN_2, "name2", "name2", "desc2", otherAttrs);
        structure3 = new Structure(SIREN_3, "name3", "name3", "desc3", otherAttrs);
        structures.add(structure1);
        structures.add(structure2);
        structures.add(structure3);

        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return structures;
            }
        });
    }

    @Test
    public void testRefresh() throws Exception {
        mvc.perform(
                post("/rest/v2/structures/refresh/" + SIREN_1).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testretrieveStructFromId() throws Exception {
        mvc.perform(
                get("/rest/v2/structures/struct/" + SIREN_1).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(SIREN_1))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value(structure1.getName()))
                .andExpect(jsonPath("$.displayName").exists())
                .andExpect(jsonPath("$.displayName").value(structure1.getDisplayName()))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.description").value(structure1.getDescription()))
                .andExpect(jsonPath("$.otherAttributes").exists())
                .andExpect(jsonPath("$.otherAttributes.street").exists())
                .andExpect(jsonPath("$.otherAttributes.street").isArray())
                .andExpect(jsonPath("$.otherAttributes.street[0]").value(otherAttrs.get("street").get(0)))
                .andExpect(jsonPath("$.otherAttributes.phone").exists())
                .andExpect(jsonPath("$.otherAttributes.phone").isArray())
        ;
    }

}
