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

package org.web4thejob.web.panel;

/**
 * <p>Defines the api of a content panel for managing the <code>user-menu</code> section of an {@link
 * org.web4thejob.security.AuthorizationPolicy AuthorizationPolicy} instance.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public interface MenuAuthorizationPanel<T> extends SecuredResourceAuthorizationPanel {
    public static final String ROOT_ELEMENT = "user-menu";
    public static final String ELEMENT_MENU = "menu";
    public static final String ELEMENT_PANEL = "panel";
    public static final String ELEMENT_COMMAND = "command";

    public T getRootItem();

    public T renderAddedMenu(T parent, String name);

    public T renderAddedPanel(T parent, Panel panel);


}
