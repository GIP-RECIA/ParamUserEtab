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
package fr.recia.paramuseretab.service.impl;

import fr.recia.paramuseretab.ParametabApplication;
import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.model.UniteAdministrativeImmatriculee;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Shared fixture data, mocks and setup/teardown for {@link CachingStructureServiceTest} (fast unit tests) and
 * {@link CachingStructureServiceLoadTest} (slow concurrency tests) - split out so the two can be run/excluded
 * independently.
 *
 * @author GIP RECIA 2013 - Maxime BOSSARD.
 */
@Slf4j
@SpringBootTest(classes = ParametabApplication.class, properties = "spring.config.name=application-test")
@AutoConfigurationPackage(basePackages = {"fr.recia.paramuseretab.dao.impl", "fr.recia.paramuseretab.service.impl"})
abstract class AbstractCachingStructureServiceTest {

    protected static final List<String> valStreet_1 = new ArrayList<String>(1);
    protected static final List<String> valStreet_2 = new ArrayList<String>(1);
    protected static final List<String> valStreet_3 = new ArrayList<String>(1);
    protected static final List<String> valStreet_4 = new ArrayList<String>(1);
    protected static final List<String> valStreet_5 = new ArrayList<String>(1);
    protected static final List<String> modified_valStreet_5 = new ArrayList<String>(1);

    static {
        valStreet_1.add("1 rue de l'éducation");
        valStreet_2.add("10 rue de l'éducation");
        valStreet_3.add("100 rue de l'éducation");
        valStreet_4.add("1000 rue de l'éducation");
        valStreet_5.add("2 rue de l'administration");
        modified_valStreet_5.add("modified 2 rue de l'administration");
    }

    protected static final Map<String, List<String>> map_1 = new HashMap<String, List<String>>(1);
    protected static final Map<String, List<String>> map_2 = new HashMap<String, List<String>>(1);
    protected static final Map<String, List<String>> map_3 = new HashMap<String, List<String>>(1);
    protected static final Map<String, List<String>> map_4 = new HashMap<String, List<String>>(1);
    protected static final Map<String, List<String>> map_5 = new HashMap<String, List<String>>(1);
    protected static final Map<String, List<String>> modified_map_5 = new HashMap<String, List<String>>(1);

    static {
        map_1.put("street", valStreet_1);
        map_2.put("street", valStreet_2);
        map_3.put("street", valStreet_3);
        map_4.put("street", valStreet_4);
        map_5.put("street", valStreet_5);
        modified_map_5.put("street", modified_valStreet_5);
    }

    protected static final String SIREN_1 = "SIREN_1";
    protected static final String SIREN_2 = "SIREN_2";
    protected static final String SIREN_3 = "SIREN_3";
    protected static final String SIREN_4 = "SIREN_4";
    protected static final String SIREN_5 = "SIREN_5";

    protected static final String UAI_1 = "UAI_1";
    protected static final String UAI_2 = "UAI_2";
    protected static final String UAI_3 = "UAI_3";
    protected static final String UAI_4 = "UAI_4";

    protected static final UniteAdministrativeImmatriculee ETAB_1 = new UniteAdministrativeImmatriculee(SIREN_1,
        UAI_1, "name1", "name1", "desc1", map_1);
    protected static final UniteAdministrativeImmatriculee ETAB_2 = new UniteAdministrativeImmatriculee(SIREN_2,
        UAI_2, "name2", "name2", "desc2", map_2);
    protected static final UniteAdministrativeImmatriculee ETAB_3 = new UniteAdministrativeImmatriculee(SIREN_3,
        UAI_3, "name3", "name3", "desc3", map_3);
    protected static final UniteAdministrativeImmatriculee ETAB_4 = new UniteAdministrativeImmatriculee(SIREN_4,
        UAI_4, "name4", "name4", "desc4", map_4);
    protected static final Structure STRUCT_5 = new Structure(SIREN_5, "name5", "name5", "desc5", map_5);
    protected static final Structure MODIFIED_STRUCT_5 = new Structure(SIREN_5, "modified name5", "modified name5",
        "modified desc5", map_5);

    /**
     * All etabs returned by mocked DAo.
     */
    protected static final List<Structure> allStructsFromDao = new ArrayList(8);

    static {
        allStructsFromDao.add(ETAB_1);
        allStructsFromDao.add(ETAB_2);
        allStructsFromDao.add(ETAB_3);
        allStructsFromDao.add(ETAB_4);
        allStructsFromDao.add(STRUCT_5);
    }

    protected static final List<Structure> allStructsWithModifiedFromDao = new ArrayList<>(5);

    static {
        allStructsWithModifiedFromDao.add(ETAB_1);
        allStructsWithModifiedFromDao.add(ETAB_2);
        allStructsWithModifiedFromDao.add(ETAB_3);
        allStructsWithModifiedFromDao.add(ETAB_4);
        allStructsWithModifiedFromDao.add(MODIFIED_STRUCT_5);
    }

    protected static final Collection<UniteAdministrativeImmatriculee> emptyStructsFromDao = Collections.emptyList();


    protected CachingStructureService service;

    @Autowired
    @Qualifier("structuresCache")
    protected Cache structureCache;

    @Autowired
    @Qualifier("etabsCodeIdCache")
    protected Cache etabsCodeIdCache;

    @Mock
    protected IStructureDao mockedStructureDao;

    private AutoCloseable closeable;


    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new CachingStructureService();
        ReflectionTestUtils.setField(service, "structureCache", this.structureCache);
        ReflectionTestUtils.setField(service, "etabsCodeIdCache", this.etabsCodeIdCache);
        ReflectionTestUtils.setField(service, "structureDao", this.mockedStructureDao);
        ReflectionTestUtils.setField(service, "cachingDuration", Duration.millis(Long.valueOf("1000")));
        log.info("Configuring Caching Structure Service {}", service);
        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return AbstractCachingStructureServiceTest.this.mockedFindAllStructures();
            }
        });
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    protected Collection<? extends Structure> mockedFindAllStructures() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allStructsFromDao;
    }

    protected Collection<UniteAdministrativeImmatriculee> mockedFindEmptyStructures() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return emptyStructsFromDao;
    }

    protected Collection<? extends Structure> randomMockedFindAllStructures() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int n = random.nextInt(10);
        if (n > 7) {
            log.debug("MockDao returns all Etabs");
            return allStructsFromDao;
        }
        log.debug("MockDao returns empty Etabs");
        return emptyStructsFromDao;
    }

}
