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

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class UserDetailsServiceImpl implements UserDetailsService {

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        Query query = ContextUtil.getEntityFactory().buildQuery(UserIdentity.class);
        query.addCriterion(new Path(UserIdentity.FLD_USERNAME), Condition.EQ, username);
        UserIdentity userIdentity = ContextUtil.getDRS().findUniqueByQuery(query);

        if (userIdentity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserDetailsExImpl(userIdentity);
    }

}
