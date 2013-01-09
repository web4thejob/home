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

import org.web4thejob.orm.Entity;

/**
 * <p>Intrnal entity type for defining abstract identity instances (similar to {@link java.security.Principal}). </p>
 *
 * @author Veniamin Isaias
 * @since 1.0.4
 */
public interface Identity extends Entity {
    public static final String FLD_ID = "id";
    public static final String FLD_CODE = "code";
    public static final String FLD_EMAIL = "email";

    public void setId(long id);

    public long getId();

    /**
     * @since 3.2.1
     */
    public String getEmail();

    /**
     * @since 3.2.1
     */
    public void setEmail(String email);

    /**
     * @since 3.2.1
     */
    public String getCode();

    /**
     * @since 3.2.1
     */
    public void setCode(String code);


}
