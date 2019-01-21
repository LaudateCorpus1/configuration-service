/**
 * Copyright (C) 2019. TomTom NV (http://www.tomtom.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomtom.services.configuration.implementation;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings({"JUnitTestMethodWithNoAssertions", "OverlyBroadThrowsClause", "ProhibitedExceptionDeclared"})
public class ApiResponseFormatTest {
    private static final Logger LOG = LoggerFactory.getLogger(ApiResponseFormatTest.class);

    private LocalTestServer server = null;

    public void startServer(@Nonnull final String config) throws IncorrectConfigurationException {
        server = new LocalTestServer("classpath:" + config);
        server.startServer();
    }

    @After
    public void stopServer() {
        server.stopServer();
    }

    @Test
    public void checkEmptyTreeJson() throws Exception {
        LOG.info("checkEmptyTreeJson");
        startServer("empty.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{}", response.readEntity(String.class));
    }

    @Test
    public void checkEmptyTreeXml() throws Exception {
        LOG.info("checkSimpleTreeXml");
        startServer("empty.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree").
                request().
                accept(APPLICATION_XML_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><node/>",
                response.readEntity(String.class));

    }

    @Test
    public void checkSimpleTreeJson() throws Exception {
        LOG.info("checkSimpleTreeJson");
        startServer("simple1.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"nodes\":[{\"match\":\"child-1\",\"parameters\":[{\"key\":\"key-1a\",\"value\":\"value-1a\"},{\"key\":\"key-1b\",\"value\":\"value-1b\"}],\"modified\":\"2016-01-02T11:11:11Z\"},{\"match\":\"child-2\",\"parameters\":[{\"key\":\"key-2\",\"value\":\"value-2\"}]}],\"parameters\":[{\"key\":\"key-0\",\"value\":\"value-0\"}],\"modified\":\"2016-01-02T00:00:00Z\",\"levels\":[\"criterium\"]}",
                response.readEntity(String.class));

    }

    @Test
    public void checkSimpleTreeXml() throws Exception {
        LOG.info("checkSimpleTreeXml");
        startServer("simple1.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree").
                request().
                accept(APPLICATION_XML_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><node><nodes><node><match>child-1</match><parameters><parameter><key>key-1a</key><value>value-1a</value></parameter><parameter><key>key-1b</key><value>value-1b</value></parameter></parameters><modified>2016-01-02T11:11:11Z</modified></node><node><match>child-2</match><parameters><parameter><key>key-2</key><value>value-2</value></parameter></parameters></node></nodes><parameters><parameter><key>key-0</key><value>value-0</value></parameter></parameters><modified>2016-01-02T00:00:00Z</modified><levels><level>criterium</level></levels></node>",
                response.readEntity(String.class));

    }

    @Test
    public void checkSearchInSImpleTreeJson() throws Exception {
        LOG.info("checkSearchInSImpleTreeJson");
        startServer("simple1.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?criterium=child-1").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"parameters\":[{\"key\":\"key-1a\",\"value\":\"value-1a\"},{\"key\":\"key-1b\",\"value\":\"value-1b\"}],\"searched\":\"criterium=child-1\",\"matched\":\"criterium=child-1\"}",
                response.readEntity(String.class));
    }

    @Test
    public void checkSearchInSimpleTreeXml() throws Exception {
        LOG.info("checkSearchInSimpleTreeXml");
        startServer("simple1.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?criterium=child-1").
                request().
                accept(APPLICATION_XML_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><searchResult><parameters><parameter><key>key-1a</key><value>value-1a</value></parameter><parameter><key>key-1b</key><value>value-1b</value></parameter></parameters><searched>criterium=child-1</searched><matched>criterium=child-1</matched></searchResult>",
                response.readEntity(String.class));

    }

    @Test
    public void checkMultiSearchWrongSeparatorJson() throws Exception {
        LOG.info("checkMultiSearchWrongSeparatorJson");
        startServer("simple1.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?criterium=child-1;unknown").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void checkMultiSearchJson() throws Exception {
        LOG.info("checkMultiSearchJson");
        startServer("simple1.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?criterium=child-1,unknown").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[{\"parameters\":[{\"key\":\"key-1a\",\"value\":\"value-1a\"},{\"key\":\"key-1b\",\"value\":\"value-1b\"}],\"searched\":\"criterium=child-1\",\"matched\":\"criterium=child-1\"},{\"parameters\":[{\"key\":\"key-0\",\"value\":\"value-0\"}],\"searched\":\"criterium=unknown\",\"matched\":\"\"}]",
                response.readEntity(String.class));
    }

    @Test
    public void checkMultiSearchWrongSeparatorXml() throws Exception {
        LOG.info("checkMultiSearchWrongSeparatorXml");
        startServer("simple1.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?criterium=child-1;unknown").
                request().
                accept(APPLICATION_XML_TYPE).get();
        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void checkMultiSearchXml() throws Exception {
        LOG.info("checkMultiSearchXml");
        startServer("simple1.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?criterium=child-1,unknown").
                request().
                accept(APPLICATION_XML_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><searchResults><searchResult><parameters><parameter><key>key-1a</key><value>value-1a</value></parameter><parameter><key>key-1b</key><value>value-1b</value></parameter></parameters><searched>criterium=child-1</searched><matched>criterium=child-1</matched></searchResult><searchResult><parameters><parameter><key>key-0</key><value>value-0</value></parameter></parameters><searched>criterium=unknown</searched><matched></matched></searchResult></searchResults>",
                response.readEntity(String.class));
    }

    @Test
    public void checkRegexSearchWithEmptyTerm1() throws Exception {
        LOG.info("checkRegexSearchWithEmptyTerm1");
        startServer("regex.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?criterium=").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"parameters\":[{\"key\":\"value\",\"value\":\"4\"}],\"searched\":\"criterium=\",\"matched\":\"criterium=.*\"}",
                response.readEntity(String.class));
    }

    @Test
    public void checkRegexSearchWithEmptyTerms1() throws Exception {
        LOG.info("checkRegexSearchWithEmptyTerms1");
        startServer("regex.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?criterium=,").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("[{\"parameters\":[{\"key\":\"value\",\"value\":\"4\"}],\"searched\":\"criterium=\",\"matched\":\"criterium=.*\"},{\"parameters\":[{\"key\":\"value\",\"value\":\"4\"}],\"searched\":\"criterium=\",\"matched\":\"criterium=.*\"}]",
                response.readEntity(String.class));
    }

    @Test
    public void checkRegexSearchWithEmptyTerm2() throws Exception {
        LOG.info("checkRegexSearchWithEmptyTerm2");
        startServer("regex-config.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?device=&country=&connection=&version=").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"parameters\":[{\"key\":\"radius_km\",\"value\":\"general fallback\"},{\"key\":\"interval_secs\",\"value\":\"general fallback\"}],\"searched\":\"device=&country=&connection=&version=\",\"matched\":\"device=.*&country=.*&connection=.*&version=.*\"}",
                response.readEntity(String.class));
    }

    @Test
    public void checkRegexSearchWithEmptyTerm3() throws Exception {
        LOG.info("checkRegexSearchWithEmptyTerm3");
        startServer("regex-config.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?device=x&country=y").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"parameters\":[{\"key\":\"radius_km\",\"value\":\"general fallback\"},{\"key\":\"interval_secs\",\"value\":\"general fallback\"}],\"searched\":\"device=x&country=y&connection=&version=\",\"matched\":\"device=.*&country=.*&connection=.*&version=.*\"}",
                response.readEntity(String.class));
    }

    @Test
    public void checkRegexSearchWithEmptyTerm4() throws Exception {
        LOG.info("checkRegexSearchWithEmptyTerm4");
        startServer("regex-config.json");
        final Response response = new ResteasyClientBuilder().build().
                target(server.getHost() + "/tree?device=x&country=y&connection=&version=z").
                request().
                accept(APPLICATION_JSON_TYPE).get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("{\"parameters\":[{\"key\":\"radius_km\",\"value\":\"general fallback\"},{\"key\":\"interval_secs\",\"value\":\"general fallback\"}],\"searched\":\"device=x&country=y&connection=&version=z\",\"matched\":\"device=.*&country=.*&connection=.*&version=.*\"}",
                response.readEntity(String.class));
    }
}
