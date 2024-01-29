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
package fr.recia.paramuseretab.dao.impl;

import fr.recia.paramuseretab.ParametabApplication;
import fr.recia.paramuseretab.dao.IStructureDao;
import fr.recia.paramuseretab.model.Structure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.core.LdapTemplate;

import java.util.Collection;

/**
 * FIXME: Unable to load the Apache Directory for the test !
 *
 * @author GIP RECIA 2013 - Maxime BOSSARD.
 */
@Slf4j
@SpringBootTest(classes = ParametabApplication.class, properties = "spring.config.name=application-test")
@AutoConfigurationPackage(basePackages = "fr.recia.paramuseretab.dao.impl")
public class LdapStructureDaoTest {

    private static int port = 42539;
    private static String defaultPartitionSuffix = "dc=esco-centre,dc=fr";
    private static String defaultPartitionName = "root";
    private static String principal = "uid=admin,ou=system";
    private static String credentials = "secret";

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    @Qualifier("ldapStructureDao")
    private IStructureDao dao;

	/*@Value(value = "classpath:esco-structure-schema.ldif")
	private Resource escoStructuresSchemaLdif;

	@Value(value = "classpath:init.ldif")
	private Resource initLdif;*/

    @RegisterExtension
    public final static LdapServerRule server = new LdapServerRule(defaultPartitionSuffix, ClassLoader.getSystemResource(
        "init.ldif").getPath(), LdapStructureDaoTest.port, true, ClassLoader.getSystemResource(
        "esco-structure-schema.ldif").getPath());

	/*@Before
	public void initLdap() throws Exception {
	    final DistinguishedName schemaDn = new DistinguishedName("ou=schema");
	    final DistinguishedName structuresDn = new DistinguishedName("ou=structures,dc=esco-centre,dc=fr");

	    LdapTestUtils.cleanAndSetup(this.ldapTemplate.getContextSource(), schemaDn, this.escoStructuresSchemaLdif);
	    //LdapTestUtils.loadLdif(this.ldapTemplate.getContextSource(), this.initLdif);
	    //LdapTestUtils.cleanAndSetup(this.ldapTemplate.getContextSource(), structuresDn, this.initLdif);
	}*/

    @Test
    public void testFindAllStructures() throws Exception {
        final Collection<? extends Structure> structs = this.dao.findAllStructures();

        Assertions.assertNotNull(structs, "Structs list shoud be empty not null !");

        Assertions.assertTrue(structs.size() > 0, "Structs list shoud not be empty !");

        for (Structure struct : structs) {
            log.debug("returned struct : {}", struct);
        }
    }

    @Test
    public void testFindOneStructures() throws Exception {
        final Collection<? extends Structure> structs = this.dao.findAllStructures();

        Assertions.assertNotNull(structs, "Structs list shoud be empty not null !");

        Assertions.assertTrue(structs.size() > 0, "Structs list shoud not be empty !");

        final Structure structComparison = structs.iterator().next();

        final Structure structToCompare = this.dao.findOneStructureById(structComparison.getId());

        Assertions.assertTrue(structToCompare.equals(structComparison), "Struct comparison should be equal");
    }
}
