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

package org.web4thejob.web.zbox;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class EntityDropdownBox extends AbstractBox<Entity> {
    // ------------------------------ FIELDS ------------------------------
    private static final long serialVersionUID = 1L;
    private static final String ON_OPEN_ECHO = Events.ON_OPEN + "Echo";

    private final RenderElement renderElement;
    private Listbox listbox;
    private Popup popup;
    private Entity entity;

// --------------------------- CONSTRUCTORS ---------------------------

    public EntityDropdownBox(PathMetadata pathMetadata) {
        super();
        renderElement = ContextUtil.getMRS().newInstance(RenderElement.class);
        renderElement.setPropertyPath(pathMetadata);
        renderElement.setFriendlyName(pathMetadata.getFriendlyName());
        marshallEmptyValue();
        addEventListener(Events.ON_CANCEL, this);
    }

    public EntityDropdownBox(RenderElement renderElement) {
        super();
        this.renderElement = renderElement;
        marshallEmptyValue();
        addEventListener(Events.ON_CANCEL, this);
    }

    @Override
    protected void marshallEmptyValue() {
        entity = null;
        super.marshallEmptyValue();
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface EventListener ---------------------

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getName().equals(Events.ON_DOUBLE_CLICK) && event.getTarget() instanceof Listbox) {
            Listbox lbox = (Listbox) event.getTarget();
            if (listbox.getSelectedIndex() >= 0) {
                popup.close();
                setRawValue((Entity) lbox.getSelectedItem().getValue());
            }
        } else if (event.getName().equals(Events.ON_CANCEL)) {
            popup.close();
        } else if (event.getName().equals(ON_OPEN_ECHO)) {
            if (listbox.getSelectedIndex() >= 0) {
                Clients.scrollIntoView(listbox.getSelectedItem());
            } else {
                listbox.setSelectedIndex(0);
            }
        } else if (event.getName().equals(Events.ON_OK)) {
            Listbox lbox = (Listbox) event.getTarget();
            if (listbox.getSelectedIndex() >= 0) {
                popup.close();
                setRawValue((Entity) lbox.getSelectedItem().getValue());
            }
        } else {
            super.onEvent(event);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void marshallToString(Entity value) {
        entity = value;
        super.marshallToString(value);
    }

    @Override
    protected void onEdit() {
        popup();
    }

    private void popup() {
        if (popup == null) {
            popup = new Popup();
            popup.addEventListener(Events.ON_CANCEL, this);
            popup.addEventListener(ON_OPEN_ECHO, this);

            listbox = new Listbox();
            listbox.setParent(popup);
            listbox.setHeight("200px");
            listbox.setWidth("350px");
            listbox.setSpan("true");

            new Listhead().setParent(listbox);
            Listheader header = new Listheader(renderElement.getFriendlyName());
            header.setParent(listbox.getListhead());
            header.setHflex("min");
            //header.setSort("auto");

            listbox.addEventListener(Events.ON_DOUBLE_CLICK, this);
            listbox.addEventListener(Events.ON_CANCEL, this);
            listbox.addEventListener(Events.ON_OK, this);

            List<? extends Entity> model = getModel();
            for (Entity entity : model) {
                Listitem item = new Listitem();
                item.setParent(listbox);
                renderItem(item, entity);

                if (listbox.getSelectedIndex() < 0 && this.entity != null && this.entity.equals(entity)) {
                    item.setSelected(true);
                }
            }
        }

        if (listbox.getItemCount() == 0) {
            Messagebox.show(L10nMessages.L10N_EMPTY_LIST.toString(), L10nMessages.L10N_MSGBOX_TITLE_INFO.toString(),
                    new Messagebox.Button[]{Messagebox.Button.OK}, Messagebox.INFORMATION, null);
            return;
        }

        popup.setPage(getPage());
        popup.setParent(this);
        popup.open(this, "after_start");

        listbox.setFocus(true);
        Events.echoEvent(ON_OPEN_ECHO, popup, null);
    }

    private List<? extends Entity> getModel() {
        Query defaultQuery = CoreUtil.getDefaultQueryForTargetType(renderElement.getPropertyPath().getLastStep()
                .getAssociatedEntityMetadata().getEntityType());
        if (defaultQuery == null) {
            defaultQuery = ContextUtil.getEntityFactory().buildQuery(renderElement.getPropertyPath().getLastStep()
                    .getAssociatedEntityMetadata().getEntityType());
        }

        defaultQuery.setSubqueries(ORMUtil.buildUniqueKeyCriteria(ZkUtil.getOwningPanelOfComponent(this),
                renderElement));

        return ContextUtil.getDRS().findByQuery(defaultQuery);

    }

    private void renderItem(Listitem item, Object data) {
        item.setValue(data);
        item.setTooltiptext(data.toString());
        //item.setLabel(data.toString()); //needed for sorting
        Listcell cell = new Listcell();
        cell.setParent(item);
        cell.setStyle("white-space: nowrap");
        Html html = new Html(((Entity) data).toRichString());
        html.setParent(cell);
    }

    @Override
    protected Entity unmarshallToRawValue() {
        return entity;
    }

    protected boolean isEmpty() {
        return entity == null;
    }

}

