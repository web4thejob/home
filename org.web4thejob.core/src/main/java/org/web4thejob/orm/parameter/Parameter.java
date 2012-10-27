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

package org.web4thejob.orm.parameter;

import org.web4thejob.orm.Entity;
import org.web4thejob.security.Identity;

/**
 * <p>Internal entity type for managing parameters that control the behavior of the
 * application in various points.</p>
 * <p>Parameters have an owner which is of type {@link Identity}. This means that the owner could be either a {@link
 * org.web4thejob.security.UserIdentity UserIdentity} or a {@link org.web4thejob.security.RoleIdentity RoleIdentity}.
 * When the
 * system looks up a parameter className, first it looks for the parameter owned by the current user. If it is not
 * found
 * the system enumerates all user's roles according to their
 * {@link org.web4thejob.security.RoleIdentity#getIndex() RoleIdentity.getIndex()} attribute and uses the className of
 * the
 * parameter instance that is encountered first. If no instances are found either {@link
 * org.web4thejob.util.CoreUtil#getParameterValue(
 *org.web4thejob.security.Identity, org.web4thejob.orm.parameter.Category, java.lang.String,
 * java.lang.Class) null}, or a {@link org.web4thejob.util.CoreUtil#getParameterValue(org.web4thejob.security.Identity,
 * org.web4thejob.orm.parameter.Category, java.lang.String, java.lang.Class) default className} is used
 * depending on the invoked utility method.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.5
 */
public interface Parameter extends Entity {
    public static final String FLD_OWNER = "owner";
    public static final String FLD_CATEGORY = "category";
    public static final String FLD_KEY = "key";

    public void setOwner(Identity owner);

    public void setCategory(Category category);

    public void setKey(String key);

    public void setValue(String value);

    public Identity getOwner();

    public Category getCategory();

    public String getKey();

    public String getValue();
}
