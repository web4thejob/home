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

package org.web4thejob.security;

/**
 * <p>Internal entity type for defining user role instances according to a <a href="http://en.wikipedia
 * .org/wiki/Role-based_access_control">RBAC</a> design. </p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface RoleIdentity extends Identity {
// ------------------------------ FIELDS ------------------------------

    public static final String ROLE_ADMINISTRATOR = "ADMINISTRATOR";
    public static final String FLD_AUTHORITY = "code";
    public static final String FLD_AUTHORIZATION_POLICY = "authorizationPolicy";
    public static final String FLD_INDEX = "index";
    public static final String FLD_USERS = "users";

// -------------------------- OTHER METHODS --------------------------

    public AuthorizationPolicy getAuthorizationPolicy();

    public String getDescription();

    public long getId();

    public int getIndex();

    public void setAuthorizationPolicy(AuthorizationPolicy authorizationPolicy);

    public void setDescription(String description);

    public void setIndex(int index);
}
