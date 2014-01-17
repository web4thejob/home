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

import org.web4thejob.command.CommandAware;
import org.web4thejob.orm.Entity;
import org.web4thejob.setting.SettingAware;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * <p>Defines the api of a panel capable of viewing, editing and inserting {@link Entity} instances as defined by the
 * <code>mutableMode</code> property.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public interface MutablePanel extends Panel, CommandAware, SettingAware, BindCapable, I18nAware {
// -------------------------- OTHER METHODS --------------------------

    public void addDirtyListener(DirtyListener dirtyListener);

    public MutableMode getMutableMode();

    public boolean isDirty();

    public void beforePersist();

    public void persist() throws Exception;

    void setDirty(boolean dirty);

    public Set<ConstraintViolation<Entity>> validate();
}
