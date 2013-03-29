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

package org.web4thejob.orm;

import my.joblet.*;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.web4thejob.context.ContextUtil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/web4thejob/conf/orm-config.xml"})
public abstract class AbstractHibernateDependentTest {

    protected static final int iterations = 5;
    private static boolean initialized = false;

    @Before
    public void initializeData() {
        if (initialized) {
            return;
        }

        initialized = true;
        final DataWriterService dataWriterService = ContextUtil.getDWS();
        for (int i = 1; i <= iterations; i++) {
            Reference2 reference2 = new Reference2(UUID.randomUUID().toString());
            dataWriterService.save(reference2);

            Reference1 reference1 = new Reference1(reference2, UUID.randomUUID().toString());
            dataWriterService.save(reference1);

            final Master1 master1 = new Master1();
            master1.setName(master1.getEntityType().getName());
            master1.setReference1(reference1);
            dataWriterService.save(master1);

            final Master2 master2 = new Master2();
            master2.setKey(UUID.randomUUID().toString());
            master2.setName(master2.getEntityType().getName());
            dataWriterService.save(master2);

            final Detail detail = new Detail();
            detail.setId(new DetailId(master1, master2, UUID.randomUUID().toString(),
                    new Random(master1.getId()).nextLong()));
            detail.setFclass(detail.getEntityType());
            detail.setFdate(new Date());
            detail.setFdouble(java.lang.Math.PI);
            detail.setFint(new LocalDate().getMonthOfYear());
            detail.setFstring(new LocalDate().toString());
            detail.setFtimestamp(new Timestamp(System.currentTimeMillis()));
            dataWriterService.save(detail);
        }
    }

}
