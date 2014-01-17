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

package org.web4thejob.web.zbox;

import org.web4thejob.web.panel.AuthorizationPolicyPanel;
import org.web4thejob.web.panel.DefaultAuthorizationPolicyPanel;
import org.zkoss.zul.Hbox;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class AuthorizationPolicyViewerBox extends Hbox implements RawValueBox<String> {
    // ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 1L;
    protected final AuthorizationPolicyPanel authorizationPolicyPanel = new DefaultAuthorizationPolicyPanel();

    // --------------------------- CONSTRUCTORS ---------------------------

    public AuthorizationPolicyViewerBox() {
        super.setHeight("450px");
        super.setWidth("100%");
        super.setSpacing("10px");
        authorizationPolicyPanel.attach(this);
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface RawValueBox ---------------------

    @Override
    public String getRawValue() {
        return authorizationPolicyPanel.getDefinition();
    }

    @Override
    public void setRawValue(String rawValue) {
        authorizationPolicyPanel.setDefinition(rawValue);
    }
}
