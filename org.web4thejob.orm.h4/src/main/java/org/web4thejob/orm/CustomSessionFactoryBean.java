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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.module.Joblet;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.converter.JobletScanner;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class CustomSessionFactoryBean extends LocalSessionFactoryBean implements HibernateConfiguration {

    private static final Logger LOG = Logger.getLogger(CustomSessionFactoryBean.class);
    private static final String SCHEMA_FILE = "classpath:org/web4thejob/conf/Schema.sql";
    @Autowired
    @Qualifier(CoreUtil.BEAN_ROOT_CONTEXT)
    private ApplicationContext applicationContext;

// -------------------------- OTHER METHODS --------------------------

    private void createSchemata() {

        if (!applicationContext.getResource(SCHEMA_FILE).exists()) {
            return;
        }

        try {

            Configuration configuration = new Configuration();
            configuration.setProperties(getHibernateProperties());

            Connection connection = null;
            if (getDataSource() != null) {
                connection = getDataSource().getConnection();
            }
            SchemaExport myDatabaseExporter = new SchemaExport(configuration, connection);

            for (String schema : FileUtils.readLines(applicationContext.getResource(SCHEMA_FILE).getFile())) {
                if (StringUtils.hasText(schema)) {
                    myDatabaseExporter.execute(true, true, false, true);
                }
            }

            LOG.info("SCHEMA creation completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Rename or remove file " + SCHEMA_FILE + " so that you don't get previous error.");
        }
    }

    private void applyInterceptor() {
        setEntityInterceptor(new HibernateInterceptor());
    }

    @Override
    protected SessionFactory buildSessionFactory() throws Exception {
        createSchemata();
        applyInterceptor();
        return super.buildSessionFactory();
    }

    @Override
    protected Configuration newConfiguration() throws HibernateException {
        Configuration configuration = super.newConfiguration();
        applyResources(configuration);
        return configuration;
    }

    private void applyResources(Configuration configuration) {

        if (ContextUtil.isInitialized()) {
            List<Joblet> joblets = new ArrayList<Joblet>();
            joblets.add(ContextUtil.getSystemJoblet());
            joblets.addAll(ContextUtil.getJoblets());

            for (Joblet joblet : joblets) {
                for (Resource resource : joblet.getResources()) {
                    try {
                        configuration.addInputStream(resource.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
                LOG.info("Joblet " + joblet.getName() + " has been loaded automatically.");
            }
        } else {
            LOG.warn("Root context not initialized yet. Falling back to classpath scanning...");
            JobletScanner scanner = new JobletScanner();
            for (Object clazz : scanner.getComponentClasses("org.web4thejob")) {
                try {
                    Joblet joblet = (Joblet) ((Class) clazz).newInstance();
                    for (Resource resource : joblet.getResources()) {
                        try {
                            configuration.addInputStream(resource.getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                    LOG.info("Joblet " + joblet.getName() + " has been loaded automatically.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
