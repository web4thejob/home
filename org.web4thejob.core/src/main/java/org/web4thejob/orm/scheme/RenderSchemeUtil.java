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

package org.web4thejob.orm.scheme;

import org.apache.commons.lang.ArrayUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityFactory;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.annotation.Encrypted;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.util.CoreUtil;

import java.util.Locale;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public abstract class RenderSchemeUtil {

    public static RenderScheme getDefaultRenderScheme(Class<? extends Entity> targetType, SchemeType schemeType,
                                                      Locale locale) {
        Query query = ContextUtil.getBean(EntityFactory.class).buildQuery(RenderScheme.class);
        query.setCached(true);
        query.addCriterion(new Path(RenderScheme.FLD_FLAT_TARGET_TYPE), Condition.EQ, targetType.getName());
        query.addCriterion(new Path(RenderScheme.FLD_SCHEME_TYPE), Condition.EQ, schemeType);
        query.addCriterion(new Path(RenderScheme.FLD_LOCALE), Condition.EQ, locale);
        query.addOrderBy(new Path(RenderScheme.FLD_INDEX));
        query.addOrderBy(new Path(RenderScheme.FLD_ID));

        RenderScheme renderScheme = ContextUtil.getDRS().findFirstByQuery(query);
        while (renderScheme == null && locale != null) {
            locale = getFallbackLocale(locale);
            if (locale != null) {
                renderScheme = getDefaultRenderScheme(targetType, schemeType, locale);
            }
        }

        return renderScheme;
    }

    public static RenderScheme getRenderScheme(String name, Class<? extends Entity> targetType, SchemeType schemeType) {
        RenderScheme renderScheme = getRenderScheme(name, targetType, schemeType, CoreUtil.getUserLocale());
        if (renderScheme == null && !CoreUtil.getUserLocale().equals(Locale.getDefault())) {
            renderScheme = getRenderScheme(name, targetType, schemeType, Locale.getDefault());
        }
        return renderScheme;

    }

    public static RenderScheme getRenderScheme(String name, Class<? extends Entity> targetType,
                                               SchemeType schemeType, Locale locale) {
        Query query = ContextUtil.getBean(EntityFactory.class).buildQuery(RenderScheme.class);
        query.setCached(true);
        query.addCriterion(new Path(RenderScheme.FLD_NAME), Condition.EQ, name);
        query.addCriterion(new Path(RenderScheme.FLD_FLAT_TARGET_TYPE), Condition.EQ, targetType.getName());
        query.addCriterion(new Path(RenderScheme.FLD_SCHEME_TYPE), Condition.EQ, schemeType);
        query.addCriterion(new Path(RenderScheme.FLD_LOCALE), Condition.EQ, locale);
        query.addOrderBy(new Path(RenderScheme.FLD_INDEX));
        query.addOrderBy(new Path(RenderScheme.FLD_ID));

        RenderScheme renderScheme = ContextUtil.getDRS().findFirstByQuery(query);
        while (renderScheme == null && locale != null) {
            locale = getFallbackLocale(locale);
            if (locale != null) {
                renderScheme = getDefaultRenderScheme(targetType, schemeType, locale);
            }
        }

        return renderScheme;

    }

    public static RenderScheme getDefaultRenderScheme(Class<? extends Entity> targetType, SchemeType schemeType) {

        RenderScheme renderScheme = getDefaultRenderScheme(targetType, schemeType, CoreUtil.getUserLocale());
        if (renderScheme == null && !CoreUtil.getUserLocale().equals(Locale.getDefault())) {
            renderScheme = getDefaultRenderScheme(targetType, schemeType, Locale.getDefault());
        }
        return renderScheme;
    }

    public static RenderScheme createDefaultRenderScheme(Class<? extends Entity> targetType, SchemeType schemeType,
                                                         Locale locale) {
        return createDefaultRenderScheme(targetType, schemeType, locale, null);
    }

    public static RenderScheme createDefaultRenderScheme(Class<? extends Entity> targetType, SchemeType schemeType,
                                                         String[] exclude) {
        return createDefaultRenderScheme(targetType, schemeType, Locale.getDefault(), exclude);
    }


    public static RenderScheme createDefaultRenderScheme(Class<? extends Entity> targetType, SchemeType schemeType,
                                                         Locale locale, String[] exclude) {

        RenderScheme renderScheme = ContextUtil.getEntityFactory().buildRenderScheme(targetType);
        renderScheme.setLocale(locale);
        renderScheme.setSchemeType(schemeType);
        renderScheme.setName(ContextUtil.getMRS().getEntityMetadata(targetType).getFriendlyName());
        if (CoreUtil.getUserLocale().equals(Locale.getDefault())) {
            renderScheme.setFriendlyName(ContextUtil.getMRS().getEntityMetadata(targetType).getFriendlyName());
        } else {
            renderScheme.setFriendlyName(targetType.getSimpleName());
        }
        renderScheme.setIndex(0);

        for (PropertyMetadata propertyMetadata : ContextUtil.getMRS().getEntityMetadata(targetType)
                .getPropertiesMetadata()) {
            if (propertyMetadata.isIdentityIdentifier() ||
                    propertyMetadata.isOneToManyType() ||
                    propertyMetadata.isOneToOneType() ||
                    ArrayUtils.contains(exclude, propertyMetadata.getName())
                    ) {
                continue;
            } else if (schemeType == SchemeType.LIST_SCHEME && (propertyMetadata.isAnnotatedWith(Encrypted.class) ||
                    propertyMetadata.isBlobType() || propertyMetadata.isClobType())) {
                continue;
            }

            RenderElement renderElement = renderScheme.addElement(propertyMetadata);

            if (schemeType == SchemeType.LIST_SCHEME) {
                if (propertyMetadata.isOfJavaType(Boolean.class)) {
                    renderElement.setAlign("center");
                } else if (propertyMetadata.isOfJavaType(String.class) && propertyMetadata.getMaxLength() > 0 &&
                        propertyMetadata.getMaxLength() <= 5) {
                    renderElement.setAlign("center");
                } else if (propertyMetadata.isNumericType() && propertyMetadata.isIdentifier()) {
                    renderElement.setWidth("60px");
                }
            } else {
                if (propertyMetadata.isClobType() || propertyMetadata.isTextType()) {
                    renderElement.setStyle("white-space:normal;");
                }
                renderElement.setAlign(null);
            }
        }

        return renderScheme;
    }

    public static RenderScheme createDefaultRenderScheme(Class<? extends Entity> targetType, SchemeType schemeType) {
        return createDefaultRenderScheme(targetType, schemeType, Locale.getDefault());
    }

    public static Locale getFallbackLocale(Locale locale) {
        String name = locale.toString();
        if (!name.contains("_")) {
            return null;
        }

        Locale fallbackLocale;
        String[] parts = name.split("_");
        if (parts.length == 1) {
            return null;
        } else if (parts.length == 2) {
            fallbackLocale = new Locale(parts[0]);
        } else if (parts.length == 3) {
            fallbackLocale = new Locale(parts[0], parts[1]);
        } else {
            throw new IllegalArgumentException("unexpected locale: " + locale.toString());
        }

        return fallbackLocale;

    }

}
