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

package org.web4thejob.orm.util;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.web4thejob.orm.PanelDefinition;

import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/web4thejob/conf/orm-config.xml"})
public class StandAloneTest {
// ------------------------------ FIELDS ------------------------------

    @Autowired
    private SessionFactory sessionFactory;

// -------------------------- OTHER METHODS --------------------------

    @Test
    @Transactional
    @SuppressWarnings({"unchecked"})
    public void dummy() {
        System.out.println(sessionFactory.toString());

        Criteria c = DetachedCriteria.forClass(PanelDefinition.class).getExecutableCriteria(sessionFactory
                .getCurrentSession());
        List<PanelDefinition> panels = c.list();

        System.out.println(panels.size());
    }
}
