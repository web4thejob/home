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

package org.web4thejob.web.panel;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.web4thejob.security.SecuredResource;

/**
 * <p>The main building block for designing every UI.</p> <p>Panels are distinguished in two different types: {@link
 * ContentPanel content panels} which are used to display some content(database fields, html text, images etc) and
 * {@link LayoutPanel layout panels} which are used for positioning child panels according to specific rules.</p>
 * <p>Since these three interfaces follow the <a href="http://en.wikipedia.org/wiki/Composite_pattern">Composite design
 * pattern</a>, designers are enabled (and encouraged) to construct nested hierarchies of panels in order to build
 * a user interface which is both functional and ellegant. </p>
 *
 * @author Veniamin Isaias
 * @see ContentPanel
 * @see LayoutPanel
 * @since 1.0.0
 */

public interface Panel extends InitializingBean, BeanNameAware, SecuredResource {
    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object obj);

    @Override
    public int hashCode();

    @Override
    public String toString();

    // -------------------------- OTHER METHODS --------------------------


    public void attach(Object container);

    public void clearBusy();

    public void detach();

    public Object getAttribute(String name);

    public <T> T getAttribute(String name, T defaultWhenNull);

    public String getBeanName();

    public int getIndex();

    public ParentCapable getParent();

    public boolean hasAttribute(String name);

    public boolean isAttached();

    public boolean isPersisted();

    public Object removeAttribute(String name);

    public void render();

    public <T> void setAttribute(String name, T value);

    public void setIndex(int index);

    public void setParent(ParentCapable parent);

    public void showBusy();

    public String toSpringXml();

    public void hightlightPanel(boolean highlight);

    /**
     * @since 3.2.1
     */
    public boolean isHighlighted();

    /**
     * @since 3.2.1
     */
    public String getImage();

    /**
     * @since 3.2.1
     */
    public String getSclass();

    /**
     * @since 3.2.1
     */
    public void setSclass(String sclass);
}
