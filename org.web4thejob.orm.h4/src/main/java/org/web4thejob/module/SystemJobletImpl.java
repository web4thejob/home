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

package org.web4thejob.module;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web4thejob.orm.DatasourceProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Veniamin Isaias
 * @since 3.4.0
 */

@Component
public class SystemJobletImpl extends AbstractJoblet implements SystemJoblet {


    @Override
    protected String getFileName() {
        return H4Module.class.getSimpleName() + ".properties";
    }

    @Override
    public String getName() {
        return "System Joblet";
    }

    @Override
    public String getProjectUrl() {
        return "http://wiki.web4thejob.org/miscel/glossary/system_joblet";
    }

    @Override
    public int getOrdinal() {
        return 5;
    }

    @Override
    protected List<Resource> getResources() {
        List<Resource> resources = new ArrayList<Resource>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            for (Resource resource : resolver.getResources("classpath*:org/web4thejob/orm/**/*.hbm.xml")) {

                if (resource.getFilename().equals("AuxiliaryDatabaseObjects.hbm.xml"))
                    continue;

                resources.add(resource);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return resources;
    }

    @Override
    public boolean isInstalled() {

        Properties datasource = new Properties();
        try {
            datasource.load(new ClassPathResource(DatasourceProperties.PATH).getInputStream());
        } catch (IOException e) {
            return false;
        }

        return StringUtils.hasText(datasource.getProperty(DatasourceProperties.INSTALLED));
    }
}
