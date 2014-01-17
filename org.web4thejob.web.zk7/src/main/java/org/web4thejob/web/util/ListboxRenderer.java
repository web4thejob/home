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

package org.web4thejob.web.util;

import org.web4thejob.orm.Entity;
import org.web4thejob.orm.annotation.StatusHolder;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface ListboxRenderer extends ListitemRenderer<Entity> {
    public static final String ATTRIB_RENDER_SCHEME = RenderScheme.class.getName();
    public static final String ATTRIB_RENDER_ELEMENT = RenderElement.class.getName();
    public static final String ATTRIB_RENDER_CALLBACK = ItemRendererCallback.class.getName();
    public static final String ATTRIB_STATUS_HOLDER = StatusHolder.class.getName();

    public void arrangeForRenderScheme(Listbox listbox, RenderScheme renderScheme);

    public void arrangeForRenderScheme(Listbox listbox, RenderScheme renderScheme,
                                       ItemRendererCallback itemRendererCallback);

}
