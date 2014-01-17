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

package org.web4thejob.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class GrantedAuthorityImpl implements GrantedAuthority {
    // ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 1L;
    private final RoleIdentity roleIdentity;

    // --------------------------- CONSTRUCTORS ---------------------------

    public GrantedAuthorityImpl(RoleIdentity roleIdentity) {
        if (roleIdentity == null || roleIdentity.isNewInstance()) {
            throw new IllegalArgumentException();
        }
        this.roleIdentity = roleIdentity;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface GrantedAuthority ---------------------

    @Override
    public String getAuthority() {
        return "ROLE_" + roleIdentity.getCode();
    }

    @Override
    public String toString() {
        return getAuthority();
    }
}
