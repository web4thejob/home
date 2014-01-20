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

import org.web4thejob.orm.Entity;

/**
 * <p>Defines the api of an instance aware of the {@link Entity} type class concept as a target type.</p>
 * <p>Target type implies that implementors arrange their view so that instances of the target type can be
 * displayed.</p>
 *
 * @author Veniamin Isaias
 * @see MasterType
 * @since 1.0.0
 */
public interface TargetType {

    public Class<? extends Entity> getTargetType();

    public boolean hasTargetType();

}
