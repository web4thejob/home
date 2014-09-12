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
import org.web4thejob.orm.annotation.Encrypted;
import org.web4thejob.orm.annotation.PropertyEditor;
import org.web4thejob.orm.annotation.PropertyViewer;
import org.web4thejob.orm.annotation.StatusHolder;
import org.web4thejob.security.RoleMembers;
import org.web4thejob.security.UserIdentity;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
/*package*/ class UserIdentityImpl extends AbstractIdentityImpl implements UserIdentity {
// ------------------------------ FIELDS ------------------------------

    @NotBlank
    @Encrypted
    @PropertyViewer(className = "org.web4thejob.web.zbox.PasswordViewer")
    @PropertyEditor(className = "org.web4thejob.web.zbox.PasswordEditor")
    private String password;
    @NotBlank
    private String lastName;
    @NotBlank
    private String firstName;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    @StatusHolder(InactiveWhen = false)
    private boolean enabled = true;
    private Set<RoleMembers> roles = new HashSet<RoleMembers>(0);
    private Locale locale;
// --------------------- GETTER / SETTER METHODS ---------------------

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleMembers> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleMembers> roles) {
        this.roles = roles;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Entity ---------------------

    @Override
    public String toString() {
        return getCode();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
