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
    private boolean readOnly;

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public String getFlatPropertyPath() {
        if (flatPropertyPath == null && propertyPath != null) {
            flatPropertyPath = propertyPath.getPath();
        }
        return flatPropertyPath;
    }

    public void setFlatPropertyPath(String flatPropertyPath) {
        this.flatPropertyPath = flatPropertyPath;
        this.propertyPath = null;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

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

    public void setPropertyPath(PathMetadata propertyPath) {
        this.propertyPath = propertyPath;
        this.flatPropertyPath = null;
    }

    public RenderScheme getRenderScheme() {
        return renderScheme;
    }

    public void setRenderScheme(RenderScheme renderScheme) {
        this.renderScheme = renderScheme;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

// ------------------------ CANONICAL METHODS ------------------------

    public String getHeight() {
        return height;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Entity ---------------------

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return flatPropertyPath;
    }

// --------------------- Interface RenderElement ---------------------

    public Serializable getIdentifierValue() {
        return id;
    }

// -------------------------- OTHER METHODS --------------------------

    public void setAsNew() {
        id = 0;
    }

    public String getPropertyViewer() {
        return propertyViewer;
    }

    public void setPropertyViewer(String propertyViewer) {
        this.propertyViewer = propertyViewer;
    }

    public String getPropertyEditor() {
        return propertyEditor;
    }

    public void setPropertyEditor(String propertyEditor) {
        this.propertyEditor = propertyEditor;
    }
}
