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
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;

import java.util.Locale;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class FullAccessSecurityService implements SecurityService {

    @Override
    public UserIdentity getAdministratorIdentity() {
        Query query = ContextUtil.getEntityFactory().buildQuery(UserIdentity.class);
        query.addCriterion(new Path(UserIdentity.FLD_USERNAME), Condition.EQ, UserIdentity.USER_ADMIN);
        UserIdentity userAdmin = ContextUtil.getDRS().findUniqueByQuery(query);
        if (userAdmin == null) {
            userAdmin = ContextUtil.getEntityFactory().buildUserIdentity();
            userAdmin.setCode(UserIdentity.USER_ADMIN);
            userAdmin.setPassword(encodePassword(userAdmin, UserIdentity.USER_ADMIN));
            userAdmin.setFirstName("test");
            userAdmin.setLastName("admin");
            userAdmin.setLocale(Locale.getDefault());
            ContextUtil.getDWS().save(userAdmin);
        }


        return userAdmin;
    }

    @Override
    public UserIdentity getUserIdentity(String userName) {
        return null;
    }

    @Override
    public boolean isPasswordValid(UserIdentity userIdentity, String rawPassword) {
        return true;
    }

    @Override
    public <T> T authenticate(String username, String password) {
        return null;
    }

    @Override
    public <T> T authenticate(String username, String password, boolean useIfValid) {
        return null;
    }

    @Override
    public boolean renewPassword(UserIdentity userIdentity, String oldPassword, String newPassword) {
        return false;
    }

    @Override
    public String encodePassword(UserIdentity userIdentity, String value) {
        return value;
    }


}
