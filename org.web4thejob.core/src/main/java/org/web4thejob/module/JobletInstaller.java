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

import java.util.List;
import java.util.Properties;

/**
 * @author Veniamin Isaias
 * @since 3.4.0
 */
public interface JobletInstaller {

    public void setConnectionInfo(Properties connInfo);

    public Properties getConnectionInfo();

    public boolean canConnect();

    public <E extends Exception> List<E> installAll();

    public <E extends Exception> List<E> install(List<Joblet> joblets);
}
