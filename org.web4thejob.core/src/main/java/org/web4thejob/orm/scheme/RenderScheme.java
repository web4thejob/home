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

package org.web4thejob.orm.scheme;

import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.security.Identity;

import java.util.List;
import java.util.Locale;

/**
 * <p>Internal entity type for managing render schemes. Render schemes are used by {@link
 * org.web4thejob.web.panel.ContentPanel
 * content panels} for controlling the layout, format and visibility of {@link Entity} instances of the orm layer. </p>
 *
 * @author Veniamin Isaias
 * @see RenderElement
 * @since 1.0.0
 */

public interface RenderScheme extends Entity {
    public static final String FLD_ID = "id";
    public static final String FLD_NAME = "name";
    public static final String FLD_FLAT_TARGET_TYPE = "flatTargetType";
    public static final String FLD_LOCALE = "locale";
    public static final String FLD_SCHEME_TYPE = "schemeType";
    public static final String FLD_COL_SPAN = "colSpan";
    public static final String FLD_PAGE_SIZE = "pageSize";
    public static final String FLD_INDEX = "index";


    public long getId();

    public Class<? extends Entity> getTargetType();

    public String getName();

    public void setName(String name);

    public Locale getLocale();

    public void setLocale(Locale locale);

    public SchemeType getSchemeType();

    public void setSchemeType(SchemeType schemeType);

    public int getColSpan();

    public void setColSpan(int colSpan);

    public RenderElement addElement(PathMetadata propertyPath);

    public RenderElement addElement(PropertyMetadata propertyMetadata);

    public RenderElement addElement(RenderElement renderElement);

    public int getIndex();

    public void setIndex(int index);

    public List<RenderElement> getElements();

    public String getFlatTargetType();

    public void setFriendlyName(String friendlyName);

    public String getFriendlyName();

    public Integer getPageSize();

    public void setPageSize(Integer size);

    public Identity getOwner();

    public void setOwner(Identity owner);
}
