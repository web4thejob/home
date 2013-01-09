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

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class FieldLocator {

    public static Object getFieldValue(Object object, String fieldName) throws Exception {

        Object target;
        if (AopUtils.isAopProxy(object)) {
            target = ((Advised) object).getTargetSource().getTarget();
        } else {
            target = object;
        }

        return PropertyAccessorFactory.forDirectFieldAccess(target).getPropertyValue(fieldName);

    }
}
