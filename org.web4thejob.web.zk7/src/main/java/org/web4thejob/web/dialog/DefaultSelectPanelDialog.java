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

package org.web4thejob.web.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.panel.*;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("prototype")
public class DefaultSelectPanelDialog extends AbstractDialog implements SelectPanelDialog {
    // ------------------------------ FIELDS ------------------------------

    public static final L10nString L10N_TAB_TITLE_PANELS = new L10nString(DefaultSelectPanelDialog.class,
            "tab_name_content_panels", "Content Panels");
    public static final L10nString L10N_TAB_TITLE_LAYOUTS = new L10nString(DefaultSelectPanelDialog.class,
            "tab_name_layout_panels", "Layout Panels");
    public static final L10nString L10N_HEADER_NAME = new L10nString(DefaultSelectPanelDialog.class, "header_name",
            "Name");
    public static final L10nString L10N_HEADER_TYPE = new L10nString(DefaultSelectPanelDialog.class, "header_type",
            "Type");
    public static final L10nString L10N_HEADER_DESCRIPTION = new L10nString(DefaultSelectPanelDialog.class,
            "header_description", "Description");
    public static final L10nString L10N_MSG_SYSTEM_DEFINED = new L10nString(DefaultSelectPanelDialog.class,
            "message_system_defined", "Template panel defined by the framework");
    private static final String ATTRIB_BEANID = "beanid";
    private final Tabbox tabbox = new Tabbox();
    private Listbox listboxPanels;
    private Listbox listboxLayouts;

    // --------------------------- CONSTRUCTORS ---------------------------

    protected DefaultSelectPanelDialog() {
        super();
        tabbox.setParent(dialogContent.getPanelchildren());
        tabbox.setHflex("true");
        tabbox.setVflex("true");
        new Tabs().setParent(tabbox);
        new Tabpanels().setParent(tabbox);

        drawPanelsTab();
        drawLayoutsTab();
    }

    private void drawPanelsTab() {
        Tab tab = new Tab(L10N_TAB_TITLE_PANELS.toString());
        tab.setParent(tabbox.getTabs());
        Tabpanel tabpanel = new Tabpanel();
        tabpanel.setParent(tabbox.getTabpanels());

        listboxPanels = new Listbox();
        listboxPanels.setParent(tabpanel);
        listboxPanels.setHflex("true");
        listboxPanels.setVflex("true");
        listboxPanels.setSpan(true);
        listboxPanels.addEventListener(Events.ON_DOUBLE_CLICK, this);

        new Listhead().setParent(listboxPanels);
        listboxPanels.getListhead().setSizable(true);
        Listheader header = new Listheader(L10N_HEADER_NAME.toString());
        header.setParent(listboxPanels.getListhead());
        header.setSort("auto");
        header = new Listheader(L10N_HEADER_DESCRIPTION.toString());
        header.setParent(listboxPanels.getListhead());
        header.setSort("auto");
        header = new Listheader(L10N_HEADER_TYPE.toString());
        header.setParent(listboxPanels.getListhead());
        header.setSort("auto");

        for (org.web4thejob.web.panel.Panel panel : ContextUtil.getSessionContext().getPanels(ContentPanel.class)) {
            if (panel != null) {
                Listitem item = new Listitem();
                item.setParent(listboxPanels);
                item.setAttribute(ATTRIB_BEANID, panel.getBeanName());

                String name;
                String descr;
                if (panel.isPersisted()) {
                    PanelDefinition panelDefinition = ORMUtil.getPanelDefinition(panel.getBeanName());
                    name = panelDefinition.getName();
                    descr = panelDefinition.getDescription();
                } else {
                    name = panel.getBeanName();
                    descr = L10N_MSG_SYSTEM_DEFINED.toString();
                }

                boolean featured = name.equals(StringUtils.uncapitalize(DefaultListViewPanel.class.getSimpleName()))
                        || name.equals(StringUtils.uncapitalize(DefaultMutableEntityViewPanel.class.getSimpleName()));

                Listcell cell = new Listcell(name);
                cell.setParent(item);
                cell.setImage(panel.getImage());
                if (featured) {
                    cell.setStyle("font-weight:bold;font-style:italic;");
                }

                cell = new Listcell(descr);
                cell.setParent(item);
                if (featured) {
                    cell.setStyle("font-weight:bold;font-style:italic;");
                }

                cell = new Listcell(panel.getClass().getSimpleName());
                cell.setParent(item);
                if (featured) {
                    cell.setStyle("font-weight:bold;font-style:italic;");
                }


            }
        }
    }

    private void drawLayoutsTab() {
        Tab tab = new Tab(L10N_TAB_TITLE_LAYOUTS.toString());
        tab.setParent(tabbox.getTabs());
        Tabpanel tabpanel = new Tabpanel();
        tabpanel.setParent(tabbox.getTabpanels());

        listboxLayouts = new Listbox();
        listboxLayouts.setParent(tabpanel);
        listboxLayouts.setHflex("true");
        listboxLayouts.setVflex("true");
        listboxLayouts.setSpan(true);
        listboxLayouts.addEventListener(Events.ON_DOUBLE_CLICK, this);

        new Listhead().setParent(listboxLayouts);
        listboxLayouts.getListhead().setSizable(true);
        Listheader header = new Listheader(L10N_HEADER_NAME.toString());
        header.setParent(listboxLayouts.getListhead());
        header.setSort("auto");
        header = new Listheader(L10N_HEADER_DESCRIPTION.toString());
        header.setParent(listboxLayouts.getListhead());
        header.setSort("auto");
        header = new Listheader(L10N_HEADER_TYPE.toString());
        header.setParent(listboxLayouts.getListhead());
        header.setSort("auto");

        for (org.web4thejob.web.panel.Panel panel : ContextUtil.getSessionContext().getPanels(LayoutPanel.class)) {
            if (panel != null && !ContentPanel.class.isInstance(panel)) {
                Listitem item = new Listitem();
                item.setParent(listboxLayouts);
                item.setAttribute(ATTRIB_BEANID, panel.getBeanName());

                String name;
                String descr;
                if (panel.isPersisted()) {
                    PanelDefinition panelDefinition = ORMUtil.getPanelDefinition(panel.getBeanName());
                    name = panelDefinition.getName();
                    descr = panelDefinition.getDescription();
                } else {
                    name = panel.getBeanName();
                    descr = L10N_MSG_SYSTEM_DEFINED.toString();
                }

                boolean featured = name.equals(StringUtils.uncapitalize(DefaultBorderedLayoutPanel.class
                        .getSimpleName())) || name.equals(StringUtils.uncapitalize(DefaultTabbedLayoutPanel.class
                        .getSimpleName()));


                Listcell cell = new Listcell(name);
                cell.setParent(item);
                cell.setImage(panel.getImage());
                if (featured) {
                    cell.setStyle("font-weight:bold;font-style:italic;");
                }

                cell = new Listcell(descr);
                cell.setParent(item);
                if (featured) {
                    cell.setStyle("font-weight:bold;font-style:italic;");
                }

                cell = new Listcell(panel.getClass().getSimpleName());
                cell.setParent(item);
                if (featured) {
                    cell.setStyle("font-weight:bold;font-style:italic;");
                }

            }
        }
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface EventListener ---------------------

    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_DOUBLE_CLICK.equals(event.getName())) {
            if (isOKReady()) {
                doOK();
            }
        } else {
            super.onEvent(event);
        }
    }

    @Override
    protected String prepareTitle() {
        return L10nUtil.getMessage(CommandEnum.DESIGN_PANEL.getClass(), CommandEnum.DESIGN_PANEL.name(),
                CommandEnum.DESIGN_PANEL.name());
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected Message getOKMessage() {
        String bean;
        if (tabbox.getSelectedIndex() == 0) {
            bean = listboxPanels.getSelectedItem().getAttribute(ATTRIB_BEANID).toString();
        } else {
            bean = listboxLayouts.getSelectedItem().getAttribute(ATTRIB_BEANID).toString();
        }

        return ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, MessageArgEnum.ARG_ITEM, bean);
    }

    @Override
    protected boolean isOKReady() {
        if (tabbox.getSelectedIndex() == 0) {
            return listboxPanels.getSelectedItem() != null;
        } else if (tabbox.getSelectedIndex() == 1) {
            return listboxLayouts.getSelectedItem() != null;
        }
        return false;
    }
}
