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

package org.web4thejob.security;

import nu.xom.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.panel.*;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("session")
public class SpringSecurityContext implements SecurityContext, InitializingBean {
// ------------------------------ FIELDS ------------------------------

    private Set<String> revokedResources = new HashSet<String>();
    private Boolean administrator = null;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface SecurityContext ---------------------

    @Override
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserIdentity getUserIdentity() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsEx) {
            return ((UserDetailsEx) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getUserIdentity();
        }
        return null;
    }

    @Override
    public boolean hasRole(String role) {
        role = "ROLE_" + role;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            for (GrantedAuthority grantedAuthority : SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities()) {
                if (grantedAuthority.getAuthority().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isAccessible(String securityId) {
        if (isAdministrator()) {
            return true;
        }

        String tokens;
        String[] token;

        tokens = null;
        token = securityId.split(SecuredResource.SECURITY_PATH_DELIM);
        for (String aToken : token) {
            if (tokens == null) {
                tokens = aToken;
            } else {
                tokens += SecuredResource.SECURITY_PATH_DELIM + aToken;
            }
            if (revokedResources.contains(tokens)) {
                return false;
            }
        }

        tokens = null;
        token = securityId.split(SecuredResource.SECURITY_PATH_DELIM);
        for (int i = token.length - 1; i >= 0; i--) {
            if (tokens == null) {
                tokens = token[i];
            } else {
                tokens = token[i] + SecuredResource.SECURITY_PATH_DELIM + tokens;
            }
            if (revokedResources.contains(tokens)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isPasswordValid(String rawPassword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            return false;
        }


        PasswordEncoder passwordEncoder;
        SaltSource saltSource = null;

        try {
            passwordEncoder = ContextUtil.getBean(PasswordEncoder.class);
        } catch (NoSuchBeanDefinitionException e) {
            return true;
        }

        try {
            saltSource = ContextUtil.getBean(SaltSource.class);
        } catch (NoSuchBeanDefinitionException e) {
            // do nothing
        }

        if (saltSource != null) {
            return passwordEncoder.isPasswordValid(userDetails.getPassword(), rawPassword,
                    saltSource.getSalt(userDetails));
        } else {
            return passwordEncoder.isPasswordValid(userDetails.getPassword(), rawPassword, null);
        }
    }

    @Override
    public boolean isRenewCredentialsIdentity() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof CredentialsRenewAuthenticationToken;
    }

    @Override
    public boolean isAdministrator() {
        if (administrator == null) {
            administrator = hasRole(RoleIdentity.ROLE_ADMINISTRATOR);
        }
        return administrator;
    }

    @Override
    public boolean renewPassword(String oldPassword, String newPassword) {
        if (isPasswordValid(oldPassword)) {
            UserIdentity userIdentity = getUserIdentity();
            ContextUtil.getDRS().refresh(userIdentity);
            userIdentity.setCredentialsNonExpired(true);
            userIdentity.setPassword(ContextUtil.getSecurityService().encodePassword(userIdentity, newPassword));
            ContextUtil.getDWS().save(userIdentity);
            Authentication authentication = ContextUtil.getSecurityService().authenticate(userIdentity.getUserName(),
                    newPassword);
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsEx && ((UserDetailsEx)
                    authentication.getPrincipal()).getUserIdentity().equals(userIdentity)) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getAuthorizationMenu() {
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

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Element> elements = getAuthorizationElements();
        for (Element element : elements) {
            readRevokedResources(element);
        }
        revokedResources = Collections.unmodifiableSet(revokedResources);
    }

    private List<Element> getAuthorizationElements() {
        List<Element> elements = new ArrayList<Element>();

        Query query = ContextUtil.getEntityFactory().buildQuery(RoleIdentity.class);
        query.addCriterion(RoleIdentity.FLD_USERS + "." + RoleMembers.FLD_USER + "." + UserIdentity.FLD_ID,
                Condition.EQ, ((getUserIdentity() != null) ? getUserIdentity().getId() : -1));
        query.addOrderBy(RoleIdentity.FLD_INDEX);
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

    private void readRevokedResources(Element element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }

        if (element.getLocalName().equals(PanelsAuthorizationPanel.ROOT_ELEMENT) || element.getLocalName().equals
                (CommandsAuthorizationPanel.ROOT_ELEMENT) || element.getLocalName().equals
                (PanelCommandsAuthorizationPanel.ROOT_ELEMENT)) {
            for (int i = 0; i < element.getChildElements().size(); i++) {
                revokedResources.add(XMLUtil.getTextualValue(element.getChildElements().get(i)));
            }
        }
    }

}
