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

package org.web4thejob.util.converter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Service
public class MyConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {
    // ------------------------------ FIELDS ------------------------------

    private GenericConversionService conversionService;

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface FactoryBean ---------------------

    @Override
    public ConversionService getObject() {
        return this.conversionService;
    }

    @Override
    public Class<? extends ConversionService> getObjectType() {
        return GenericConversionService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    // --------------------- Interface InitializingBean ---------------------

    @SuppressWarnings("rawtypes")
    @Override
    public void afterPropertiesSet() throws Exception {
        this.conversionService = createConversionService();
        // ConversionServiceFactory.addDefaultConverters(this.conversionService);

        Set<Converter> customConverters = new HashSet<Converter>(3);
        customConverters.add(new StringToClassConverter());
        customConverters.add(new ClassToStringConverter());
        customConverters.add(new EntityToStringConverter());
        ConversionServiceFactory.registerConverters(customConverters, this.conversionService);
    }

    // -------------------------- OTHER METHODS --------------------------

    protected GenericConversionService createConversionService() {
        return new DefaultConversionService();
    }
}
