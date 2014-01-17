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

package org.web4thejob.web.zbox;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.util.L10nMessages;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */
public class ColorDropDownBox extends AbstractBox<String> {
    private static final long serialVersionUID = 1L;
    private static final String ON_OPEN_ECHO = Events.ON_OPEN + "Echo";

    private final RenderElement renderElement;
    private Listbox listbox;
    private Popup popup;
    private String color;

    public ColorDropDownBox(PathMetadata pathMetadata) {
        super();
        renderElement = ContextUtil.getMRS().newInstance(RenderElement.class);
        renderElement.setPropertyPath(pathMetadata);
        renderElement.setFriendlyName(pathMetadata.getFriendlyName());
        marshallEmptyValue();
        addEventListener(Events.ON_CANCEL, this);
    }

    public ColorDropDownBox(RenderElement renderElement) {
        super();
        this.renderElement = renderElement;
        marshallEmptyValue();
        addEventListener(Events.ON_CANCEL, this);
    }


    @Override
    protected void onEdit() {
        popup();
    }

    @Override
    protected void marshallEmptyValue() {
        color = null;
        super.marshallEmptyValue();
    }

    @Override
    protected void marshallToString(String value) {
        color = value;
        super.marshallToString(value);
    }

    @Override
    protected PropertyBox getValueBox() {
        //PropertyBox propertyBox = new PropertyBox(true);
        PropertyBox propertyBox = new PropertyBox(renderElement);
        propertyBox.setParent(this);
        propertyBox.setTooltipLimit(getTooltipLimit());
        return propertyBox;
    }


    @Override
    protected String unmarshallToRawValue() {
        return color;
    }

    protected boolean isEmpty() {
        return color == null;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getName().equals(Events.ON_DOUBLE_CLICK) && event.getTarget() instanceof Listbox) {
            Listbox lbox = (Listbox) event.getTarget();
            if (listbox.getSelectedIndex() >= 0) {
                popup.close();
                setRawValue((String) lbox.getSelectedItem().getValue());
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
                setRawValue((String) lbox.getSelectedItem().getValue());
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
            listbox.setHeight("200px");
            listbox.setWidth("250px");
            listbox.setSpan("true");

            new Listhead().setParent(listbox);
            Listheader header = new Listheader(renderElement.getFriendlyName());
            header.setParent(listbox.getListhead());
            header.setHflex("min");

            listbox.addEventListener(Events.ON_DOUBLE_CLICK, this);
            listbox.addEventListener(Events.ON_CANCEL, this);
            listbox.addEventListener(Events.ON_OK, this);

            List<String> model = getModel();
            for (String color : model) {
                Listitem item = new Listitem();
                item.setParent(listbox);
                renderItem(item, color);

                if (listbox.getSelectedIndex() < 0 && this.color != null && this.color.equals(color)) {
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

    private void renderItem(Listitem item, Object data) {
        item.setValue(data);
        item.setTooltiptext(data.toString());

        Listcell cell = new Listcell();
        cell.setParent(item);
        cell.setVflex("true");

        Div div = new Div();
        div.setParent(cell);

        div.setStyle("height:15px; border-style: solid; border-color: black; border-width: 1px; background-color: " +
                data.toString());


    }

    private List<String> getModel() {
        List<String> colors = new ArrayList<String>();

        colors.add("white");

        colors.add("red");
        colors.add("orange");

        colors.add("green");
        colors.add("darkgreen");
        colors.add("olive");
        colors.add("yellowgreen");

        colors.add("blue");
        colors.add("darkturquoise");
        colors.add("darkcyan");
        colors.add("lightskyblue");

        colors.add("yellow");
        colors.add("gold");

        colors.add("silver");
        colors.add("darkgray");
        colors.add("gray");

        colors.add("blueviolet");
        colors.add("purple");
        colors.add("orchid");
        colors.add("beige");


        return colors;

    }

}
