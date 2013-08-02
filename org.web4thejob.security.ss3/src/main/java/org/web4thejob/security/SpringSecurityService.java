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

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;

import java.util.Locale;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Service
public class SpringSecurityService implements SecurityService {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface SecurityService ---------------------

    @Override
    public String encodePassword(UserIdentity userIdentity, String value) {
        PasswordEncoder passwordEncoder;

        try {
            passwordEncoder = ContextUtil.getBean(PasswordEncoder.class);
        } catch (NoSuchBeanDefinitionException e) {
            return value;
        }

        return passwordEncoder.encode(value);
    }

    @Override
    public UserIdentity getAdministratorIdentity() {
        Query query = ContextUtil.getEntityFactory().buildQuery(UserIdentity.class);
        query.addCriterion(new Path(UserIdentity.FLD_USERNAME), Condition.EQ, UserIdentity.USER_ADMIN);
        UserIdentity userAdmin = ContextUtil.getDRS().findUniqueByQuery(query);
        if (userAdmin == null) {
            userAdmin = ContextUtil.getEntityFactory().buildUserIdentity();
            userAdmin.setCode(UserIdentity.USER_ADMIN);
            userAdmin.setFirstName("System");
            userAdmin.setLastName("Administrator");
            userAdmin.setPassword(encodePassword(userAdmin, UserIdentity.USER_ADMIN));
            userAdmin.setLocale(Locale.getDefault());
        }
        if (userAdmin.isNewInstance() || !userAdmin.isAccountNonExpired() || !userAdmin.isAccountNonLocked() ||
                !userAdmin.isCredentialsNonExpired() || !userAdmin.isEnabled()) {
            userAdmin.setCredentialsNonExpired(true);
            userAdmin.setAccountNonLocked(true);
            userAdmin.setAccountNonExpired(true);
            userAdmin.setEnabled(true);
            ContextUtil.getDWS().save(userAdmin);
        }

        return userAdmin;
    }

    @Override
    public UserIdentity getUserIdentity(String userName) {
        Query query = ContextUtil.getEntityFactory().buildQuery(UserIdentity.class);
        query.addCriterion(new Path(UserIdentity.FLD_USERNAME), Condition.EQ, userName);
        return ContextUtil.getDRS().findUniqueByQuery(query);
    }

    @Override
    public boolean isPasswordValid(UserIdentity userIdentity, String rawPassword) {
        PasswordEncoder passwordEncoder;

        try {
            passwordEncoder = ContextUtil.getBean(PasswordEncoder.class);
        } catch (NoSuchBeanDefinitionException e) {
            return true;
        }

        return passwordEncoder.matches(rawPassword, userIdentity.getPassword());
    }

    @Override
    public boolean renewPassword(UserIdentity userIdentity, String oldPassword, String newPassword) {
        if (isPasswordValid(userIdentity, oldPassword)) {
            ContextUtil.getDRS().refresh(userIdentity);
            userIdentity.setCredentialsNonExpired(true);
            userIdentity.setPassword(ContextUtil.getSecurityService().encodePassword(userIdentity, newPassword));
            ContextUtil.getDWS().save(userIdentity);
            Authentication authentication = ContextUtil.getSecurityService().authenticate(userIdentity.getCode(),
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
    @SuppressWarnings("unchecked")
    public <T> T authenticate(String username, String password, boolean useIfValid) {
        Authentication authentication = authenticate(username, password);
        if (authentication != null && useIfValid) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return (T) authentication;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T authenticate(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        AuthenticationManager authenticationManager = ContextUtil.getBean(BEAN_AUTHENTICATION_MANAGER,
                AuthenticationManager.class);

        try {
            return (T) authenticationManager.authenticate(authentication);
        } catch (AuthenticationException e) {
            return null;
        }
    }

}
