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

package org.web4thejob.orm.validation;

import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */
public class ServerPathValidator implements ConstraintValidator<ServerPath, String> {

    private String serverPath;

    @Override
    public void initialize(ServerPath constraintAnnotation) {
        serverPath = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) return true;

        try {
            return ContextUtil.resourceExists(value) && ContextUtil.getResource(value).getFile().isDirectory();
        } catch (IOException e) {
            return false;
        }
    }
}
