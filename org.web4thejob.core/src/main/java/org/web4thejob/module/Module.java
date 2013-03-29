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

/**
 * <p>Defines the api of valid web4thejob modules.</p>
 *
 * @author Veniamin Isaias
 * @since 2.0.0
 */
public interface Module extends Comparable<Module> {
    public String getName();

    public String getVersion();

    public String getFileName();

    public String getOrganizationName();

    public String getOrganizationUrl();

    public String getLicenseName();

    public String getLicenseUrl();

    public String getProjectUrl();

    public int getOrdinal();

    public ModuleType getType();
}
