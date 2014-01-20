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

import org.web4thejob.security.Identity;

/**
 * <p>Internal entity type for managing persisted panel instances.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface PanelDefinition extends Entity {
    public static final String FLD_BEANID = "beanId";
    public static final String FLD_NAME = "name";
    public static final String FLD_TYPE = "type";
    public static final String FLD_OWNER = "owner";
    public static final String FLD_TAGS = "tags";
    public static final String FLD_IMAGE = "image";

    public String getBeanId();

    public String getDefinition();

    public String getDescription();

    public long getId();

    public String getName();

    public void setBeanId(String beanId);

    public void setDefinition(String definition);

    public void setDescription(String description);

    public void setName(String name);

    public String getType();

    public void setType(String type);

    public Identity getOwner();

    public void setOwner(Identity owner);

    public void setTags(String tags);

    public String getTags();

    public void setImage(String image);

    public String getImage();
}
