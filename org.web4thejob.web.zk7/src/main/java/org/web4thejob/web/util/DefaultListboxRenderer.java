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

package org.web4thejob.web.util;

import org.springframework.stereotype.Service;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.annotation.StatusHolder;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.web.zbox.PropertyBox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Comparator;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Service
public class DefaultListboxRenderer implements ListboxRenderer {
    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface ListboxRenderer ---------------------

    public void arrangeForRenderScheme(Listbox listbox, RenderScheme renderScheme) {
        arrangeForRenderScheme(listbox, renderScheme, null);
    }

    @SuppressWarnings("rawtypes")
    public void arrangeForRenderScheme(Listbox listbox, RenderScheme renderScheme,
                                       ItemRendererCallback itemRendererCallback) {
        listbox.removeAttribute(ATTRIB_STATUS_HOLDER);
        listbox.removeAttribute(ATTRIB_RENDER_CALLBACK);
        //listbox.setOddRowSclass("web4thejob-listbox-oddRow");

        if (itemRendererCallback != null) {
            listbox.setAttribute(ATTRIB_RENDER_CALLBACK, itemRendererCallback);
        }

        listbox.setMold("paging");
        listbox.getPaginal().setDetailed(true);
        listbox.setPagingPosition("bottom");
        if (renderScheme.getPageSize() == null || renderScheme.getPageSize() <= 0) {
            listbox.setPageSize(1); //ignored
            listbox.setAutopaging(true);
        } else {
            listbox.setPageSize(renderScheme.getPageSize());
            listbox.setAutopaging(false);
        }


        // look for StatusHolder annotation
        PropertyMetadata propertyMetadata = ContextUtil.getMRS().getEntityMetadata(renderScheme.getTargetType())
                .getAnnotatedProperty(StatusHolder.class);
        if (propertyMetadata != null) {
            listbox.setAttribute(ATTRIB_STATUS_HOLDER, propertyMetadata);
        }

        // clear headers
        if (listbox.getListhead() == null) {
            new Listhead().setParent(listbox);
        }
        listbox.getListhead().setSizable(true);

        // clear rows
        ListModelList model = null;
        if (listbox.getModel() != null) {
            model = (ListModelList) listbox.getModel();
            listbox.setModel((ListModelList) null);
        }

        // create columns
        listbox.getListhead().getChildren().clear();
        RenderScheme oldScheme = (RenderScheme) listbox.getAttribute(ATTRIB_RENDER_SCHEME);
        listbox.setAttribute(ATTRIB_RENDER_SCHEME, renderScheme);
        for (RenderElement element : renderScheme.getElements()) {
            if (element.getPropertyPath().getLastStep().isOneToManyType() || element.getPropertyPath().getLastStep()
                    .isOneToOneType()) {
                continue;
            }

            Listheader listheader = new Listheader(element.getFriendlyName());
            listheader.setTooltiptext(element.getFriendlyName());
            listheader.setParent(listbox.getListhead());
            listheader.setAttribute(ATTRIB_RENDER_ELEMENT, element);
            if ("min".equals(element.getWidth()) || "max".equals(element.getWidth())) {
                listheader.setHflex(element.getWidth());
                listheader.setWidth(null);
            } else {
                listheader.setHflex(null);
                listheader.setWidth(element.getWidth());
            }
            listheader.setStyle("text-align:center;");
            if (element.getPropertyPath().isMultiStep() || element.getPropertyPath().getLastStep().isAssociationType
                    () || !Comparable.class.isAssignableFrom(element.getPropertyPath().getLastStep().getJavaType())) {
                listheader.setSortAscending(new FieldComparator(element.getPropertyPath(), true));
                listheader.setSortDescending(new FieldComparator(element.getPropertyPath(), false));
            } else {
                listheader.setSort("auto(" + element.getPropertyPath() + ")");
            }
            SortHandler sortHandler = new SortHandler();
            listheader.addEventListener(Events.ON_SORT, sortHandler);
            listheader.addEventListener(Events.ON_SORT + "Echo", sortHandler);
        }

        // reattach columns if previous and current models are compatible
        if (oldScheme == null || renderScheme.getTargetType() != null && model != null && renderScheme.getTargetType
                ().equals(oldScheme.getTargetType())) {
            listbox.setModel(model);
        }
    }

    // --------------------- Interface ListitemRenderer ---------------------

    public void render(Listitem item, Entity data, int index) throws Exception {
        boolean inactive = false;
        PropertyMetadata statusHolderProp = (PropertyMetadata) item.getListbox().getAttribute(ATTRIB_STATUS_HOLDER);
        if (statusHolderProp != null) {
            inactive = statusHolderProp.<Object, Entity>getValue(data).equals(statusHolderProp.getAnnotation(StatusHolder
                    .class).InactiveWhen());
        }

        for (Object child : item.getListbox().getListhead().getChildren()) {
            RenderElement element = (RenderElement) ((Component) child).getAttribute(ATTRIB_RENDER_ELEMENT);
            if (element != null) {
                item.setDraggable(data.getEntityType().getCanonicalName());
                Listcell listcell = new Listcell();
                listcell.setParent(item);
                listcell.setStyle("white-space:nowrap;");

                PropertyBox propertyBox = new PropertyBox(element);
                ZkUtil.setInactive(propertyBox, inactive);
                propertyBox.setParent(listcell);
                propertyBox.setTooltipLimit(40);
                propertyBox.setEntity(data);
            }
        }

        if (item.getListbox().getAttribute(ATTRIB_RENDER_CALLBACK) != null) {
            ((ItemRendererCallback) item.getListbox().getAttribute(ATTRIB_RENDER_CALLBACK)).afterRender(item, data,
                    index);
        }
    }

    // -------------------------- INNER CLASSES --------------------------

    private class FieldComparator implements Comparator<Entity> {
        public FieldComparator(PathMetadata pathMetadata, boolean ascending) {
            this.pathMetadata = pathMetadata;
            this.ascending = ascending;
        }

        private final PathMetadata pathMetadata;
        private final boolean ascending;

        @SuppressWarnings({"rawtypes", "unchecked"})
        public int compare(Entity e1, Entity e2) {
            int result = 0;
            Object o1 = pathMetadata.getValue(e1);
            Object o2 = pathMetadata.getValue(e2);

            if (o1 instanceof Comparable && o2 != null) {
                result = ((Comparable) o1).compareTo(o2);
            } else if (o2 instanceof Comparable && o1 != null) {
                result = ((Comparable) o2).compareTo(o1);
            } else if (o1 != null && o2 != null) {
                result = o1.toString().compareTo(o2.toString());
            } else if (o1 == null) {
                result = -1;
            } else {
                result = 1;
            }

            return result * (ascending ? 1 : -1);
        }
    }

    private class SortHandler implements EventListener<Event> {
        public void onEvent(Event event) throws Exception {
            if (Events.ON_SORT.equals(event.getName())) {
                Clients.showBusy(null);
                Events.echoEvent(Events.ON_SORT + "Echo", event.getTarget(), ((SortEvent) event).isAscending());
                event.stopPropagation();
            } else {
                Clients.clearBusy();
                ((Listheader) event.getTarget()).sort((Boolean) event.getData());
            }
        }
    }
}
