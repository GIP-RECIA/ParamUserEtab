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
/**
 *
 */
package fr.recia.paramuseretab.service.impl;

import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.model.UniteAdministrativeImmatriculee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fast, isolated unit tests for {@link CachingStructureService}. The slow/concurrent load tests live in
 * {@link CachingStructureServiceLoadTest}.
 *
 * @author GIP RECIA 2013 - Maxime BOSSARD.
 */
@Slf4j
public class CachingStructureServiceTest extends AbstractCachingStructureServiceTest {

    @Test
    public void testRetrieveOneExistingEtab() throws Exception {
        log.debug("Running testRetrieveOneExistingEtab");
        final Collection<String> uais = new ArrayList<String>();
        uais.add(UAI_2);

        final Map<String, UniteAdministrativeImmatriculee> etabs = this.service.retrieveEtablissementsByCodes(uais);

        Assertions.assertNotNull(etabs, "Should return an empty collection !");
        Assertions.assertEquals(1, etabs.size(), "Should return only one etab !");
        Assertions.assertTrue(etabs.containsValue(ETAB_2), "Bad struct returned !");
        log.debug("End of testRetrieveOneExistingEtab");
    }

    @Test
    public void testRetrieveOneExistingStruct() throws Exception {
        log.debug("Running testRetrieveOneExistingStruct");

        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindAllStructures();
            }
        });
        final Collection<String> sirens = new ArrayList<String>();
        sirens.add(SIREN_2);

        final Map<String, Structure> structs = this.service.retrieveStructuresByIds(sirens);

        Assertions.assertNotNull(structs, "Should return an empty collection !");
        Assertions.assertEquals(1, structs.size(), "Should return only one etab !");
        Assertions.assertTrue(structs.containsValue(ETAB_2), "Bad struct returned !");
        log.debug("End of testRetrieveOneExistingStruct");
    }

    @Test
    public void testRetrieveSeveralExistingEtabs() throws Exception {
        log.debug("Running testRetrieveSeveralExistingEtabs");
        final Collection<String> uais = new ArrayList<String>();
        uais.add(UAI_3);
        uais.add(UAI_1);

        final Map<String, UniteAdministrativeImmatriculee> etabs = this.service.retrieveEtablissementsByCodes(uais);

        Assertions.assertNotNull(etabs, "Should return an empty collection !");
        Assertions.assertEquals(2, etabs.size(), "Should return 2 etab !");
        Assertions.assertTrue(etabs.containsValue(ETAB_1), "Bad etab in returned list !");
        Assertions.assertTrue(etabs.containsValue(ETAB_3), "Bad etab in returned list !");
        log.debug("End of testRetrieveSeveralExistingEtabs");
    }

    @Test
    public void testRetrieveSeveralExistingStructs() throws Exception {
        log.debug("Running testRetrieveSeveralExistingStructs");
        final Collection<String> sirens = new ArrayList<String>();
        sirens.add(SIREN_3);
        sirens.add(SIREN_5);

        final Map<String, Structure> structs = this.service.retrieveStructuresByIds(sirens);

        Assertions.assertNotNull(structs, "Should return an empty collection !");
        Assertions.assertEquals(2, structs.size(), "Should return 2 struct !");
        Assertions.assertTrue(structs.containsValue(ETAB_3), "Bad struct in returned list !");
        Assertions.assertTrue(structs.containsValue(STRUCT_5), "Bad struct in returned list !");
        log.debug("End of testRetrieveSeveralExistingStructs");
    }

    @Test
    public void testRetrieveNotExistingEtab() throws Exception {
        log.debug("Running testRetrieveNotExistingEtab");
        final Collection<String> uais = new ArrayList<String>();
        uais.add("NotExistingUai");

        final Map<String, UniteAdministrativeImmatriculee> etabs = this.service.retrieveEtablissementsByCodes(uais);

        Assertions.assertNotNull(etabs, "Should return an empty collection !");
        Assertions.assertEquals(0, etabs.size(), "Should return an empty collection !");
        log.debug("End of testRetrieveNotExistingEtab");
    }

    @Test
    public void testRetrieveNotExistingStruct() throws Exception {
        log.debug("Running testRetrieveNotExistingEtab");
        final Collection<String> sirens = new ArrayList<String>();
        sirens.add("NotExistingUai");

        final Map<String, Structure> structs = this.service.retrieveStructuresByIds(sirens);

        Assertions.assertNotNull(structs, "Should return an empty collection !");
        Assertions.assertEquals(0, structs.size(), "Should return an empty collection !");
        log.debug("End of testRetrieveNotExistingEtab");
    }

    @Test
    public void testRetrieveEtablissementsByUaisWithNullParam() throws Exception {
        log.debug("Running testRetrieveEtablissementsByUaisWithNullParam");
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.retrieveEtablissementsByCodes(null));
        log.debug("End of testRetrieveEtablissementsByUaisWithNullParam");
    }

    @Test
    public void testRetrieveEtablissementsBySirensWithNullParam() throws Exception {
        log.debug("Running testRetrieveEtablissementsBySirensWithNullParam");
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.retrieveStructuresByIds(null));
        log.debug("End of testRetrieveEtablissementsBySirensWithNullParam");
    }

    @Test
    public void testRetrieveEtablissementsByUaisWithEmptyParam() throws Exception {
        log.debug("Running testRetrieveEtablissementsByUaisWithEmptyParam");
        final List<String> s = Collections.emptyList();
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.retrieveEtablissementsByCodes(s));
        log.debug("End of testRetrieveEtablissementsByUaisWithEmptyParam");
    }

    @Test
    public void testRetrieveEtablissementsBySirensWithEmptyParam() throws Exception {
        log.debug("Running testRetrieveEtablissementsBySirensWithEmptyParam");
        final List<String> s = Collections.emptyList();
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.retrieveStructuresByIds(s));
        log.debug("End of testRetrieveEtablissementsBySirensWithEmptyParam");
    }

    @Test
    public void testRetrieveOneExistingEmptyEtab() throws Exception {
        log.debug("Running testRetrieveOneExistingEmptyEtab");
        final Collection<String> uais = new ArrayList<String>();
        uais.add(UAI_2);

        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<UniteAdministrativeImmatriculee>>() {

            @Override
            public Collection<UniteAdministrativeImmatriculee> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        final Map<String, UniteAdministrativeImmatriculee> etabs = this.service.retrieveEtablissementsByCodes(uais);

        Assertions.assertNotNull(etabs, "Should return an empty collection !");
        log.debug("End of testRetrieveOneExistingEmptyEtab");
    }

    @Test
    public void testRetrieveOneExistingEmptyStruct() throws Exception {
        log.debug("Running testRetrieveOneExistingEmptyStruct");
        final Collection<String> sirens = new ArrayList<String>();
        sirens.add(SIREN_2);

        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        final Map<String, Structure> structs = this.service.retrieveStructuresByIds(sirens);

        Assertions.assertNotNull(structs, "Should return an empty collection !");
        log.debug("End of testRetrieveOneExistingEmptyStruct");
    }

    @Test
    public void testRetrieveSeveralExistingEmptyEtabs() throws Exception {
        log.debug("Running testRetrieveSeveralExistingEmptyEtabs");
        final Collection<String> uais = new ArrayList<String>();
        uais.add(UAI_3);
        uais.add(UAI_1);

        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<UniteAdministrativeImmatriculee>>() {

            @Override
            public Collection<UniteAdministrativeImmatriculee> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        final Map<String, UniteAdministrativeImmatriculee> etabs = this.service.retrieveEtablissementsByCodes(uais);

        Assertions.assertNotNull(etabs, "Should return an empty collection !");
        log.debug("End of testRetrieveSeveralExistingEmptyEtabs");
    }

    @Test
    public void testRetrieveSeveralExistingEmptyStructs() throws Exception {
        log.debug("Running testRetrieveSeveralExistingEmptyStructs");
        final Collection<String> sirens = new ArrayList<String>();
        sirens.add(SIREN_3);
        sirens.add(SIREN_5);

        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        final Map<String, Structure> structs = this.service.retrieveStructuresByIds(sirens);

        Assertions.assertNotNull(structs, "Should return an empty collection !");
        log.debug("End of testRetrieveSeveralExistingEmptyStructs");
    }

    @Test
    public void testRetrieveNotExistingEmptyEtab() throws Exception {
        log.debug("Running testRetrieveNotExistingEmptyEtab");
        final Collection<String> uais = new ArrayList<String>();
        uais.add("NotExistingUai");

        final Map<String, UniteAdministrativeImmatriculee> etabs = this.service.retrieveEtablissementsByCodes(uais);

        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<UniteAdministrativeImmatriculee>>() {

            @Override
            public Collection<UniteAdministrativeImmatriculee> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        Assertions.assertNotNull(etabs, "Should return an empty collection !");
        Assertions.assertEquals(0, etabs.size(), "Should return an empty collection !");
        log.debug("End of testRetrieveNotExistingEmptyEtab");
    }

    @Test
    public void testRetrieveNotExistingEmptyStruct() throws Exception {
        log.debug("Running testRetrieveNotExistingEmptyStruct");
        final Collection<String> sirens = new ArrayList<String>();
        sirens.add("NotExistingUai");

        final Map<String, Structure> structs = this.service.retrieveStructuresByIds(sirens);

        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        Assertions.assertNotNull(structs, "Should return an empty collection !");
        Assertions.assertEquals(0, structs.size(), "Should return an empty collection !");
        log.debug("End of testRetrieveNotExistingEmptyStruct");
    }

    @Test
    public void testRetrieveEmptyEtablissementsByUaisWithNullParam() throws Exception {
        log.debug("Running testRetrieveEmptyEtablissementsByUaisWithNullParam");
        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<UniteAdministrativeImmatriculee>>() {

            @Override
            public Collection<UniteAdministrativeImmatriculee> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.retrieveEtablissementsByCodes(null));
        log.debug("End of testRetrieveEmptyEtablissementsByUaisWithNullParam");
    }

    @Test
    public void testRetrieveEmptyStructuresBySirensWithNullParam() throws Exception {
        log.debug("Running testRetrieveEmptyStructuresBySirensWithNullParam");
        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.retrieveStructuresByIds(null));
        log.debug("End of testRetrieveEmptyStructuresBySirensWithNullParam");
    }

    @Test
    public void testRetrieveEmptyEtablissementsByUaisWithEmptyParam() throws Exception {
        log.debug("Running testRetrieveEmptyEtablissementsByUaisWithEmptyParam");
        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<UniteAdministrativeImmatriculee>>() {

            @Override
            public Collection<UniteAdministrativeImmatriculee> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        final List<String> s = Collections.emptyList();
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.retrieveEtablissementsByCodes(s));
        log.debug("End of testRetrieveEmptyEtablissementsByUaisWithEmptyParam");
    }

    @Test
    public void testRetrieveEmptyStructuresBySirensWithEmptyParam() throws Exception {
        log.debug("Running testRetrieveEmptyStructuresBySirensWithEmptyParam");
        // Init DAO mock
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceTest.this.mockedFindEmptyStructures();
            }
        });

        final List<String> s = Collections.emptyList();
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.retrieveStructuresByIds(s));
        log.debug("End of testRetrieveEmptyStructuresBySirensWithEmptyParam");
    }

}
