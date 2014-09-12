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

package org.web4thejob.web.controller;

import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.parameter.EntityTypeQueryParameter;
import org.web4thejob.web.zbox.QueryDropDownBox;
import org.zkoss.zkplus.databind.DataBinder;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class EditableQueryHolderController<E extends EntityTypeQueryParameter, B extends DataBinder,
        C extends QueryDropDownBox> extends AbstractComponentController<E, B, C> {

    public boolean supportsEntity(Class<? extends Entity> entityType) {
        return EntityTypeQueryParameter.class.isAssignableFrom(entityType);
    }

    public boolean supportsComponent(Class<?> componentType) {
        return QueryDropDownBox.class.isAssignableFrom(componentType);
    }

    @Override
    protected void arrangeAfterUserChange() {
        entity.setValue(null);
        if (entity.getKey() != null) {
            component.reset();

            component.setTargetType(ContextUtil.getMRS().getEntityMetadata(entity.getKey()).getEntityType());
        }

    }

}
