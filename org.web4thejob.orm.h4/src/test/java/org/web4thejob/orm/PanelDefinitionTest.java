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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.security.SecurityService;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class PanelDefinitionTest extends AbstractHibernateDependentTest {

    @Autowired
    private EntityFactory entityFactory;

    @Autowired
    private DataWriterService dataWriterService;

    @Test
    public void persistenceTest() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= 1000000; i++) {
            sb.append("a");
        }

        PanelDefinition definition = entityFactory.buildPanelDefinition();
        definition.setBeanId("123");
        definition.setDefinition("1");
        definition.setDescription(sb.toString());
        definition.setName("fdsbfhsd");
        definition.setType("123");
        definition.setOwner(ContextUtil.getBean(SecurityService.class).getAdministratorIdentity());
        dataWriterService.save(definition);
        Assert.assertTrue(definition.getId() > 0);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void uniqueNameCheck() {
        persistenceTest();
        persistenceTest();
    }
}
