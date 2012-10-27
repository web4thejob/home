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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.mapping.Detail;
import org.web4thejob.orm.mapping.Master1;
import org.web4thejob.orm.query.Query;
import org.web4thejob.security.SecurityService;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class TypeSerailizationTest extends AbstractHibernateDependentTest {
    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void marshallingQueryTest() throws XmlMappingException, IOException {
        final Marshaller marshaller = ContextUtil.getBean(Marshaller.class);
        Assert.assertNotNull(marshaller);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        final Query query1 = entityFactory.buildQuery(Master1.class);
        query1.setName("123");
        query1.setOwner(ContextUtil.getBean(SecurityService.class).getAdministratorIdentity());
        ContextUtil.getDWS().save(query1);
        marshaller.marshal(query1, result);

        final Unmarshaller unmarshaller = ContextUtil.getBean(Unmarshaller.class);
        final Query query2 = (Query) unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(out.toByteArray
                ())));

        Assert.assertEquals(query1, query2);
    }

    @Test
    public void marshallingTest() throws XmlMappingException, IOException {
        final Marshaller marshaller = ContextUtil.getBean(Marshaller.class);
        Assert.assertNotNull(marshaller);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        final Detail detail1 = ContextUtil.getDRS().getOne(Detail.class);
        marshaller.marshal(detail1, result);

        final Unmarshaller unmarshaller = ContextUtil.getBean(Unmarshaller.class);
        final Detail detail2 = (Detail) unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(out
                .toByteArray())));

        Assert.assertEquals(detail1, detail2);
    }

    @Test
    public void simpleMarshallingTest() throws XmlMappingException, IOException {
        final Marshaller marshaller = ContextUtil.getBean(Marshaller.class);
        Assert.assertNotNull(marshaller);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        final Master1 master1 = ContextUtil.getDRS().getOne(Master1.class);
        marshaller.marshal(master1, result);

        final Unmarshaller unmarshaller = ContextUtil.getBean(Unmarshaller.class);
        final Master1 master2 = (Master1) unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(out
                .toByteArray())));

        Assert.assertEquals(master1, master2);
    }

}
