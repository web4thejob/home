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

package org.web4thejob.web.controller;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.web4thejob.orm.Entity;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.DataBinder;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */
public abstract class AbstractComponentController<E extends Entity, B extends DataBinder,
        C extends Component> implements ComponentController<E, B, C> {

    protected B dataBinder;
    protected E entity;
    protected C component;


    @Override
    public void setBinder(B dataBinder) {
        this.dataBinder = dataBinder;
    }

    @Override
    public void setEntity(E entity) {
        this.entity = entity;
    }

    @Override
    public void setComponent(C component) {
        this.component = component;
    }

    @Override
    public void onDirty(boolean dirty) {
        if (component != null && dataBinder != null && entity != null) {
            arrangeAfterUserChange();
        }
    }

    protected abstract void arrangeAfterUserChange();

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getClass()).append(component).append(dataBinder).append(entity).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return hashCode() == o.hashCode();
    }

}
