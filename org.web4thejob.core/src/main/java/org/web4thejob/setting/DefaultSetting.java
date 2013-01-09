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

package org.web4thejob.setting;

import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.web4thejob.context.ContextUtil;

import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("prototype")
public class DefaultSetting<T extends Serializable> implements Setting<T> {
// ------------------------------ FIELDS ------------------------------

    private final SettingEnum id;
    private boolean hidden;

    private T value;

// --------------------------- CONSTRUCTORS ---------------------------

    protected DefaultSetting(SettingEnum id, T value) {
        this.id = id;
        setValue(value);
    }

    //used by spring
    protected DefaultSetting(SettingEnum id, String value) {
        this.id = id;
        coerceFromString(value);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public SettingEnum getId() {
        return id;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        if (value != null && !getType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(value.toString() + " (" + value.getClass().getName() + ") is not " +
                    "applicable value for setting " + toString() + " (" + getType().getName() + ")");
        }

        this.value = value;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Setting<T> clone() {
        try {
            return (Setting<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("clone of " + toString() + " failed.");
        }
    }

    @Override
    public String toString() {
        return id.name() + "=" + (value == null ? "<null>" : value.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        return obj instanceof Setting && hashCode() == obj.hashCode();

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        result = prime * result + (value == null ? 0 : value.hashCode());
        return result;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Comparable ---------------------


    @Override
    public int compareTo(Setting<?> o) {
        return id.compareTo(o.getId());
    }

// --------------------- Interface Setting ---------------------

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        return (Class<T>) id.getType();
    }

    @Override
    public Class<?> getSubType() {
        return id.getSubType();
    }

    @Override
    public void coerceFromString(String newValue) {
        setValue(ContextUtil.getBean(ConversionService.class).convert(newValue, getType()));
    }

    @Override
    public String coerceToString() {
        return ContextUtil.getBean(ConversionService.class).convert(value, String.class);
    }
}
