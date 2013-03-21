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

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.Target;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.web4thejob.orm.DatasourceProperties;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Veniamin Isaias
 * @since 3.4.0
 */
public abstract class AbstractJoblet extends AbstractModule implements Joblet {

    @Override
    public ModuleType getType() {
        return ModuleType.JOBLET;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Exception> List<E> install(Properties connectionInfo) {

        List<E> exceptions = new ArrayList<E>();

        try {

            final Configuration configuration = new Configuration();
            configuration.setProperty(AvailableSettings.DIALECT, connectionInfo.getProperty(DatasourceProperties
                    .DIALECT));
            configuration.setProperty(AvailableSettings.DRIVER, connectionInfo.getProperty(DatasourceProperties
                    .DRIVER));
            configuration.setProperty(AvailableSettings.URL, connectionInfo.getProperty(DatasourceProperties.URL));
            configuration.setProperty(AvailableSettings.USER, connectionInfo.getProperty(DatasourceProperties.USER));
            configuration.setProperty(AvailableSettings.PASS, connectionInfo.getProperty(DatasourceProperties
                    .PASSWORD));

            final ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .buildServiceRegistry();

            if (StringUtils.hasText(connectionInfo.getProperty(DatasourceProperties.INITIAL_DDL))) {
                Connection connection = serviceRegistry.getService(ConnectionProvider.class).getConnection();
                Statement statement = connection.createStatement();
                statement.executeUpdate(connectionInfo.getProperty(DatasourceProperties.INITIAL_DDL));
                statement.close();
            }

            for (Resource resource : getResources()) {
                configuration.addInputStream(resource.getInputStream());
            }

            SchemaExport schemaExport = new SchemaExport(serviceRegistry, configuration);
            schemaExport.execute(Target.EXPORT, SchemaExport.Type.CREATE);
            exceptions.addAll(schemaExport.getExceptions());

        } catch (Exception e) {
            exceptions.add((E) e);
        }

        return exceptions;
    }

    protected abstract List<Resource> getResources();


}
