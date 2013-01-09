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

import java.util.Locale;
import java.util.Set;

/**
 * <p>Internal entity type for defining user instances.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface UserIdentity extends Identity {
// ------------------------------ FIELDS ------------------------------

    public static final String USER_ADMIN = "admin";

    public static final String FLD_ID = "id";
    public static final String FLD_USERNAME = "code";

// -------------------------- OTHER METHODS --------------------------

    public long getId();

    public String getPassword();

    public boolean isAccountNonExpired();

    public boolean isAccountNonLocked();

    public boolean isCredentialsNonExpired();

    public boolean isEnabled();

    public void setAccountNonExpired(boolean accountNonExpired);

    public void setAccountNonLocked(boolean accountNonLocked);

    public void setCredentialsNonExpired(boolean credentialsNonExpired);

    public void setEnabled(boolean enabled);

    public void setPassword(String password);

    public Set<RoleMembers> getRoles();

    public String getLastName();

    public void setLastName(String lastName);

    public String getFirstName();

    public void setFirstName(String firstName);

    public void setLocale(Locale locale);

    public Locale getLocale();
}
