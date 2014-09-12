/*
 * Copyright (c) 2012-2014 Veniamin Isaias.
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

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.module.JobletInstaller;
import org.web4thejob.orm.DatasourceProperties;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/web4thejob/conf/bootstrap.xml"}, loader = TestWebContextLoader.class)
@WebAppConfiguration
public abstract class AbstractWebApplicationContextTest {

    private static boolean initialized = false;

    @Before
    public void initializeData() {
        if (initialized) {
            return;
        }

        Properties datasource = new Properties();
        try {
            datasource.load(new ClassPathResource(DatasourceProperties.PATH).getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        JobletInstaller jobletInstaller;
        jobletInstaller = ContextUtil.getBean(JobletInstaller.class);
        jobletInstaller.setConnectionInfo(datasource);
        List<Exception> errors = jobletInstaller.installAll();
        if (errors.isEmpty()) {
            ContextUtil.addActiveProfile("installed");
            ContextUtil.refresh();
        } else {
            throw new RuntimeException("Test Context initialization failed.");
        }

        initialized = true;
    }
}
