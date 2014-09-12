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

import org.hibernate.validator.constraints.NotBlank;
import org.web4thejob.orm.annotation.PropertyEditor;
import org.web4thejob.orm.annotation.PropertyViewer;
import org.web4thejob.security.AuthorizationPolicy;
import org.web4thejob.security.RoleIdentity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
/*package*/ class AuthorizationPolicyImpl extends AbstractHibernateEntity implements AuthorizationPolicy {
// ------------------------------ FIELDS ------------------------------

    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @PropertyViewer(className = "org.web4thejob.web.zbox.AuthorizationPolicyViewerBox")
    @PropertyEditor(className = "org.web4thejob.web.zbox.AuthorizationPolicyEditorBox")
    private String definition;
    private Set<RoleIdentity> roles = new HashSet<RoleIdentity>(0);
    @SuppressWarnings("unused")
    private int version;
// --------------------- GETTER / SETTER METHODS ---------------------

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RoleIdentity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleIdentity> roles) {
        this.roles = roles;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Entity ---------------------

    public Serializable getIdentifierValue() {
        return id;
    }

    public void setAsNew() {
        id = 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
