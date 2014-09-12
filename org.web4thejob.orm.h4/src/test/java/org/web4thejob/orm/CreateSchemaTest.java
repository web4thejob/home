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

package org.web4thejob.orm;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.MyManagedProviderConnectionHelper;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Log4jConfigurer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Veniamin Isaias
 * @since 3.4.0
 */
public class CreateSchemaTest {

    @Test
    public void schemaExportTest() throws IOException, SQLException {

        Log4jConfigurer.initLogging("classpath:org/web4thejob/conf/log4j.xml");

        Properties datasource = new Properties();
        datasource.load(new ClassPathResource(DatasourceProperties.PATH).getInputStream());

        final Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.dialect", datasource.getProperty(DatasourceProperties.DIALECT));
        configuration.setProperty("hibernate.connection.driver_class", datasource.getProperty(DatasourceProperties.DRIVER));
        configuration.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:mydb");
        configuration.setProperty("hibernate.connection.username", datasource.getProperty(DatasourceProperties.USER));
        configuration.setProperty("hibernate.connection.password", datasource.getProperty(DatasourceProperties.PASSWORD));
        configuration.setProperty("hibernate.show_sql", "true");

        MyManagedProviderConnectionHelper connectionHelper = new MyManagedProviderConnectionHelper(
                configuration.getProperties());
        connectionHelper.prepare(true);


        Connection connection = connectionHelper.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE SCHEMA w4tj;");
        statement.close();


        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            for (Resource resource : resolver.getResources("classpath*:org/web4thejob/orm/**/*.hbm.xml")) {

                if (resource.getFile().getName().equals("AuxiliaryDatabaseObjects.hbm.xml"))
                    continue;

                configuration.addFile(resource.getFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        SchemaExport schemaExport = new SchemaExport(configuration);
        schemaExport.execute(true, true, false, true);

        if (!schemaExport.getExceptions().isEmpty()) {
            throw new RuntimeException((Throwable) schemaExport.getExceptions().get(0));
        }

    }

}
