/*
 * Copyright (c) 2012 Veniamin Isaias.
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

package org.web4thejob.orm;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.oxm.Marshaller;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.mapping.Reference1;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class ListSerializationTest extends AbstractHibernateDependentTest {

    @Test
    public void datesListTest() throws IOException {
        final Marshaller marshaller = ContextUtil.getBean(Marshaller.class);
        Assert.assertNotNull(marshaller);

        List<Date> dates = new ArrayList<Date>();
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        marshaller.marshal(dates, result);
        System.out.println(out.toString("UTF-8"));

    }

    @Test
    public void localesListTest() throws IOException {
        final Marshaller marshaller = ContextUtil.getBean(Marshaller.class);
        Assert.assertNotNull(marshaller);

        List<Locale> locales = new ArrayList<Locale>();
        locales.add(Locale.CANADA);
        locales.add(Locale.CHINA);
        locales.add(Locale.ENGLISH);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        marshaller.marshal(locales, result);
        System.out.println(out.toString("UTF-8"));

    }

    @Test
    public void entitiesListTest() throws IOException {
        final Marshaller marshaller = ContextUtil.getBean(Marshaller.class);
        Assert.assertNotNull(marshaller);

        List<Entity> entities = new ArrayList<Entity>();
        for (Entity entity : ContextUtil.getDRS().getAll(Reference1.class)) {
            entities.add(entity);
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        marshaller.marshal(entities, result);
        System.out.println(out.toString("UTF-8"));

    }

}
