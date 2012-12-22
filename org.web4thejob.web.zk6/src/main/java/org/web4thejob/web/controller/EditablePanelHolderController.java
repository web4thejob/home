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

package org.web4thejob.web.controller;

import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.parameter.Category;
import org.web4thejob.orm.parameter.EntityTypeViewParameter;
import org.web4thejob.web.panel.ListViewPanel;
import org.web4thejob.web.panel.MutableEntityViewPanel;
import org.web4thejob.web.zbox.PanelDropDownBox;
import org.zkoss.zkplus.databind.DataBinder;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class EditablePanelHolderController<E extends EntityTypeViewParameter, B extends DataBinder,
        C extends PanelDropDownBox> extends AbstractComponentController<E, B, C> {


    @Override
    public boolean supportsEntity(Class<? extends Entity> entityType) {
        return EntityTypeViewParameter.class.isAssignableFrom(entityType);
    }

    @Override
    public boolean supportsComponent(Class<?> componentType) {
        return PanelDropDownBox.class.isAssignableFrom(componentType);
    }

    @Override
    protected void arrangeAfterUserChange() {
        entity.setValue(null);
        if (entity.getKey() != null) {
            component.reset();

            if (entity.getCategory() == Category.ENTITY_TYPE_ENTITY_VIEW_PARAM) {
                component.setPanelType(MutableEntityViewPanel.class);
            } else if (entity.getCategory() == Category.ENTITY_TYPE_LIST_VIEW_PARAM) {
                component.setPanelType(ListViewPanel.class);
            } else {
                throw new UnsupportedOperationException("value not supported: " + entity.getCategory().name());
            }

            component.setTargetType(ContextUtil.getMRS().getEntityMetadata(entity.getKey()).getEntityType());
        }
    }
}
