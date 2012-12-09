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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class UserDetailsExImpl implements UserDetailsEx {
    // ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 1L;
    private final UserIdentity userIdentity;
    private final Collection<GrantedAuthority> authorities;

    // --------------------------- CONSTRUCTORS ---------------------------

    public UserDetailsExImpl(UserIdentity userIdentity) {
        if (userIdentity == null) {
            throw new IllegalArgumentException();
        }
        this.userIdentity = userIdentity;
        this.authorities = new ArrayList<GrantedAuthority>(1);
        loadAuthorities();
    }

    private void loadAuthorities() {
        if (userIdentity.isNewInstance()) {
            return;
        }

        ContextUtil.getTransactionWrapper().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Query query = ContextUtil.getEntityFactory().buildQuery(RoleMembers.class);
                query.addCriterion(new Path(RoleMembers.FLD_USER), Condition.EQ, userIdentity);
                for (Entity roleMembers : ContextUtil.getDRS().findByQuery(query)) {
                    RoleIdentity roleIdentity = ContextUtil.getMRS().deproxyEntity(((RoleMembers) roleMembers)
                            .getRole());
                    authorities.add(new GrantedAuthorityImpl(roleIdentity));
                }
            }
        });
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface UserDetails ---------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userIdentity.getPassword();
    }

    @Override
    public String getUsername() {
        return userIdentity.getCode();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userIdentity.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userIdentity.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userIdentity.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return userIdentity.isEnabled();
    }

    @Override
    public UserIdentity getUserIdentity() {
        return userIdentity;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + userIdentity.getCode();
    }
}
