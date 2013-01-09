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

package org.web4thejob.orm;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

/*package*/class RenderElementImpl extends AbstractHibernateEntity implements RenderElement {
// ------------------------------ FIELDS ------------------------------

    private long id;
    @NotNull
    private RenderScheme renderScheme;
    @NotBlank
    private String friendlyName;
    @NotBlank
    private String flatPropertyPath;
    private String style;
    private String format;
    private String align;
    private int colSpan = 1;
    private int index;
    private String width;
    private String height;
    private PathMetadata propertyPath;
    private String propertyViewer;
    private String propertyEditor;

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    private boolean readOnly;

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public String getAlign() {
        return align;
    }

    @Override
    public void setAlign(String align) {
        this.align = align;
    }

    @Override
    public int getColSpan() {
        return colSpan;
    }

    @Override
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    @Override
    public String getFlatPropertyPath() {
        if (flatPropertyPath == null && propertyPath != null) {
            flatPropertyPath = propertyPath.getPath();
        }
        return flatPropertyPath;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public PathMetadata getPropertyPath() {
        if (propertyPath == null && flatPropertyPath != null) {
            if (getRenderScheme() == null) {
                throw new IllegalStateException("renderScheme cannot be null.");
            }

            propertyPath = ContextUtil.getMRS().getPropertyPath(getRenderScheme().getTargetType(), StringUtils
                    .delimitedListToStringArray(flatPropertyPath, Path.DELIMITER));
        }
        return propertyPath;
    }

    @Override
    public RenderScheme getRenderScheme() {
        return renderScheme;
    }

    public void setRenderScheme(RenderScheme renderScheme) {
        this.renderScheme = renderScheme;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getWidth() {
        return width;
    }

    @Override
    public void setWidth(String width) {
        this.width = width;
    }

    @Override
    public String getHeight() {
        return height;
    }

    @Override
    public void setHeight(String height) {
        this.height = height;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return flatPropertyPath;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Entity ---------------------

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }

// --------------------- Interface RenderElement ---------------------

    @Override
    public void setPropertyPath(PathMetadata propertyPath) {
        this.propertyPath = propertyPath;
        this.flatPropertyPath = null;
    }

// -------------------------- OTHER METHODS --------------------------

    public void setFlatPropertyPath(String flatPropertyPath) {
        this.flatPropertyPath = flatPropertyPath;
        this.propertyPath = null;
    }

    @Override
    public String getPropertyViewer() {
        return propertyViewer;
    }

    @Override
    public void setPropertyViewer(String propertyViewer) {
        this.propertyViewer = propertyViewer;
    }

    @Override
    public String getPropertyEditor() {
        return propertyEditor;
    }

    @Override
    public void setPropertyEditor(String propertyEditor) {
        this.propertyEditor = propertyEditor;
    }
}
