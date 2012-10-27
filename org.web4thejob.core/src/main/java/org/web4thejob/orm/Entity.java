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

package org.web4thejob.orm;

import org.web4thejob.web.panel.DirtyListener;

import javax.validation.ConstraintViolation;
import java.io.Serializable;
import java.util.Set;

/**
 * <p>This interface is the heart of the mapping between pojo objects and databse records. All pojos of the
 * orm layer <strong>should implement the <code>Entity</code> interface</strong> in order to be valid for use withing
 * the framework. Classes that extend this interface are called entity types.</p>
 * <p>Users may construct new instances of the internal entity types by using the
 * {@link EntityFactory} bean usually invoked through
 * {@link org.web4thejob.context.ContextUtil#getEntityFactory() ContextUtil.getEntityFactory()}.</p>
 *
 * @author Veniamin Isaias
 * @see DataReaderService
 * @see DataWriterService
 * @see MetaReaderService
 * @since 1.0.0
 */

public interface Entity {

    public void addDirtyListener(DirtyListener dirtyListener);

    public Entity clone();

    public void merge(Entity source);

    @Override
    public boolean equals(Object obj);

    public Class<? extends Entity> getEntityType();

    public Serializable getIdentifierValue();

    @Override
    public int hashCode();

    public boolean isNewInstance();

    public void setAsNew();

    public String toRichString();

    public Set<ConstraintViolation<Entity>> validate();

    public <T> void setAttribute(String key, T value);

    public <T> T getAttribute(String key);

    public void removeAttribute(String key);

    public boolean hasAttribute(String key);

    public void calculate();

}
