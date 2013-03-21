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

/**
 * @author Veniamin Isaias
 * @since 3.4.0
 */
public interface DatasourceProperties {
    public static final String DIALECT = "datasource.dialect";
    public static final String DRIVER = "datasource.driverClassName";
    public static final String URL = "datasource.url";
    public static final String USER = "datasource.username";
    public static final String PASSWORD = "datasource.password";
    public static final String SCHEMA = "datasource.default_schema";
    public static final String CATALOG = "datasource.default_catalog";
    public static final String INITIAL_DDL = "datasource.initial_ddl";
    public static final String INSTALLED = "datasource.installed";

    public static final String PATH = "/org/web4thejob/conf/datasource.properties";
}
