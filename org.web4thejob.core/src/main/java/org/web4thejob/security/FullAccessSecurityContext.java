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

import nu.xom.Element;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.panel.AuthorizationPolicyPanel;
import org.web4thejob.web.panel.MenuAuthorizationPanel;

import java.util.ArrayList;
import java.util.List;

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
        //we return the menu of admin
        //copy of org.web4thejob.security.SpringSecurityContext.getAuthorizationElements()
        Element userMenu = new Element("uid_" + getUserIdentity().getId());

        for (Element element : getAuthorizationElements()) {
            if (element.getLocalName().equals(MenuAuthorizationPanel.ROOT_ELEMENT)) {
                if (element.getChildElements().size() != 1) {
                    throw new IllegalArgumentException();
                }

                Element startMenu = element.getChildElements().get(0);
                for (int i = 0; i < startMenu.getChildElements().size(); i++) {
                    userMenu.appendChild(startMenu.getChildElements().get(i).copy());
                }
            }
        }

        return userMenu.toXML();
    }

    private List<Element> getAuthorizationElements() {
        List<Element> elements = new ArrayList<Element>();

        Query query = ContextUtil.getEntityFactory().buildQuery(RoleIdentity.class);
        query.addCriterion(new Path(RoleIdentity.FLD_USERS).append(RoleMembers.FLD_USER).append(UserIdentity.FLD_ID),
                Condition.EQ, ((getUserIdentity() != null) ? getUserIdentity().getId() : -1));
        query.addOrderBy(new Path(RoleIdentity.FLD_INDEX));
        PropertyMetadata propertyMetadata = ContextUtil.getMRS().getPropertyMetadata(RoleIdentity.class,
                RoleIdentity.FLD_AUTHORIZATION_POLICY);

        for (Entity role : ContextUtil.getDRS().findByQuery(query)) {
            AuthorizationPolicy policy = propertyMetadata.getValue(role);
            if (policy != null && StringUtils.hasText(policy.getDefinition())) {
                Element rootElement = XMLUtil.getRootElement(policy.getDefinition());
                if (rootElement == null || !rootElement.getLocalName().equals(AuthorizationPolicyPanel
                        .ROOT_ELEMENT)) {
                    throw new IllegalArgumentException();
                }

                for (int i = 0; i < rootElement.getChildElements().size(); i++) {
                    elements.add(rootElement.getChildElements().get(i));
                }
            }
        }

        return elements;
    }

}
