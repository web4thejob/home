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
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.util.L10nMessages;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */

@SuppressWarnings("unchecked")
public class QueryDropDownBox extends AbstractBox<Query> {
    private static final long serialVersionUID = 1L;
    private static final String ON_OPEN_ECHO = Events.ON_OPEN + "Echo";

    private final RenderElement renderElement;
    private Listbox listbox;
    private Popup popup;
    private Query query;
    private Class<? extends Entity> targetType;

    public QueryDropDownBox(PathMetadata pathMetadata) {
        super();
        renderElement = ContextUtil.getMRS().newInstance(RenderElement.class);
        renderElement.setPropertyPath(pathMetadata);
        renderElement.setFriendlyName(pathMetadata.getFriendlyName());
        marshallEmptyValue();
        addEventListener(Events.ON_CANCEL, this);
    }

    public QueryDropDownBox(RenderElement renderElement) {
        super();
        this.renderElement = renderElement;
        marshallEmptyValue();
        addEventListener(Events.ON_CANCEL, this);
    }

    @Override
    protected void marshallEmptyValue() {
        query = null;
        super.marshallEmptyValue();
    }

    @Override
    protected void marshallToString(Query value) {
        query = value;
        super.marshallToString(query);
    }

    @Override
    protected void onEdit() {
        popup();
    }

    @Override
    protected Query unmarshallToRawValue() {
        return query;
    }

    protected boolean isEmpty() {
        return query == null;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getName().equals(Events.ON_DOUBLE_CLICK) && event.getTarget() instanceof Listbox) {
            Listbox lbox = (Listbox) event.getTarget();
            if (listbox.getSelectedIndex() >= 0) {
                popup.close();
                setRawValue((Query) lbox.getSelectedItem().getValue());
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
                setRawValue((Query) lbox.getSelectedItem().getValue());
            }
        } else {
            super.onEvent(event);
        }
    }

    private void popup() {
        if (popup == null) {
            popup = new Popup();
            popup.addEventListener(Events.ON_CANCEL, this);
            popup.addEventListener(ON_OPEN_ECHO, this);

            listbox = new Listbox();
            listbox.setParent(popup);
            listbox.setHeight("300px");
            listbox.setWidth("400px");
            listbox.setSpan("true");

            new Listhead().setParent(listbox);
            Listheader header = new Listheader(renderElement.getFriendlyName());
            header.setParent(listbox.getListhead());
            header.setHflex("min");

            listbox.addEventListener(Events.ON_DOUBLE_CLICK, this);
            listbox.addEventListener(Events.ON_CANCEL, this);
            listbox.addEventListener(Events.ON_OK, this);


            Query query = ContextUtil.getEntityFactory().buildQuery(Query.class);
            if (targetType != null) {
                query.addCriterion(Query.FLD_FLAT_TARGET_TYPE, Condition.EQ, targetType.getCanonicalName());
            }
            query.addOrderBy(Query.FLD_NAME);
            query.setCached(true);
            for (Entity entity : ContextUtil.getDRS().findByQuery(query)) {
                query = (Query) entity;
                Listitem item = new Listitem(query.getName());
                item.setValue(query);
                item.setParent(listbox);

                if (listbox.getSelectedIndex() < 0 && this.query != null && this.query.equals(query)) {
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

    @Override
    protected PropertyBox getValueBox() {
        PropertyBox propertyBox = new PropertyBox(renderElement);
        propertyBox.setParent(this);
        propertyBox.setTooltipLimit(getTooltipLimit());
        return propertyBox;
    }

    public void setTargetType(Class<? extends Entity> targetType) {
        this.targetType = targetType;
    }

    public void reset() {
        if (popup != null) {
            popup.detach();
            popup = null;
        }
    }


}
