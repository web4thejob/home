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

import org.web4thejob.orm.query.Query;

/**
 * @author Veniamin Isaias
 * @since 3.5.2
 */
public interface EntityHierarchy<P extends EntityHierarchyParent, C extends EntityHierarchyItem> extends Entity {

    public P getParent();

    public void setParent(P parent);

    public C getChild();

    public void setChild(C child);

    public long getSorting();

    public void setSorting(long order);

    public Query getRootItems();

    public Class<C> getChildType();

    public Class<P> getParentType();


}
