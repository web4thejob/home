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

import org.hibernate.validator.internal.engine.PathImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.ConstraintOrigin;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationFactory;
import org.web4thejob.orm.Entity;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.ElementType;

/**
 * @author Veniamin Isaias
 * @since 3.2.1
 */
public class AdhocConstraintViolation implements ConstraintViolation<Entity> {
    private static final ConstraintHelper constraintHelper = new ConstraintHelper();
    private final String message;
    private final Path path;
    private final Entity rootBean;
    private final Object invalidValue;

    public AdhocConstraintViolation(String message, String path, Entity rootBean, Object invalidValue) {
        this.message = message;
        this.path = PathImpl.createPathFromString(path);
        this.rootBean = rootBean;
        this.invalidValue = invalidValue;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageTemplate() {
        return "";
    }

    @Override
    public Entity getRootBean() {
        return rootBean;
    }

    @Override
    public Class<Entity> getRootBeanClass() {
        return (Class<Entity>) rootBean.getClass();
    }

    @Override
    public Object getLeafBean() {
        return rootBean;
    }

    @Override
    public Path getPropertyPath() {
        return path;
    }

    @Override
    public Object getInvalidValue() {
        return invalidValue;
    }

    @Override
    public ConstraintDescriptor<AdhocConstraintAnnotation> getConstraintDescriptor() {
        AnnotationDescriptor<AdhocConstraintAnnotation> annotationDescriptor = new
                AnnotationDescriptor<AdhocConstraintAnnotation>(AdhocConstraintAnnotation.class);

        return new ConstraintDescriptorImpl<AdhocConstraintAnnotation>(AnnotationFactory.create(annotationDescriptor)
                , constraintHelper, ElementType.FIELD,
                ConstraintOrigin.DEFINED_LOCALLY);
    }
}
