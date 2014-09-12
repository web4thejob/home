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

import org.web4thejob.context.ContextUtil;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class FullAccessSecurityContext implements SecurityContext {


    public void clearContext() {
        //do nothing
    }

    public UserIdentity getUserIdentity() {
        return ContextUtil.getBean(SecurityService.class).getAdministratorIdentity();
    }

    public boolean hasRole(String role) {
        return true;
    }

    public boolean isAccessible(String securityId) {
        return true;
    }

    public boolean isPasswordValid(String rawPassword) {
        return true;
    }

    public boolean isAdministrator() {
        return true;
    }

    public boolean renewPassword(String oldPassword, String newPassword) {
        return true;
    }

    public String getAuthorizationMenu() {
        return null;
    }
}
