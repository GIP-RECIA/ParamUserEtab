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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.recia.paramuseretab.ParametabProjectApplication;
import lombok.extern.slf4j.Slf4j;

import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.model.Structure;
import fr.recia.paramuseretab.model.UniteAdministrativeImmatriculee;
import fr.mby.utils.common.test.LoadRunner;

import org.joda.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author GIP RECIA 2013 - Maxime BOSSARD.
 *
 */
@Slf4j
@SpringBootTest(classes = ParametabProjectApplication.class, properties = "spring.config.name=application-test")
@AutoConfigurationPackage(basePackages = {"fr.recia.paramuseretab.dao.impl", "fr.recia.paramuseretab.service.impl"})
public class CachingStructureServiceTest {

	private static final List<String> valStreet_1 = new ArrayList<String>(1);
	private static final List<String> valStreet_2 = new ArrayList<String>(1);
	private static final List<String> valStreet_3 = new ArrayList<String>(1);
	private static final List<String> valStreet_4 = new ArrayList<String>(1);
	private static final List<String> valStreet_5 = new ArrayList<String>(1);
	private static final List<String> modified_valStreet_5 = new ArrayList<String>(1);
	static {
		CachingStructureServiceTest.valStreet_1.add("1 rue de l'éducation");
		CachingStructureServiceTest.valStreet_2.add("10 rue de l'éducation");
		CachingStructureServiceTest.valStreet_3.add("100 rue de l'éducation");
		CachingStructureServiceTest.valStreet_4.add("1000 rue de l'éducation");
		CachingStructureServiceTest.valStreet_5.add("2 rue de l'administration");
		CachingStructureServiceTest.modified_valStreet_5.add("modified 2 rue de l'administration");
	}
	private static final Map<String, List<String>> map_1 = new HashMap<String, List<String>>(1);
	private static final Map<String, List<String>> map_2 = new HashMap<String, List<String>>(1);
	private static final Map<String, List<String>> map_3 = new HashMap<String, List<String>>(1);
	private static final Map<String, List<String>> map_4 = new HashMap<String, List<String>>(1);
	private static final Map<String, List<String>> map_5 = new HashMap<String, List<String>>(1);
	private static final Map<String, List<String>> modified_map_5 = new HashMap<String, List<String>>(1);
	static {
		CachingStructureServiceTest.map_1.put("street", valStreet_1);
		CachingStructureServiceTest.map_2.put("street", valStreet_2);
		CachingStructureServiceTest.map_3.put("street", valStreet_3);
		CachingStructureServiceTest.map_4.put("street", valStreet_4);
		CachingStructureServiceTest.map_5.put("street", valStreet_5);
		CachingStructureServiceTest.modified_map_5.put("street", modified_valStreet_5);
	}

	private static final String SIREN_1 = "SIREN_1";
	private static final String SIREN_2 = "SIREN_2";
	private static final String SIREN_3 = "SIREN_3";
	private static final String SIREN_4 = "SIREN_4";
	private static final String SIREN_5 = "SIREN_5";

	private static final String UAI_1 = "UAI_1";
	private static final String UAI_2 = "UAI_2";
	private static final String UAI_3 = "UAI_3";
	private static final String UAI_4 = "UAI_4";

	private static final UniteAdministrativeImmatriculee ETAB_1 = new UniteAdministrativeImmatriculee(SIREN_1,
			CachingStructureServiceTest.UAI_1, "name1", "name1", "desc1", CachingStructureServiceTest.map_1);
	private static final UniteAdministrativeImmatriculee ETAB_2 = new UniteAdministrativeImmatriculee(SIREN_2,
			CachingStructureServiceTest.UAI_2, "name2", "name2", "desc2", CachingStructureServiceTest.map_2);
	private static final UniteAdministrativeImmatriculee ETAB_3 = new UniteAdministrativeImmatriculee(SIREN_3,
			CachingStructureServiceTest.UAI_3, "name3", "name3", "desc3", CachingStructureServiceTest.map_3);
	private static final UniteAdministrativeImmatriculee ETAB_4 = new UniteAdministrativeImmatriculee(SIREN_4,
			CachingStructureServiceTest.UAI_4, "name4", "name4", "desc4", CachingStructureServiceTest.map_4);
	private static final Structure STRUCT_5 = new Structure(SIREN_5, "name5", "name5", "desc5",
			CachingStructureServiceTest.map_5);
	private static final Structure MODIFIED_STRUCT_5 = new Structure(SIREN_5, "modified name5", "modified name5", "modified desc5",
			CachingStructureServiceTest.map_5);

	/** All etabs returned by mocked DAo. */
	private static final List<Structure> allStructsFromDao = new ArrayList(8);
	static {
		CachingStructureServiceTest.allStructsFromDao.add(CachingStructureServiceTest.ETAB_1);
		CachingStructureServiceTest.allStructsFromDao.add(CachingStructureServiceTest.ETAB_2);
		CachingStructureServiceTest.allStructsFromDao.add(CachingStructureServiceTest.ETAB_3);
		CachingStructureServiceTest.allStructsFromDao.add(CachingStructureServiceTest.ETAB_4);
		CachingStructureServiceTest.allStructsFromDao.add(CachingStructureServiceTest.STRUCT_5);
	}

	private static final List<Structure> allStructsWithModifiedFromDao = new ArrayList<>(5);
	static {
		CachingStructureServiceTest.allStructsWithModifiedFromDao.add(CachingStructureServiceTest.ETAB_1);
		CachingStructureServiceTest.allStructsWithModifiedFromDao.add(CachingStructureServiceTest.ETAB_2);
		CachingStructureServiceTest.allStructsWithModifiedFromDao.add(CachingStructureServiceTest.ETAB_3);
		CachingStructureServiceTest.allStructsWithModifiedFromDao.add(CachingStructureServiceTest.ETAB_4);
		CachingStructureServiceTest.allStructsWithModifiedFromDao.add(CachingStructureServiceTest.MODIFIED_STRUCT_5);
	}

	private static final Collection<UniteAdministrativeImmatriculee> emptyStructsFromDao = Collections.emptyList();


	private CachingStructureService service;

    @Autowired
    @Qualifier("structuresCache")
    private Cache structureCache;

    @Autowired
    @Qualifier("etabsCodeIdCache")
    private Cache etabsCodeIdCache;

    @Mock
	private IStructureDao mockedStructureDao;

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
				return CachingStructureServiceTest.this.mockedFindAllStructures();
			}
		});
	}

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

	@Test
	public void testRetrieveOneExistingEtab() throws Exception {
		log.debug("Running testRetrieveOneExistingEtab");
		final Collection<String> uais = new ArrayList<String>();
		uais.add(CachingStructureServiceTest.UAI_2);

		final Map<String, UniteAdministrativeImmatriculee> etabs = this.service.retrieveEtablissementsByCodes(uais);

		Assertions.assertNotNull(etabs, "Should return an empty collection !");
		Assertions.assertEquals(1, etabs.size(), "Should return only one etab !");
		Assertions.assertTrue(etabs.containsValue(CachingStructureServiceTest.ETAB_2), "Bad struct returned !");
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
		sirens.add(CachingStructureServiceTest.SIREN_2);

		final Map<String, Structure> structs = this.service.retrieveStructuresByIds(sirens);

		Assertions.assertNotNull(structs, "Should return an empty collection !");
		Assertions.assertEquals(1, structs.size(), "Should return only one etab !");
		Assertions.assertTrue(structs.containsValue(CachingStructureServiceTest.ETAB_2), "Bad struct returned !");
		log.debug("End of testRetrieveOneExistingStruct");
	}

	@Test
	public void testRetrieveSeveralExistingEtabs() throws Exception {
		log.debug("Running testRetrieveSeveralExistingEtabs");
		final Collection<String> uais = new ArrayList<String>();
		uais.add(CachingStructureServiceTest.UAI_3);
		uais.add(CachingStructureServiceTest.UAI_1);

		final Map<String, UniteAdministrativeImmatriculee> etabs = this.service.retrieveEtablissementsByCodes(uais);

		Assertions.assertNotNull(etabs, "Should return an empty collection !");
		Assertions.assertEquals(2, etabs.size(), "Should return 2 etab !");
		Assertions.assertTrue(etabs.containsValue(CachingStructureServiceTest.ETAB_1), "Bad etab in returned list !");
		Assertions.assertTrue(etabs.containsValue(CachingStructureServiceTest.ETAB_3), "Bad etab in returned list !");
		log.debug("End of testRetrieveSeveralExistingEtabs");
	}

	@Test
	public void testRetrieveSeveralExistingStructs() throws Exception {
		log.debug("Running testRetrieveSeveralExistingStructs");
		final Collection<String> sirens = new ArrayList<String>();
		sirens.add(CachingStructureServiceTest.SIREN_3);
		sirens.add(CachingStructureServiceTest.SIREN_5);

		final Map<String, Structure> structs = this.service.retrieveStructuresByIds(sirens);

		Assertions.assertNotNull(structs, "Should return an empty collection !");
		Assertions.assertEquals(2, structs.size(), "Should return 2 struct !");
		Assertions.assertTrue(structs.containsValue(CachingStructureServiceTest.ETAB_3), "Bad struct in returned list !");
		Assertions.assertTrue(structs.containsValue(CachingStructureServiceTest.STRUCT_5), "Bad struct in returned list !");
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
		uais.add(CachingStructureServiceTest.UAI_2);

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
		sirens.add(CachingStructureServiceTest.SIREN_2);

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
		uais.add(CachingStructureServiceTest.UAI_3);
		uais.add(CachingStructureServiceTest.UAI_1);

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
		sirens.add(CachingStructureServiceTest.SIREN_3);
		sirens.add(CachingStructureServiceTest.SIREN_5);

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

	@Test
	public void loadTestRetrieveSeveralExistingEtabs() throws Exception {
		log.debug("Running loadTestRetrieveSeveralExistingEtabs");
		this.service.setCachingDuration(100);

		// define a random on etabs retrived to test when the ldap dao returned errors
		Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {

			@Override
			public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
				return CachingStructureServiceTest.this.randomMockedFindAllStructures();
			}
		});

		long startTime = System.currentTimeMillis();

		int nbIterations = 100000;
		LoadRunner<CachingStructureServiceTest, Void> runner = new LoadRunner<CachingStructureServiceTest, Void>(
				nbIterations, 100, this) {

			@Override
			protected Void loadTest(CachingStructureServiceTest unitTest) throws Exception {

				final Collection<String> uais = new ArrayList<String>();
				uais.add(CachingStructureServiceTest.UAI_3);
				uais.add(CachingStructureServiceTest.UAI_1);

				final Map<String, UniteAdministrativeImmatriculee> etabs = CachingStructureServiceTest.this.service
						.retrieveEtablissementsByCodes(uais);

				Assertions.assertNotNull(etabs, "Should return an empty collection !");
				//Manage the case of the randomMockedFindAllEtablissements returned 0 etabs
				if (etabs.size() != 0) {
					Assertions.assertEquals(2, etabs.size(), "Should return 2 etabs !");
					Assertions.assertTrue(etabs.containsValue(CachingStructureServiceTest.ETAB_1), "Bad etab in returned list !");
					Assertions.assertTrue(etabs.containsValue(CachingStructureServiceTest.ETAB_3), "Bad etab in returned list !");
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
				return CachingStructureServiceTest.this.randomMockedFindAllStructures();
			}
		});

		long startTime = System.currentTimeMillis();

		int nbIterations = 100000;
		LoadRunner<CachingStructureServiceTest, Void> runner = new LoadRunner<CachingStructureServiceTest, Void>(
				nbIterations, 100, this) {

			@Override
			protected Void loadTest(CachingStructureServiceTest unitTest) throws Exception {

				final Collection<String> sirens = new ArrayList<String>();
				sirens.add(CachingStructureServiceTest.SIREN_5);
				sirens.add(CachingStructureServiceTest.SIREN_1);

				Random rd = new Random();

				if (rd.nextBoolean()) {
					CachingStructureServiceTest.this.service.invalidateStructureById(CachingStructureServiceTest.SIREN_5);
				}
				if (rd.nextBoolean()) {
					CachingStructureServiceTest.this.service.invalidateStructureById(CachingStructureServiceTest.SIREN_1);
				}

				final Map<String, Structure> structs = CachingStructureServiceTest.this.service
						.retrieveStructuresByIds(sirens);

				Assertions.assertNotNull(structs, "Should return an empty collection !");
				//Manage the case of the randomMockedFindAllEtablissements returned 0 etabs
				if (structs.size() != 0) {
					Assertions.assertEquals(2, structs.size(), "Should return 2 etabs !");
					Assertions.assertTrue(structs.containsValue(CachingStructureServiceTest.ETAB_1), "Bad etab in returned list !");
					Assertions.assertTrue(structs.containsValue(CachingStructureServiceTest.STRUCT_5), "Bad etab in returned list !");
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
				return CachingStructureServiceTest.MODIFIED_STRUCT_5;
			}
		});

		long startTime = System.currentTimeMillis();

		// for the first call structures are retrieved from allStruct
		Structure struct = CachingStructureServiceTest.this.service.retrieveStructureById(CachingStructureServiceTest.SIREN_5);
		Assertions.assertNotNull(struct, "Structure retrieved should not be null");
		Assertions.assertEquals(CachingStructureServiceTest.STRUCT_5, struct, "Bad struct in returned list !");

		Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {
			@Override
			public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
				return CachingStructureServiceTest.allStructsWithModifiedFromDao;
			}
		});

		CachingStructureServiceTest.this.service.invalidateStructureById(CachingStructureServiceTest.SIREN_5);
		long invalidated = System.currentTimeMillis();
		struct = CachingStructureServiceTest.this.service.retrieveStructureById(CachingStructureServiceTest.SIREN_5);

		// on check si on n'est pas en dehors de la durée de refresh et qu'on récupère toujours l'ancien non mis à jour
		if (!((invalidated + this.service.getRefreshExpiredDuration()) > System.currentTimeMillis())) {
			Assertions.assertEquals(CachingStructureServiceTest.STRUCT_5, struct, "Bad struct in returned list !");
		}
		while (!((invalidated + this.service.getRefreshExpiredDuration()) < System.currentTimeMillis())) {
			// wait
		}
		// should be refreshed for one struct and not globally
		struct = CachingStructureServiceTest.this.service.retrieveStructureById(CachingStructureServiceTest.SIREN_5);
		Assertions.assertNotNull(struct, "Structure modified retrieved should not be null");
		Assertions.assertEquals(CachingStructureServiceTest.MODIFIED_STRUCT_5, struct, "Bad struct in returned list !");

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
				return CachingStructureServiceTest.allStructsFromDao;
			}
		});

		long startTime = System.currentTimeMillis();

		Structure struct = CachingStructureServiceTest.this.service.retrieveStructureById(CachingStructureServiceTest.SIREN_5);
		long loaded = System.currentTimeMillis();

		Assertions.assertNotNull(struct, "Structure retrieved should not be null");
		Assertions.assertEquals(CachingStructureServiceTest.STRUCT_5, struct, "Bad struct in returned list !");

		Mockito.when(mockedStructureDao.findAllStructures()).then(new Answer<Collection<? extends Structure>>() {
			@Override
			public Collection<? extends Structure> answer(InvocationOnMock invocation) throws Throwable {
				return CachingStructureServiceTest.allStructsWithModifiedFromDao;
			}
		});

		CachingStructureServiceTest.this.service.invalidateStructureById(CachingStructureServiceTest.SIREN_5);

		while (!((loaded + this.service.getCachingDuration()) < System.currentTimeMillis())) {
			// wait
		}

		struct = CachingStructureServiceTest.this.service.retrieveStructureById(CachingStructureServiceTest.SIREN_5);
		Assertions.assertNotNull(struct, "Structure modified retrieved should not be null");
		Assertions.assertEquals(CachingStructureServiceTest.MODIFIED_STRUCT_5, struct, "Bad struct modified  in returned list !");

		Assertions.assertTrue(CachingStructureServiceTest.this.service.getExpiredIds().isEmpty(), "The Invalidated List should be empty !");

		long endTime = System.currentTimeMillis();

		log.info("Test take {} ms.", (endTime - startTime));
		log.debug("End of loadTestInvalidatingStruct2");
	}

	/**
	 * @return
	 */
	private Collection<? extends Structure> mockedFindAllStructures() {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return CachingStructureServiceTest.allStructsFromDao;
	}

	/**
	 * @return
	 */
	private Collection<UniteAdministrativeImmatriculee> mockedFindEmptyStructures() {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return CachingStructureServiceTest.emptyStructsFromDao;
	}

	/**
	 * @return
	 */
	private Collection<? extends Structure> randomMockedFindAllStructures() {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Random random = new Random();
		int n = random.nextInt(10);
		if (n > 7) {
			log.debug("MockDao returns all Etabs");
			return CachingStructureServiceTest.allStructsFromDao;
		}
		log.debug("MockDao returns empty Etabs");
		return CachingStructureServiceTest.emptyStructsFromDao;
	}

}
