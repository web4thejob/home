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

package org.web4thejob.web.panel;

/**
 * <p>Interface for defining the api of an instance aware of a Master\Detail relationship between two entity types.</p>
 * <p>The relationship is defined by the <code>bindProperty</code> property. This property should be a member of the
 * <i>target type</i> and have a type of the <i>master type</i>.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface MasterDetailTypeAware extends TargetTypeAware, MasterTypeAware {

    public String getBindProperty();

    public void setBindProperty(String propertyName);

    public boolean hasBindProperty();

    public boolean isMasterDetail();

}
