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

package org.web4thejob.util;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class L10nString implements java.io.Serializable, Comparable<L10nString>, CharSequence {
    private static final long serialVersionUID = 1L;
    private final String key;
    private final Class<?> clazz;
    private final String defaultValue;

    public L10nString(String key, String defaultValue) {
        this.key = key;
        this.clazz = getClass();
        this.defaultValue = defaultValue;
    }

    public L10nString(String key, Object[] args, String defaultValue) {
        this.key = key;
        this.clazz = getClass();
        this.defaultValue = defaultValue;
    }

    public L10nString(Class<?> clazz, String key, String defaultValue) {
        this.key = key;
        this.clazz = clazz;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return L10nUtil.getMessage(clazz, key, null, defaultValue);
    }

    public String toString(Object... args) {
        return L10nUtil.getMessage(clazz, key, args, defaultValue);
    }

    public Class<?> getDeclaringClass() {
        return clazz;
    }

    public String getCode() {
        return clazz.getName() + "." + key;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public int compareTo(L10nString o) {
        return toString().compareTo(o.toString());
    }
}
