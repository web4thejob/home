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

package org.web4thejob.util.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class StringToClassConverter implements Converter<String, Class<?>> {
    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface Converter ---------------------

    @Override
    public Class<?> convert(String source) {
        try {
            if (StringUtils.hasText(source)) {
                return Class.forName(source);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
