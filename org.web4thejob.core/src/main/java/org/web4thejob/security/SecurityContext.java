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

/**
 * <p>Security context scoped at session level.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public interface SecurityContext {

    public void clearContext();

    public UserIdentity getUserIdentity();

    public boolean hasRole(String role);

    public boolean isAccessible(String securityId);

    public boolean isPasswordValid(String rawPassword);

    public boolean isAdministrator();

    public boolean renewPassword(String oldPassword, String newPassword);

    public String getAuthorizationMenu();

}
