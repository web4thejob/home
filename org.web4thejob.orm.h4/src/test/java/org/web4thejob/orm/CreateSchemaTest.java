package org.web4thejob.orm;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.Target;
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
        configuration.setProperty(AvailableSettings.DIALECT, datasource.getProperty(DatasourceProperties.DIALECT));
        configuration.setProperty(AvailableSettings.DRIVER, datasource.getProperty(DatasourceProperties.DRIVER));
        configuration.setProperty(AvailableSettings.URL, "jdbc:hsqldb:mem:mydb");
        configuration.setProperty(AvailableSettings.USER, datasource.getProperty(DatasourceProperties.USER));
        configuration.setProperty(AvailableSettings.PASS, datasource.getProperty(DatasourceProperties.PASSWORD));

        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();


        Connection connection = serviceRegistry.getService(ConnectionProvider.class).getConnection();
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

        SchemaExport schemaExport = new SchemaExport(serviceRegistry, configuration);
        schemaExport.execute(Target.EXPORT, SchemaExport.Type.CREATE);

        if (!schemaExport.getExceptions().isEmpty()) {
            throw new RuntimeException((Throwable) schemaExport.getExceptions().get(0));
        }

    }

}
