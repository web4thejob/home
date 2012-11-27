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

package org.web4thejob.orm;

import org.web4thejob.security.AuthorizationPolicy;
import org.web4thejob.security.RoleIdentity;
import org.web4thejob.security.RoleMembers;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
/*package*/ class RoleIdentityImpl extends AbstractIdentityImpl implements RoleIdentity {
// ------------------------------ FIELDS ------------------------------

    private String description;
    private int index = Integer.MAX_VALUE;
    private AuthorizationPolicy authorizationPolicy;
    private Set<RoleMembers> users = new HashSet<RoleMembers>(0);
    @SuppressWarnings("unused")
    private int version;

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public AuthorizationPolicy getAuthorizationPolicy() {
        return authorizationPolicy;
    }

    @Override
    public void setAuthorizationPolicy(AuthorizationPolicy authorizationPolicy) {
        this.authorizationPolicy = authorizationPolicy;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    public Set<RoleMembers> getUsers() {
        return users;
    }

    public void setUsers(Set<RoleMembers> users) {
        this.users = users;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Entity ---------------------

    @Override
    public String toString() {
        return getCode();
    }
}