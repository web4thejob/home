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

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.ArbitraryDropdownItems;
import org.web4thejob.command.CommandAware;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.command.DefaultArbitraryDropdownCommandDecorator;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.web.panel.base.zk.AbstractZkBindablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Menupopup;

import java.util.Collections;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultNavigationChainPanel extends AbstractZkBindablePanel implements NavigationChainPanel {
    private static final String ON_CLICK_ECHO = Events.ON_CLICK + "Echo";
    private Entity targetEntity;
    private Hbox hbox = new Hbox();
    private A navigateLink = new A();
    private Html html = new Html();

    public DefaultNavigationChainPanel() {
        ZkUtil.setParentOfChild((Component) base, hbox);
        hbox.setHflex("true");
        hbox.setVflex("true");
        hbox.setSpacing("3px");
        hbox.setAlign("center");

        navigateLink.setParent(hbox);
        navigateLink.setVisible(false);
        navigateLink.setTooltiptext(L10nMessages.L10N_TOOLTIP_NAVIGATE.toString());
        NavigateLinkListener listener = new NavigateLinkListener();
        navigateLink.addEventListener(Events.ON_CLICK, listener);
        navigateLink.addEventListener(ON_CLICK_ECHO, listener);
        navigateLink.setStyle("font-size:120%;font-style:italic;");

        html.setParent(hbox);
        html.setZclass("z-label");
    }

    @Override
    protected void arrangeForMasterEntity() {
        if (getMasterEntity() == null) {
            setTargetEntity(null);
        } else if (getBindProperty() != null) {
            Entity trgEntity = ContextUtil.getDRS().findById(getTargetType(), getMasterEntity().getIdentifierValue());
            setTargetEntity(trgEntity);
        }
    }

    @Override
    protected void arrangeForNullTargetType() {
        super.arrangeForNullTargetType();
        arrangeForTargetEntity(null);
    }

    @Override
    protected void arrangeForNullMasterType() {
        super.arrangeForNullMasterType();
        arrangeForTargetEntity(null);
    }

    @Override
    protected void arrangeForTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
        if (targetEntity != null) {
            navigateLink.setVisible(true);
            navigateLink.setLabel(targetEntity.toRichString());
            html.setContent(null);
        } else {
            navigateLink.setVisible(false);
            navigateLink.setLabel(null);
            html.setContent(null);
        }
    }

    @Override
    protected boolean processEntityDeselection(Entity entity) {
        return (isMasterDetail() || getSettingValue(SettingEnum.ASSUME_DETAIL_BEHAVIOR,
                false)) && processEntityDeletion(entity);
    }

    @Override
    protected boolean processEntityInsertion(Entity entity) {
        if (canBind(entity)) {
            bindEcho(entity);
            return true;
        }
        return false;
    }

    @Override
    protected boolean processEntityUpdate(Entity entity) {
        if (hasTargetEntity() && getTargetEntity().equals(entity)) {
            setTargetEntity(entity);
            return true;
        }
        return false;
    }

    @Override
    protected boolean processEntityDeletion(Entity entity) {
        if (hasMasterEntity() && getMasterEntity().equals(entity)) {
            setMasterEntity(null);
            return true;
        } else if (hasTargetEntity() && getTargetEntity().equals(entity)) {
            setTargetEntity(null);
            return true;
        }
        return false;
    }

    @Override
    protected void arrangeForTargetType() {
        //nothing to do
    }

    @Override
    public boolean hasTargetEntity() {
        return targetEntity != null;
    }

    @Override
    public Entity getTargetEntity() {
        return targetEntity;
    }

    @Override
    public void processMessage(Message message) {
        if (message.getId() == MessageEnum.BIND_DIRECT) {
            bindEcho(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
        } else {
            super.processMessage(message);
        }
    }

    private class NavigateLinkListener implements EventListener<Event>, ArbitraryDropdownItems {

        @Override
        public void onEvent(Event event) throws Exception {
            if (Events.ON_CLICK.equals(event.getName())) {
                if (((MouseEvent) event).getKeys() != (MouseEvent.LEFT_CLICK + MouseEvent.SHIFT_KEY)) {
                    Clients.showBusy(null);
                    Events.echoEvent(ON_CLICK_ECHO, event.getTarget(), null);
                } else {
                    Panel panel = ZkUtil.getOwningPanelOfComponent(navigateLink);
                    if (panel instanceof CommandAware && ((CommandAware) panel).hasCommand
                            (CommandEnum.RELATED_PANELS)) {
                        Menupopup menupopup = new Menupopup();
                        DefaultArbitraryDropdownCommandDecorator.renderSubCommands(this, menupopup);
                        if (!menupopup.getChildren().isEmpty()) {
                            menupopup.setParent(navigateLink);
                            menupopup.open(navigateLink);
                        }
                    }
                }
            } else if (ON_CLICK_ECHO.equals(event.getName())) {
                Clients.clearBusy();
                onItemClicked(null);
            }
        }

        @Override
        public Map<String, String> getDropdownItems() {
            final Entity bindValue = targetEntity;
            if (bindValue == null) return Collections.emptyMap();
            return CoreUtil.getRelatedPanelsMap(bindValue.getEntityType(), MutableEntityViewPanel.class);
        }

        @Override
        public void onItemClicked(String key) {
            Panel panel = ZkUtil.getOwningPanelOfComponent(navigateLink);
            if (panel instanceof MessageAware && targetEntity != null) {
                if (targetEntity != null) {
                    Panel entityPanel = CoreUtil.getEntityViewPanel(targetEntity, key);
                    if (entityPanel != null) {
                        ((MessageAware) panel).dispatchMessage(ContextUtil.getMessage(MessageEnum.ADOPT_ME,
                                entityPanel));
                    }
                }
            }
        }

    }

}
