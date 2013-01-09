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

import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextLoader;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class TestWebContextLoader implements ContextLoader {

    @Override
    public ApplicationContext loadContext(String... locations) throws Exception {

        final MockServletContext ctx = new MockServletContext("");
        ctx.addInitParameter("contextConfigLocation", locations[0]);
        final org.springframework.web.context.ContextLoader loader = new org.springframework.web.context
                .ContextLoader();
        return loader.initWebApplicationContext(ctx);
    }

    @Override
    public String[] processLocations(Class<?> clazz, String... locations) {
        return locations;
    }

}
