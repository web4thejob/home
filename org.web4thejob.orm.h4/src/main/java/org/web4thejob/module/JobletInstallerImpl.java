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

package org.web4thejob.module;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.DatasourceProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Veniamin Isaias
 * @since 3.4.0
 */

@Component
class JobletInstallerImpl implements JobletInstaller {
    private Properties connInfo;

    public Properties getConnectionInfo() {
        return connInfo;
    }

    public void setConnectionInfo(Properties connInfo) {
        this.connInfo = connInfo;
    }

    public boolean canConnect() {
        try {
            Class.forName(connInfo.getProperty(DatasourceProperties.DRIVER));
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Connection conn;
            conn = DriverManager.getConnection(connInfo.getProperty(DatasourceProperties.URL),
                    connInfo.getProperty(DatasourceProperties.USER), connInfo.getProperty(DatasourceProperties
                            .PASSWORD));
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public <E extends Exception> List<E> installAll() {
        List<Joblet> joblets = new ArrayList<Joblet>();
        joblets.add(ContextUtil.getSystemJoblet());
        joblets.addAll(ContextUtil.getJoblets());
        return install(joblets);
    }

    @SuppressWarnings("unchecked")
    public <E extends Exception> List<E> install(List<Joblet> joblets) {
        List<E> exceptions = new ArrayList<E>();

        try {

            final Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.dialect", connInfo.getProperty(DatasourceProperties
                    .DIALECT));
            configuration.setProperty("hibernate.connection.driver_class", connInfo.getProperty(DatasourceProperties
                    .DRIVER));
            configuration.setProperty("hibernate.connection.url", connInfo.getProperty(DatasourceProperties.URL));
            configuration.setProperty("hibernate.connection.username", connInfo.getProperty(DatasourceProperties.USER));
            configuration.setProperty("hibernate.connection.password", connInfo.getProperty(DatasourceProperties
                    .PASSWORD));


            if (StringUtils.hasText(connInfo.getProperty(DatasourceProperties.SCHEMA_SYNTAX))) {
                String schemaSyntax = connInfo.getProperty(DatasourceProperties.SCHEMA_SYNTAX);
                Connection connection = configuration.buildSettings().getConnectionProvider().getConnection();

                for (Joblet joblet : joblets) {
                    for (String schema : joblet.getSchemas()) {
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(schemaSyntax.replace("%s", schema));
                        statement.close();
                    }
                }

                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            }

            for (Joblet joblet : joblets) {
                for (Resource resource : joblet.getResources()) {
                    configuration.addInputStream(resource.getInputStream());
                }
            }

            SchemaExport schemaExport = new SchemaExport(configuration);
            schemaExport.execute(true, true, false, true);
            exceptions.addAll(schemaExport.getExceptions());

        } catch (Exception e) {
            exceptions.add((E) e);
        }

        return exceptions;

    }


}
