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

import fr.mby.utils.common.test.LoadRunner;
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
import java.util.Map;
import java.util.Random;

/**
 * Slow, concurrent load/stress tests for {@link CachingStructureService}. Kept separate from
 * {@link CachingStructureServiceTest} so the fast unit tests can run (and be excluded) independently - e.g.
 * {@code -Dtest=!*LoadTest} to skip these during routine development.
 *
 * @author GIP RECIA 2013 - Maxime BOSSARD.
 */
@Slf4j
public class CachingStructureServiceLoadTest extends AbstractCachingStructureServiceTest {

    @Test
    public void loadTestRetrieveSeveralExistingEtabs() throws Exception {
        log.debug("Running loadTestRetrieveSeveralExistingEtabs");
        this.service.setCachingDuration(100);

        // define a random on etabs retrived to test when the ldap dao returned errors
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceLoadTest.this.randomMockedFindAllStructures();
            }
        });

        long startTime = System.currentTimeMillis();

        int nbIterations = 5000;
        LoadRunner<CachingStructureServiceLoadTest, Void> runner = new LoadRunner<CachingStructureServiceLoadTest, Void>(
            nbIterations, 100, this) {

            @Override
            protected Void loadTest(CachingStructureServiceLoadTest unitTest) throws Exception {

                final Collection<String> uais = new ArrayList<String>();
                uais.add(UAI_3);
                uais.add(UAI_1);

                final Map<String, UniteAdministrativeImmatriculee> etabs = CachingStructureServiceLoadTest.this.service
                    .retrieveEtablissementsByCodes(uais);

                Assertions.assertNotNull(etabs, "Should return an empty collection !");
                //Manage the case of the randomMockedFindAllEtablissements returned 0 etabs
                if (etabs.size() != 0) {
                    Assertions.assertEquals(2, etabs.size(), "Should return 2 etabs !");
                    Assertions.assertTrue(etabs.containsValue(ETAB_1), "Bad etab in returned list !");
                    Assertions.assertTrue(etabs.containsValue(ETAB_3), "Bad etab in returned list !");
                }

                return null;
            }
        };

        Assertions.assertEquals(runner.getFinishedTestWithoutErrorCount(), nbIterations, "LoadRunner run failed !");

        long endTime = System.currentTimeMillis();

        log.info("Test took {} ms.", (endTime - startTime));
        log.debug("End of loadTestRetrieveSeveralExistingEtabs");
    }

    @Test
    public void loadTestRetrieveSeveralExistingStructs() throws Exception {
        log.debug("Running loadTestRetrieveSeveralExistingStructs");
        this.service.setCachingDuration(100);

        // define a random on etabs retrived to test when the ldap dao returned errors
        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return CachingStructureServiceLoadTest.this.randomMockedFindAllStructures();
            }
        });

        long startTime = System.currentTimeMillis();

        int nbIterations = 5000;
        LoadRunner<CachingStructureServiceLoadTest, Void> runner = new LoadRunner<CachingStructureServiceLoadTest, Void>(
            nbIterations, 100, this) {

            @Override
            protected Void loadTest(CachingStructureServiceLoadTest unitTest) throws Exception {

                final Collection<String> sirens = new ArrayList<String>();
                sirens.add(SIREN_5);
                sirens.add(SIREN_1);

                Random rd = new Random();

                if (rd.nextBoolean()) {
                    CachingStructureServiceLoadTest.this.service.invalidateStructureById(SIREN_5);
                }
                if (rd.nextBoolean()) {
                    CachingStructureServiceLoadTest.this.service.invalidateStructureById(SIREN_1);
                }

                final Map<String, Structure> structs = CachingStructureServiceLoadTest.this.service
                    .retrieveStructuresByIds(sirens);

                Assertions.assertNotNull(structs, "Should return an empty collection !");
                //Manage the case of the randomMockedFindAllEtablissements returned 0 etabs
                if (structs.size() != 0) {
                    Assertions.assertEquals(2, structs.size(), "Should return 2 etabs !");
                    Assertions.assertTrue(structs.containsValue(ETAB_1), "Bad etab in returned list !");
                    Assertions.assertTrue(structs.containsValue(STRUCT_5), "Bad etab in returned list !");
                }


                return null;
            }
        };

        Assertions.assertEquals(runner.getFinishedTestWithoutErrorCount(), nbIterations, "LoadRunner run failed !");

        long endTime = System.currentTimeMillis();

        log.info("Test took {} ms.", (endTime - startTime));
        log.debug("End of loadTestRetrieveSeveralExistingStructs");
    }

    // On refreshed struct before global refresh but not before RefreshExpiredDuration
    @Test
    public void loadTestInvalidatingStruct() throws Exception {
        log.debug("Running loadTestInvalidatingStruct");
        this.service.setCachingDuration(700);
        this.service.setRefreshExpiredDuration(400);

        Mockito.when(mockedStructureDao.findOneStructureById(Mockito.anyString())).then(new Answer<Structure>() {
            @Override
            public Structure answer(InvocationOnMock invocation) throws Throwable {
                return MODIFIED_STRUCT_5;
            }
        });

        long startTime = System.currentTimeMillis();

        // for the first call structures are retrieved from allStruct
        Structure struct = this.service.retrieveStructureById(SIREN_5);
        Assertions.assertNotNull(struct, "Structure retrieved should not be null");
        Assertions.assertEquals(STRUCT_5, struct, "Bad struct in returned list !");

        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {
            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return allStructsWithModifiedFromDao;
            }
        });

        this.service.invalidateStructureById(SIREN_5);
        long invalidated = System.currentTimeMillis();
        struct = this.service.retrieveStructureById(SIREN_5);

        // on check si on n'est pas en dehors de la durée de refresh et qu'on récupère toujours l'ancien non mis à jour
        if (!((invalidated + this.service.getRefreshExpiredDuration()) > System.currentTimeMillis())) {
            Assertions.assertEquals(STRUCT_5, struct, "Bad struct in returned list !");
        }
        while (!((invalidated + this.service.getRefreshExpiredDuration()) < System.currentTimeMillis())) {
            // wait
        }
        // should be refreshed for one struct and not globally
        struct = this.service.retrieveStructureById(SIREN_5);
        Assertions.assertNotNull(struct, "Structure modified retrieved should not be null");
        Assertions.assertEquals(MODIFIED_STRUCT_5, struct, "Bad struct in returned list !");

        long endTime = System.currentTimeMillis();

        log.info("Test take {} ms.", (endTime - startTime));
        log.debug("End of loadTestInvalidatingStruct");
    }

    // on global refresh only
    @Test
    public void loadTestInvalidatingStruct2() throws Exception {
        log.debug("Running loadTestInvalidatingStruct2");
        this.service.setCachingDuration(500);
        this.service.setRefreshExpiredDuration(200);

        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {
            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return allStructsFromDao;
            }
        });

        long startTime = System.currentTimeMillis();

        Structure struct = this.service.retrieveStructureById(SIREN_5);
        long loaded = System.currentTimeMillis();

        Assertions.assertNotNull(struct, "Structure retrieved should not be null");
        Assertions.assertEquals(STRUCT_5, struct, "Bad struct in returned list !");

        Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {
            @Override
            public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
                return allStructsWithModifiedFromDao;
            }
        });

        this.service.invalidateStructureById(SIREN_5);

        while (!((loaded + this.service.getCachingDuration()) < System.currentTimeMillis())) {
            // wait
        }

        struct = this.service.retrieveStructureById(SIREN_5);
        Assertions.assertNotNull(struct, "Structure modified retrieved should not be null");
        Assertions.assertEquals(MODIFIED_STRUCT_5, struct, "Bad struct modified  in returned list !");

        Assertions.assertTrue(this.service.getExpiredIds().isEmpty(), "The Invalidated List should be empty !");

        long endTime = System.currentTimeMillis();

        log.info("Test take {} ms.", (endTime - startTime));
        log.debug("End of loadTestInvalidatingStruct2");
    }

}
