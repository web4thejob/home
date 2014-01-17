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

package org.web4thejob.orm;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.annotation.UserIdHolder;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.security.Identity;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

/*package*/ class RenderSchemeImpl extends AbstractHibernateEntity implements RenderScheme {

    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String friendlyName;
    @NotBlank
    private String flatTargetType;
    @NotNull
    private Locale locale;
    @NotNull
    private SchemeType schemeType;
    @Range(min = 1, max = 3)
    private int colSpan = 1;
    private int index = Integer.MAX_VALUE;
    private List<RenderElement> elements = new ArrayList<RenderElement>();
    @UserIdHolder
    private Identity owner;
    private Integer pageSize;

    private Class<? extends Entity> targetType;

    @SuppressWarnings("unused")
    private int version;

    public RenderSchemeImpl() {
        super();
    }

    public RenderSchemeImpl(Class<? extends Entity> targetType) {
        this.targetType = targetType;
    }

    @Override
    public RenderElement addElement(PathMetadata propertyPath) {
        final RenderElementImpl element = new RenderElementImpl();
        element.setRenderScheme(this);
        element.setPropertyPath(propertyPath);
        element.setFormat(propertyPath.getLastStep().getFormat());
        element.setStyle(propertyPath.getLastStep().getStyle());
        element.setAlign(propertyPath.getLastStep().getAlign());
        element.setWidth(propertyPath.getLastStep().getWidth());
        element.setHeight(propertyPath.getLastStep().getHeight());
        if (CoreUtil.getUserLocale().equals(Locale.getDefault())) {
            element.setFriendlyName(propertyPath.getFriendlyName());
        } else {
            element.setFriendlyName(propertyPath.getPath());
        }
        elements.add(element);
        return element;
    }

    @Override
    public RenderElement addElement(PropertyMetadata propertyMetadata) {
        return addElement(ContextUtil.getMRS().getPropertyPath(propertyMetadata));
    }

    @Override
    public RenderElement addElement(RenderElement renderElement) {
        RenderElementImpl element = (RenderElementImpl) renderElement.clone();
        element.setRenderScheme(this);

        elements.add(element);
        return element;
    }

    @Override
    public int getColSpan() {
        return colSpan;
    }

    @Override
    public List<RenderElement> getElements() {
        return elements;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SchemeType getSchemeType() {
        return schemeType;
    }

    @Override
    public Class<? extends Entity> getTargetType() {
        if (targetType == null && flatTargetType != null) {
            targetType = ContextUtil.getBean(EntityFactory.class).toEntityType(flatTargetType);
        }
        return targetType;
    }

    @Override
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public void setElements(List<RenderElement> elements) {
        this.elements = elements;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setSchemeType(SchemeType schemeType) {
        this.schemeType = schemeType;
    }

    public void setTargetType(Class<? extends Entity> targetType) {
        this.targetType = targetType;
        this.flatTargetType = null;
    }

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }

    @Override
    public String getFlatTargetType() {
        if (flatTargetType == null && targetType != null) {
            flatTargetType = targetType.getName();
        }
        return flatTargetType;
    }

    public void setFlatTargetType(String flatTargetType) {
        this.flatTargetType = flatTargetType;
        this.targetType = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" [");
        if (schemeType == SchemeType.LIST_SCHEME) {
            sb.append(L10nMessages.L10N_SCHEME_LIST.toString());
        } else if (schemeType == SchemeType.ENTITY_SCHEME) {
            sb.append(L10nMessages.L10N_SCHEME_ENTITY.toString());
        }
        sb.append(", ");
        sb.append(locale);
        sb.append("]");

        return sb.toString();
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public Integer getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(Integer size) {
        pageSize = size;
    }

    @Override
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public Identity getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Identity owner) {
        this.owner = owner;
    }
}
