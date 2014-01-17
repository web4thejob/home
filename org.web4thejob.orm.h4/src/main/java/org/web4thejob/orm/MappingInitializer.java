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

package org.web4thejob.orm;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.RenderSchemeUtil;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.security.RoleIdentity;
import org.web4thejob.security.RoleMembers;
import org.web4thejob.security.UserIdentity;
import org.web4thejob.util.CoreUtil;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */


/* package */class MappingInitializer implements ApplicationListener<ContextRefreshedEvent> {

    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;// do nothing if this is not the root context
        }

        ContextUtil.getMRS().refreshMetaCache();

        ensureAdministratorExists();

        //in order to support unit testing of seperate modules (like ss3)
        if (ContextUtil.getMRS().getEntityMetadata(RenderScheme.class) != null) {
            createDefaultRenderSchemes(SchemeType.LIST_SCHEME);
            createDefaultRenderSchemes(SchemeType.ENTITY_SCHEME);
        }
    }

    private void createDefaultRenderSchemes(SchemeType schemeType) {
        for (EntityMetadata entityMetadata : ContextUtil.getMRS().getEntityMetadatas()) {
            RenderScheme renderScheme = RenderSchemeUtil.getDefaultRenderScheme(entityMetadata.getEntityType(),
                    schemeType);
            if (renderScheme == null) {
                renderScheme = RenderSchemeUtil.createDefaultRenderScheme(entityMetadata.getEntityType(), schemeType);
                renderScheme.setOwner(ContextUtil.getSecurityService().getAdministratorIdentity());
                ContextUtil.getDWS().save(renderScheme);
            }
            CoreUtil.addSystemLock(renderScheme);
        }
    }

    private void ensureAdministratorExists() {
        UserIdentity userAdmin = ContextUtil.getSecurityService().getAdministratorIdentity();

        Query query = ContextUtil.getEntityFactory().buildQuery(RoleIdentity.class);
        query.addCriterion(new Path(RoleIdentity.FLD_AUTHORITY), Condition.EQ, RoleIdentity.ROLE_ADMINISTRATOR);
        RoleIdentity roleAdmin = ContextUtil.getDRS().findUniqueByQuery(query);
        if (roleAdmin == null) {
            roleAdmin = ContextUtil.getEntityFactory().buildRoleIdentity();
            roleAdmin.setCode(RoleIdentity.ROLE_ADMINISTRATOR);
            ContextUtil.getDWS().save(roleAdmin);
        }

        query = ContextUtil.getEntityFactory().buildQuery(RoleMembers.class);
        query.addCriterion(new Path(RoleMembers.FLD_ROLE), Condition.EQ, roleAdmin);
        query.addCriterion(new Path(RoleMembers.FLD_USER), Condition.EQ, userAdmin);
        RoleMembers adminMembers = ContextUtil.getDRS().findUniqueByQuery(query);
        if (adminMembers == null) {
            adminMembers = ContextUtil.getEntityFactory().buildRoleMembers();
            adminMembers.setRole(roleAdmin);
            adminMembers.setUser(userAdmin);
            ContextUtil.getDWS().save(adminMembers);
        }

        CoreUtil.addSystemLock(userAdmin);
        CoreUtil.addSystemLock(roleAdmin);
        CoreUtil.addSystemLock(adminMembers);
    }

}
