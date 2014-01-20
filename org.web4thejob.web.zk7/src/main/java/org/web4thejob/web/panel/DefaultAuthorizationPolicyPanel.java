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

package org.web4thejob.web.panel;

import nu.xom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.security.AuthorizationPolicy;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.panel.base.zk.AbstractZkCommandAwarePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Panel;
import org.zkoss.zul.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultAuthorizationPolicyPanel extends AbstractZkCommandAwarePanel implements
        AuthorizationPolicyPanel {
    public static final L10nString L10N_TAB_MENU = new L10nString(DefaultAuthorizationPolicyPanel.class,
            "tab_menu", "Start Menu");
    public static final L10nString L10N_TAB_PANELS = new L10nString(DefaultAuthorizationPolicyPanel.class,
            "tab_panels", "Panels");
    public static final L10nString L10N_TAB_COMMANDS = new L10nString(DefaultAuthorizationPolicyPanel.class,
            "tab_commands", "Commands");
    public static final L10nString L10N_TAB_PANEL_COMMANDS = new L10nString(DefaultAuthorizationPolicyPanel.class,
            "tab_panel_commands", "Panel\\Commands");

    @SuppressWarnings("rawtypes")
    private final MenuAuthorizationPanel menuAuthorizationPanel;
    private final PanelsAuthorizationPanel panelsAuthorizationPanel;
    private final CommandsAuthorizationPanel commandsAuthorizationPanel;
    private final PanelCommandsAuthorizationPanel panelCommandsAuthorizationPanel;
    private final boolean readOnly;

    public DefaultAuthorizationPolicyPanel() {
        this(true);
    }

    public DefaultAuthorizationPolicyPanel(boolean readOnly) {
        this.readOnly = readOnly;
        Tabbox tabbox = new Tabbox();
        ZkUtil.setParentOfChild((Component) base, tabbox);
        ((Panel) base).setHflex("true");
        tabbox.setWidth("100%");
        tabbox.setVflex("true");
        new Tabs().setParent(tabbox);
        new Tabpanels().setParent(tabbox);

        menuAuthorizationPanel = ContextUtil.getDefaultPanel(MenuAuthorizationPanel.class, readOnly);
        if (menuAuthorizationPanel != null) {
            Tab tab = new Tab(L10N_TAB_MENU.toString());
            tab.setClosable(false);
            tab.setParent(tabbox.getTabs());
            tab.setSelected(true);
            Tabpanel tabpanel = new Tabpanel();
            tabpanel.setParent(tabbox.getTabpanels());
            menuAuthorizationPanel.attach(tabpanel);
        }

        //if (!readOnly) {
        panelsAuthorizationPanel = ContextUtil.getDefaultPanel(PanelsAuthorizationPanel.class, readOnly);
        if (panelsAuthorizationPanel != null) {
            Tab tab = new Tab(L10N_TAB_PANELS.toString());
            tab.setClosable(false);
            tab.setParent(tabbox.getTabs());
            Tabpanel tabpanel = new Tabpanel();
            tabpanel.setParent(tabbox.getTabpanels());
            panelsAuthorizationPanel.attach(tabpanel);
        }

        commandsAuthorizationPanel = ContextUtil.getDefaultPanel(CommandsAuthorizationPanel.class, readOnly);
        if (commandsAuthorizationPanel != null) {
            Tab tab = new Tab(L10N_TAB_COMMANDS.toString());
            tab.setClosable(false);
            tab.setParent(tabbox.getTabs());
            Tabpanel tabpanel = new Tabpanel();
            tabpanel.setParent(tabbox.getTabpanels());
            commandsAuthorizationPanel.attach(tabpanel);
        }

        panelCommandsAuthorizationPanel = ContextUtil.getDefaultPanel(PanelCommandsAuthorizationPanel.class, readOnly);
        if (panelCommandsAuthorizationPanel != null) {
            Tab tab = new Tab(L10N_TAB_PANEL_COMMANDS.toString());
            tab.setClosable(false);
            tab.setParent(tabbox.getTabs());
            Tabpanel tabpanel = new Tabpanel();
            tabpanel.setParent(tabbox.getTabpanels());
            panelCommandsAuthorizationPanel.attach(tabpanel);
        }
//        } else {
//            panelsAuthorizationPanel = null;
//            commandsAuthorizationPanel = null;
//            panelCommandsAuthorizationPanel = null;
//        }

    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public String getDefinition() {
        Element authorization = new Element(ROOT_ELEMENT);

        if (menuAuthorizationPanel != null) {
            authorization.appendChild(XMLUtil.getRootElement(menuAuthorizationPanel.getDefinition()).copy());
        }
        if (panelsAuthorizationPanel != null) {
            authorization.appendChild(XMLUtil.getRootElement(panelsAuthorizationPanel.getDefinition()).copy());
        }
        if (commandsAuthorizationPanel != null) {
            authorization.appendChild(XMLUtil.getRootElement(commandsAuthorizationPanel.getDefinition()).copy());
        }
        if (panelCommandsAuthorizationPanel != null) {
            authorization.appendChild(XMLUtil.getRootElement(panelCommandsAuthorizationPanel.getDefinition()).copy());
        }

        //System.out.print(authorization.toXML());
        return authorization.toXML();
    }


    @Override
    public void setDefinition(String xml) {
        if (!StringUtils.hasText(xml)) return;

        Element rootElement = XMLUtil.getRootElement(xml);
        if (rootElement == null || !rootElement.getLocalName().equals(ROOT_ELEMENT)) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < rootElement.getChildElements().size(); i++) {
            Element element = rootElement.getChildElements().get(i);
            if (menuAuthorizationPanel != null && element.getLocalName().equals(MenuAuthorizationPanel.ROOT_ELEMENT)) {
                menuAuthorizationPanel.setDefinition(element.toXML());
            } else if (panelsAuthorizationPanel != null && element.getLocalName().equals(PanelsAuthorizationPanel
                    .ROOT_ELEMENT)) {
                panelsAuthorizationPanel.setDefinition(element.toXML());
            } else if (commandsAuthorizationPanel != null && element.getLocalName().equals(CommandsAuthorizationPanel
                    .ROOT_ELEMENT)) {
                commandsAuthorizationPanel.setDefinition(element.toXML());
            } else if (panelCommandsAuthorizationPanel != null && element.getLocalName().equals
                    (PanelCommandsAuthorizationPanel.ROOT_ELEMENT)) {
                panelCommandsAuthorizationPanel.setDefinition(element.toXML());
            }
        }

    }

    @Override
    public void render() {
        if (menuAuthorizationPanel != null) {
            menuAuthorizationPanel.setL10nMode(getL10nMode());
            menuAuthorizationPanel.render();
        }
        if (panelsAuthorizationPanel != null) {
            panelsAuthorizationPanel.setL10nMode(getL10nMode());
            panelsAuthorizationPanel.render();
        }
        if (commandsAuthorizationPanel != null) {
            commandsAuthorizationPanel.setL10nMode(getL10nMode());
            commandsAuthorizationPanel.render();
        }
        if (panelCommandsAuthorizationPanel != null) {
            panelCommandsAuthorizationPanel.setL10nMode(getL10nMode());
            panelCommandsAuthorizationPanel.render();
        }
        super.render();
    }

    @Override
    public void processMessage(Message message) {
        if (MessageEnum.ENTITY_SELECTED.equals(message.getId()) && message.getArg(MessageArgEnum.ARG_ITEM,
                Object.class) instanceof AuthorizationPolicy) {
            setDefinition(message.getArg(MessageArgEnum.ARG_ITEM, AuthorizationPolicy.class).getDefinition());
        } else {
            super.processMessage(message);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public MenuAuthorizationPanel<Treeitem> getMenuAuthorizationPanel() {
        return menuAuthorizationPanel;
    }
}
