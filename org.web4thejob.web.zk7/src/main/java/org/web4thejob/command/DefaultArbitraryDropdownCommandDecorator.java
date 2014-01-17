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

package org.web4thejob.command;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 3.0.0
 */
public class DefaultArbitraryDropdownCommandDecorator extends DefaultSubcommandsCommandDecorator implements
        ArbitraryDropdownCommandDecorator {
    private static final String ON_CLICK_ECHO = Events.ON_CLICK + "Echo";
    private boolean dirty = true;

    public DefaultArbitraryDropdownCommandDecorator(Command command) {
        super(command);
    }


    public static void renderSubCommands(final ArbitraryDropdownItems owner,
                                         Menupopup container) {

        EventListener<Event> clickListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (Events.ON_CLICK.equals(event.getName())) {
                    Clients.showBusy(null);
                    Events.echoEvent(ON_CLICK_ECHO, event.getTarget(), event.getData());
                } else {
                    Clients.clearBusy();
                    owner.onItemClicked(event.getTarget().getAttribute("key")
                            .toString());
                }
            }
        };

        Map<String, String> keyItems = new LinkedHashMap<String, String>(owner.getDropdownItems());
        for (String key : keyItems.keySet()) {
            Menuitem menuitem = new Menuitem(keyItems.get(key));
            menuitem.setParent(container);
            menuitem.setAttribute("key", key);
            menuitem.setImage("img/LINK.png");
            menuitem.addEventListener(Events.ON_CLICK, clickListener);
            menuitem.addEventListener(ON_CLICK_ECHO, clickListener);
        }
    }

    @Override
    protected void renderSubCommands(Command parent, Menupopup container) {
        if (!dirty || !(command.getOwner() instanceof ArbitraryDropdownItems))
            return;

        renderSubCommands((ArbitraryDropdownItems) command.getOwner(), container);
        if (container.getChildren().isEmpty()) return;

        dirty = false;
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled || combobutton.getDropdown() == null || combobutton.getDropdown().getChildren()
                .isEmpty());
    }
}
