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

package org.web4thejob.orm.validation;

import org.hibernate.validator.internal.engine.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.PathImpl;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class AssertTrueForPathValidator implements ConstraintValidator<AssertTrueForPath, Boolean> {
    private AssertTrueForPath assertTrueForPath;

    @Override
    public void initialize(AssertTrueForPath constraintAnnotation) {
        assertTrueForPath = constraintAnnotation;
    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {

        if (value == null || value) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();

            //shitty way but what to do...
            final Field field = ReflectionUtils.findField(ConstraintValidatorContextImpl.class, "basePath");
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, context, PathImpl.createPathFromString(assertTrueForPath.value()));

            context.buildConstraintViolationWithTemplate(assertTrueForPath.message()).addConstraintViolation();

            return false;
        }
    }
}
