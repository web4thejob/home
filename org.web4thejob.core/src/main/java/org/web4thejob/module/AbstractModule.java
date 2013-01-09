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

import java.util.Properties;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */
public abstract class AbstractModule implements Module {

    private Properties properties;

    protected AbstractModule() {
        properties = new Properties();
        try {
            properties.load(new ClassPathResource(getClass().getSimpleName() + ".properties",
                    getClass()).getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersion() {
        return properties.getProperty("application.version");
    }

    @Override
    public String getPackageName() {
        return properties.getProperty("application.packageName");
    }

    @Override
    public String getOrganizationName() {
        return properties.getProperty("application.organization.name");
    }

    @Override
    public String getOrganizationUrl() {
        return properties.getProperty("application.organization.url");
    }

    @Override
    public String getLicenseName() {
        return properties.getProperty("application.license.name");
    }

    @Override
    public String getLicenseUrl() {
        return properties.getProperty("application.license.url");
    }

    @Override
    public String getName() {
        return properties.getProperty("application.name");
    }

    @Override
    public String getProjectUrl() {
        return properties.getProperty("application.url");
    }

    @Override
    public ModuleType getType() {
        return ModuleType.valueOf(properties.getProperty("application.type").trim().toUpperCase());
    }

    @Override
    public String toString() {
        return getPackageName();
    }

    @Override
    public int compareTo(Module o) {
        return Integer.valueOf(getOrdinal()).compareTo(o.getOrdinal());
    }

}
