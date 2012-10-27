/*
 * Copyright (c) 2012 Veniamin Isaias.
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
 * <p>Defines the api of an instance capable of binding to a specific {@link Entity} instance.</p>
 * <p>The specifics of the binding mechanism and how this affects the view and behavior of the implementing instance
 * depends on the implementation.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public interface BindCapable extends TargetType {

    public void bind(Entity bindEntity);

    public boolean canBind(Entity entity);

    public boolean isBoundOn(Entity entity);

    public boolean hasTargetEntity();

    public Entity getTargetEntity();

    public boolean hasMasterEntity();

    public Entity getMasterEntity();

    public void setMasterEntity(Entity masterEntity);

    public void setTargetEntity(Entity targetEntity);

}
