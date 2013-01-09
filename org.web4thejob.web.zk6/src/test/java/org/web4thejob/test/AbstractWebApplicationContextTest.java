/*
 * Copyright (c) 2012-2013 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.web4thejob.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextListener;
import org.web4thejob.context.SessionContext;
import org.web4thejob.util.CoreUtil;

import javax.servlet.ServletRequestEvent;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/web4thejob/conf/bootstrap.xml"}, loader = TestWebContextLoader.class)
public abstract class AbstractWebApplicationContextTest {

    private static boolean initialized = false;
    @Autowired
    @Qualifier(CoreUtil.BEAN_ROOT_CONTEXT)
    private ApplicationContext ctx;
    private static RequestContextListener contextListener;
    private static ServletRequestEvent requestEvent;

    @Before
    public void setUp() {
        if (!initialized) {
            initialized = true;
            contextListener = new RequestContextListener();
            requestEvent = new ServletRequestEvent(new MockServletContext(), new MockHttpServletRequest("GET",
                    "/index.zul"));
        }
        contextListener.requestInitialized(requestEvent);
        //Assert.assertTrue(StringUtils.hasText(WebZkVersion.getVersion()));
    }

    @After
    public void tearDown() {
        contextListener.requestDestroyed(requestEvent);
    }

    @Test
    public void test1() {
        final SessionContext session = ctx.getBean(SessionContext.class);
        session.refresh();
        Assert.assertNotNull(session);
    }
}
