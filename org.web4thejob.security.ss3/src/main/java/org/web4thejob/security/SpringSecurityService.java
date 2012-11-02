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

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.web4thejob.context.ContextUtil;
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
        SaltSource saltSource = null;

        try {
            passwordEncoder = ContextUtil.getBean(PasswordEncoder.class);
        } catch (NoSuchBeanDefinitionException e) {
            return value;
        }

        try {
            saltSource = ContextUtil.getBean(SaltSource.class);
        } catch (NoSuchBeanDefinitionException e) {
            // do nothing
        }

        if (saltSource != null) {
            return passwordEncoder.encodePassword(value, saltSource.getSalt(new UserDetailsExImpl(userIdentity)));
        } else {
            return passwordEncoder.encodePassword(value, null);
        }
    }

    @Override
    public UserIdentity getAdministratorIdentity() {
        Query query = ContextUtil.getEntityFactory().buildQuery(UserIdentity.class);
        query.addCriterion(UserIdentity.FLD_USERNAME, Condition.EQ, UserIdentity.USER_ADMIN);
        UserIdentity userAdmin = ContextUtil.getDRS().findUniqueByQuery(query);
        if (userAdmin == null) {
            userAdmin = ContextUtil.getEntityFactory().buildUserIdentity();
            userAdmin.setUserName(UserIdentity.USER_ADMIN);
            userAdmin.setLastName("System");
            userAdmin.setFirstName("Administrator");
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
