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
package fr.recia.paramuseretab.dao.impl;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.schema.Schema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LdapServerRule implements BeforeAllCallback, AfterAllCallback {
    private static final Log LOG = LogFactory.getLog(LdapServerRule.class);

    public static final String DefaultDn = "cn=Directory Manager";
    public static final String DefaultPassword = "password";
    private String baseDn;
    private String dn;
    private String password;
    private String lDiffPath;
    private String schemaFilePath;
    private boolean validateSchema;
    private InMemoryDirectoryServer server;
    private int listenPort;

    public LdapServerRule(String baseDn, String lDiffPath, int listenPort, boolean validateSchema,
                          String schemaFilePath) {
        this.lDiffPath = lDiffPath;
        this.baseDn = baseDn;
        this.dn = DefaultDn;
        this.password = DefaultPassword;
        this.listenPort = listenPort;
        this.schemaFilePath = schemaFilePath;
        this.validateSchema = validateSchema;

    }

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        start();
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        stop();
    }

    public int getRunningPort() {
        return getServer().getListenPort();
    }

    private void start() throws Exception {
        InMemoryDirectoryServerConfig config;

        LOG.info("LDAP server " + toString() + " starting...");
        config = new InMemoryDirectoryServerConfig(getBaseDn());
        config.addAdditionalBindCredentials(getDn(), getPassword());
        if (!validateSchema) {
            config.setSchema(null);
        } else if (schemaFilePath != null) {
            config.setSchema(Schema.mergeSchemas(Schema.getDefaultStandardSchema(),
                Schema.getSchema(schemaFilePath)));
        } else {
            config.setSchema(Schema.getDefaultStandardSchema());
        }
        config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("LDAP", getListenPort()));
        setServer(new InMemoryDirectoryServer(config));
        getServer().importFromLDIF(true, getLDiffPath());
        getServer().startListening();
        LOG.info("LDAP server " + toString() + " started. Listen on port " + getServer().getListenPort());
    }

    private void stop() {
        server.shutDown(true);
        LOG.info("LDAP server " + toString() + " stopped");
    }

    public String getBaseDn() {
        return baseDn;
    }

    public String getDn() {
        return dn;
    }

    public String getPassword() {
        return password;
    }

    public InMemoryDirectoryServer getServer() {
        return server;
    }

    public void setServer(InMemoryDirectoryServer server) {
        this.server = server;
    }

    public String getLDiffPath() {
        return lDiffPath;
    }

    public int getListenPort() {
        return listenPort;
    }

    /*
     * @Override
     * public String toString() {
     * return com.google.common.base.Objects.toStringHelper(this).add("baseDn",
     * baseDn).add("listenPort", listenPort)
     * .toString();
     * }
     */
}
