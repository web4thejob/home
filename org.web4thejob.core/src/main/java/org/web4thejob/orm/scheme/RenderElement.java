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

import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PathMetadata;

/**
 * <p>Internal entity type for managing the definition of elements in a {@link RenderScheme}. The {@link SchemeType}
 * of the render scheme defines the actual nature of the render element. When the scheme type is {@link
 * SchemeType#ENTITY_SCHEME} the element represents a single attribute component (i.e. textbox, checkbox etc),
 * while when the scheme type is {@link SchemeType#LIST_SCHEME} the element represents a list column.</p>
 * <p>Think of render elements as wrappers of {@link org.web4thejob.orm.PropertyMetadata PropertyMetadata} instances
 * that
 * beside the type and className of the property, they hold meta data for controlling the alignment, format,
 * width and other aspects of the visual representation of the property.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */


public interface RenderElement extends Entity {
// ------------------------------ FIELDS ------------------------------

    public static final String ATTRIB_RENDER_ELEMENT = RenderElement.class.getName();
    public static final String FLD_ID = "id";
    public static final String FLD_RENDER_SCHEME = "renderScheme";

// -------------------------- OTHER METHODS --------------------------

    public String getAlign();

    public int getColSpan();

    public String getFlatPropertyPath();

    public String getFormat();

    public String getFriendlyName();

    public long getId();

    public int getIndex();

    public PathMetadata getPropertyPath();

    public RenderScheme getRenderScheme();

    public String getStyle();

    public void setAlign(String align);

    public void setColSpan(int colSpan);

    public void setFormat(String format);

    public void setFriendlyName(String friendlyName);

    public void setPropertyPath(PathMetadata propertyPath);

    public void setStyle(String style);

    public void setWidth(String width);

    public String getWidth();

    public void setReadOnly(boolean readOnly);

    public boolean isReadOnly();

    public void setHeight(String height);

    public String getHeight();

    /**
     * @since 3.3.0
     */
    public String getPropertyViewer();

    /**
     * @since 3.3.0
     */
    public void setPropertyViewer(String propertyViewer);

    /**
     * @since 3.3.0
     */
    public String getPropertyEditor();

    /**
     * @since 3.3.0
     */
    public void setPropertyEditor(String propertyEditor);
}
