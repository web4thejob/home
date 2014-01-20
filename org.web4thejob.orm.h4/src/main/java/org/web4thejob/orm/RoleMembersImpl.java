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

package org.web4thejob.orm;

import org.web4thejob.security.RoleIdentity;
import org.web4thejob.security.RoleMembers;
import org.web4thejob.security.UserIdentity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
/*package*/ class RoleMembersImpl extends AbstractHibernateEntity implements RoleMembers {
// ------------------------------ FIELDS ------------------------------

    private long id;
    @NotNull
    private RoleIdentity role;
    @NotNull
    private UserIdentity user;
    @SuppressWarnings("unused")
    private int version;
// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public RoleIdentity getRole() {
        return role;
    }

    @Override
    public void setRole(RoleIdentity role) {
        this.role = role;
    }

    @Override
    public UserIdentity getUser() {
        return user;
    }

    @Override
    public void setUser(UserIdentity user) {
        this.user = user;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Entity ---------------------

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (role != null) {
            sb.append(role.toString());
        } else {
            sb.append("<?>");
        }
        sb.append("\"");
        if (user != null) {
            sb.append(user.toString());
        } else {
            sb.append("<?>");
        }
        return sb.toString();
    }
}
