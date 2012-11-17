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

import nu.xom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.NestedRuntimeException;
import org.springframework.util.StringUtils;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandAware;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.security.UnauthorizedResourceException;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.dialog.AboutDialog;
import org.web4thejob.web.dialog.Dialog;
import org.web4thejob.web.dialog.PasswordDialog;
import org.web4thejob.web.dialog.SelectPanelDialog;
import org.web4thejob.web.panel.base.zk.AbstractZkLayoutPanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultDesktopLayoutPanel extends AbstractZkLayoutPanel implements DesktopLayoutPanel, L10nMessages,
        EventListener<Event> {
// ------------------------------ FIELDS ------------------------------

    private static final String ON_PANEL_LOAD = "onPanelLoad";
    private static final int DEFAULT_CHILDREN_COUNT = 1;
    public static final L10nString L10N_QUESTION_LOGOUT = new L10nString(DefaultDesktopLayoutPanel.class, "question",
            "Are you sure you want to logout?");
    private final Borderlayout regions = new Borderlayout();
    private Tree startMenu;
    private West menuRegion;
    private TabbedLayoutPanel tabbedRegion;

// --------------------------- CONSTRUCTORS ---------------------------

    public DefaultDesktopLayoutPanel() {
        ZkUtil.setParentOfChild((Component) base, regions);
        //regions.setWidth("100%");
        regions.setVflex("true");
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CommandAware ---------------------


    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.USER_DROPDOWN);
        supported.add(CommandEnum.TOOLS_DROPDOWN);
        return Collections.unmodifiableSet(supported);
    }

// --------------------- Interface EventListener ---------------------


    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_CLICK.equals(event.getName()) && event.getTarget().hasAttribute(DefaultMenuAuthorizationPanel
                .ELEMENT_PANEL)) {
            showBusy();
            Events.echoEvent(ON_PANEL_LOAD, event.getTarget(), null);
        } else if (ON_PANEL_LOAD.equals(event.getName())) {
            clearBusy();
            try {
                org.web4thejob.web.panel.Panel panel = ContextUtil.getPanel(event.getTarget().getAttribute
                        (DefaultMenuAuthorizationPanel
                                .ELEMENT_PANEL).toString());
                panel.setParent(tabbedRegion);
                if (panel instanceof DesignModeAware) {
                    ((DesignModeAware) panel).setInDesignMode(isInDesignMode());
                }
                if (panel instanceof I18nAware) {
                    ((I18nAware) panel).setL10nMode(getL10nMode());
                }
                panel.render();
            } catch (Exception e) {
                if (e instanceof NestedRuntimeException && ((NestedRuntimeException) e).contains
                        (UnauthorizedResourceException.class)) {
                    displayMessage(L10N_UNAUTHORIZED_ACCESS.toString(), true);
                } else {
                    displayMessage(L10N_UNEXPECTED_ERROR.toString(), true);
                }
                e.printStackTrace();
            }
        }
    }

// --------------------- Interface InitializingBean ---------------------


// --------------------- Interface Panel ---------------------

    @Override
    public void clearBusy() {
        Clients.clearBusy();
    }

    @Override
    public void showBusy() {
        Clients.showBusy(null);
    }

// --------------------- Interface ParentCapable ---------------------

    @Override
    public boolean accepts(org.web4thejob.web.panel.Panel panel) {
        return getSettingValue(SettingEnum.CHILDREN_COUNT, DEFAULT_CHILDREN_COUNT) > subpanels.size();
    }

// -------------------------- OTHER METHODS --------------------------

    private Treeitem appendTreeNode(Element node, Treeitem parentItem) {
        Treeitem item = null;
        if (DefaultMenuAuthorizationPanel.ELEMENT_MENU.equals(node.getLocalName())) {
            item = renderMenuItem(parentItem, XMLUtil.getTextualValue(node));
        } else if (DefaultMenuAuthorizationPanel.ELEMENT_PANEL.equals(node.getLocalName())) {
            String sid = XMLUtil.getTextualValue(node);
            if (ContextUtil.getSessionContext().getSecurityContext().isAccessible(sid)) {
                Query query = ContextUtil.getEntityFactory().buildQuery(PanelDefinition.class);
                query.addCriterion(PanelDefinition.FLD_BEANID, Condition.EQ, XMLUtil.getTextualValue(node));
                PanelDefinition panelDefinition = ContextUtil.getDRS().findUniqueByQuery(query);
                if (panelDefinition == null) {
                    return null;
                }
                item = renderPanelItem(parentItem, panelDefinition);
            }
        } else {
            return null;
        }

        if (item != null) {
            for (int i = 0; i < node.getChildElements().size(); i++) {
                appendTreeNode(node.getChildElements().get(i), item);
            }
        }

        return item;
    }

    private Treeitem renderMenuItem(Treeitem parent, String name) {
        Treeitem item = new Treeitem();
        if (parent.getTreechildren() == null) {
            new Treechildren().setParent(parent);
        }
        item.setParent(parent.getTreechildren());

        if (item.getTreerow() == null) {
            new Treerow().setParent(item);
        }
        Treecell cell = new Treecell(name, "img/FOLDER.png");
        cell.setParent(item.getTreerow());
        cell.setStyle("white-space:nowrap;");
        item.setTooltiptext(name);

        return item;
    }

    private Treeitem renderPanelItem(Treeitem parent, PanelDefinition panelDefinition) {
        Treeitem item = new Treeitem();
        if (parent.getTreechildren() == null) {
            new Treechildren().setParent(parent);
        }
        item.setParent(parent.getTreechildren());

        if (item.getTreerow() == null) {
            new Treerow().setParent(item);
        }
        Treecell cell = new Treecell(panelDefinition.getName(), StringUtils.hasText(panelDefinition.getImage()) ?
                panelDefinition.getImage() : "img/PANEL.png");
        cell.setParent(item.getTreerow());
        cell.setStyle("white-space:nowrap;");
        item.setTooltiptext(panelDefinition.getName());

        parent.setOpen(false);
        item.setParent(parent.getTreechildren());
        item.setAttribute(DefaultMenuAuthorizationPanel.ELEMENT_PANEL, panelDefinition.getBeanId());
        item.addEventListener(Events.ON_CLICK, this);
        item.addEventListener(ON_PANEL_LOAD, this);
        return item;
    }

    @Override
    protected void beforeAdd(org.web4thejob.web.panel.Panel panel) {
        final Center center = new Center();
        center.setParent(regions);
        //center.setFlex(true);
        panel.attach(center);
    }

    @Override
    protected Collection<org.web4thejob.web.panel.Panel> getRenderedOrderOfChildren() {
        return subpanels;
    }

    @Override
    protected boolean isActive(org.web4thejob.web.panel.Panel panel) {
        return true;
    }

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.SESSION_INFO.equals(command.getId())) {
            SessionInfoPanel panel = ContextUtil.getDefaultPanel(SessionInfoPanel.class);
            panel.setParent(tabbedRegion);
            panel.render();
        } else if (CommandEnum.DESIGN_PANEL_ENTITY_VIEW.equals(command.getId())) {
            displayPanelForDesign(ContextUtil.getDefaultPanel(EntityViewPanel.class));
        } else if (CommandEnum.DESIGN_PANEL_LIST_VIEW.equals(command.getId())) {
            displayPanelForDesign(ContextUtil.getDefaultPanel(ListViewPanel.class));
        } else if (CommandEnum.DESIGN_PANEL_HTML_VIEW.equals(command.getId())) {
            displayPanelForDesign(ContextUtil.getDefaultPanel(HtmlViewPanel.class));
        } else if (CommandEnum.DESIGN_PANEL_IFRAME_VIEW.equals(command.getId())) {
            displayPanelForDesign(ContextUtil.getDefaultPanel(FramePanel.class));
        } else if (CommandEnum.DESIGN_PANEL_TABBED_VIEW.equals(command.getId())) {
            displayPanelForDesign(ContextUtil.getDefaultPanel(TabbedLayoutPanel.class));
        } else if (CommandEnum.DESIGN_PANEL_BORDERED_VIEW.equals(command.getId())) {
            displayPanelForDesign(ContextUtil.getDefaultPanel(BorderedLayoutPanel.class));
        } else if (CommandEnum.DESIGN_PANEL_OTHER.equals(command.getId())) {
            Dialog dialog = ContextUtil.getDefaultDialog(SelectPanelDialog.class);
            dialog.setInDesignMode(isInDesignMode());
            dialog.setL10nMode(getL10nMode());
            dialog.show(new SelectPanelResponse());
        } else if (CommandEnum.REFRESH_CONTEXT.equals(command.getId())) {
            ContextUtil.getSessionContext().refresh();
            ContextUtil.getDRS().evictCache();
            ReloadableResourceBundleMessageSource messageSource = ContextUtil.getBean
                    (ReloadableResourceBundleMessageSource.class);
            messageSource.clearCacheIncludingAncestors();
        } else if (CommandEnum.DESIGN_MODE.equals(command.getId())) {
            final boolean designMode = (Boolean) command.getValue();
            setInDesignMode(designMode);
            render();
        } else if (CommandEnum.LOCALIZATION_MODE.equals(command.getId())) {
            final boolean l12nMode = (Boolean) command.getValue();
            setL10nMode(l12nMode);
            render();
        } else if (CommandEnum.LOGOUT.equals(command.getId())) {
            Messagebox.show(L10N_QUESTION_LOGOUT.toString(), L10nMessages.L10N_MSGBOX_TITLE_QUESTION.toString(),
                    new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL}, Messagebox.QUESTION,
                    new EventListener<Messagebox.ClickEvent>() {
                        @Override
                        public void onEvent(Messagebox.ClickEvent event) throws Exception {
                            if (event.getButton() == Messagebox.Button.OK) {
                                Executions.sendRedirect("j_spring_security_logout");
                            }
                        }
                    });
        } else if (CommandEnum.SAVE_DESKTOP.equals(command.getId())) {
            saveDesktop();
        } else if (CommandEnum.CHANGE_PASSWORD.equals(command.getId())) {
            changePassword();
        } else if (CommandEnum.ABOUT.equals(command.getId())) {
            AboutDialog aboutDialog = ContextUtil.getDefaultDialog(AboutDialog.class);
            aboutDialog.show(null);
        } else {
            super.processValidCommand(command);
        }
    }

    @Override
    public void render() {
        super.render();
        if (subpanels.isEmpty()) {
            tabbedRegion = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
            tabbedRegion.setSettingValue(SettingEnum.HONOR_ADOPTION_REQUEST, true);
            tabbedRegion.setParent(this);
            tabbedRegion.setUnsavedSettings(false);
            tabbedRegion.render();
        } else {
            tabbedRegion = (TabbedLayoutPanel) subpanels.get(0);
        }

        if (menuRegion == null && getSettingValue(SettingEnum.WEST_ENABLED, false)) {
            menuRegion = new West();
            menuRegion.setParent(regions);
        } else if (menuRegion != null && !getSettingValue(SettingEnum.WEST_ENABLED, false)) {
            menuRegion.detach();
            menuRegion = null;
        }

        if (menuRegion != null) {
            menuRegion.setBorder("none");
            menuRegion.setWidth(getSettingValue(SettingEnum.WEST_WIDTH, "250px"));
            menuRegion.setSplittable(getSettingValue(SettingEnum.WEST_SPLITTABLE, true));
            menuRegion.setCollapsible(getSettingValue(SettingEnum.WEST_COLLAPSIBLE, true));
            menuRegion.setOpen(getSettingValue(SettingEnum.WEST_OPEN, true));
        }

        Treeitem rootItem;
        if (menuRegion != null && startMenu == null) {
            startMenu = new Tree();
            startMenu.setSclass("w4tj-desktop-menu");
            startMenu.setParent(menuRegion);
            startMenu.setHflex("true");
            startMenu.setVflex("true");
            startMenu.setSpan(true);
            Treecols treecols = new Treecols();
            treecols.setSizable(true);
            treecols.setParent(startMenu);
            new Treecol().setParent(treecols);
            new Treechildren().setParent(startMenu);

            rootItem = new Treeitem();
            rootItem.setParent(startMenu.getTreechildren());
            rootItem.setImage("img/ROOT.png");
            rootItem.setLabel(ContextUtil.getSessionContext().getSecurityContext().getUserIdentity().getUserName
                    ());

            Element root = XMLUtil.getRootElement(ContextUtil.getSessionContext().getSecurityContext()
                    .getAuthorizationMenu());
            for (int i = 0; i < root.getChildElements().size(); i++) {
                Treeitem item = appendTreeNode(root.getChildElements().get(i), rootItem);
                if (i == 0 && item != null) {
                    item.setOpen(true);
                }
            }
        } else if (menuRegion == null && startMenu != null) {
            startMenu.detach();
            startMenu = null;
            rootItem = null;
        }

        arrangeForState(PanelState.READY);
        activateCommands(true);
    }

    private void saveDesktop() {
        PanelDefinition panelDefinition = ORMUtil.getUserDesktop();
        if (panelDefinition != null) {
            if (ContextUtil.getMRS().deproxyEntity(panelDefinition.getOwner()).equals
                    (ContextUtil.getSessionContext()
                            .getSecurityContext().getUserIdentity())) {
                ContextUtil.getDWS().delete(panelDefinition);
            } else {
                updateBeanName("<temp>");
            }
        }

        setSettingValue(SettingEnum.WEST_OPEN, menuRegion != null ? menuRegion.isOpen() : false);
        String xml = toSpringXml();
        panelDefinition = ContextUtil.getEntityFactory().buildPanelDefinition();
        panelDefinition.setBeanId(XMLUtil.getRootElementId(xml));
        panelDefinition.setName(ContextUtil.getSessionContext().getSecurityContext().getUserIdentity()
                .getUserName() + "\\Desktop");
        panelDefinition.setType(CoreUtil.describeClass(getClass()));
        panelDefinition.setOwner(ContextUtil.getSessionContext().getSecurityContext().getUserIdentity());
        panelDefinition.setDefinition(XMLUtil.toSpringBeanXmlResource(xml));
        ContextUtil.getDWS().save(panelDefinition);
        updateBeanName(panelDefinition.getBeanId());
        ContextUtil.getSessionContext().refresh();
    }

    private void changePassword() {
        PasswordDialog passwordDialog = ContextUtil.getDefaultDialog(PasswordDialog.class,
                ContextUtil.getSessionContext().getSecurityContext().getUserIdentity(), true);
        passwordDialog.setL10nMode(getL10nMode());
        passwordDialog.show(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId()) {
                    String newPassword = message.getArg(MessageArgEnum.ARG_ITEM, String.class);
                    String oldPassword = message.getArg(MessageArgEnum.ARG_OLD_ITEM, String.class);
                    if (!ContextUtil.getSessionContext().getSecurityContext().renewPassword(oldPassword,
                            newPassword)) {
                        //huge fuckup, close down
                        ContextUtil.getSessionContext().getSecurityContext().clearContext();
                        Executions.sendRedirect(null);
                    }
                }
            }
        });
    }

    @Override
    protected void registerCommands() {
        super.registerCommands();

        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.USER_DROPDOWN, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.TOOLS_DROPDOWN, this));

        if (getCommandRenderer() != null) {
            getCommandRenderer().setAlign("end");
        }
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();
        registerSetting(SettingEnum.CHILDREN_COUNT, DEFAULT_CHILDREN_COUNT);

        registerSetting(SettingEnum.WEST_ENABLED, true);
        registerSetting(SettingEnum.WEST_OPEN, true);
        registerSetting(SettingEnum.WEST_COLLAPSIBLE, true);
        registerSetting(SettingEnum.WEST_SPLITTABLE, true);
        registerSetting(SettingEnum.WEST_WIDTH, "250px");

        registerSetting(SettingEnum.EAST_ENABLED, false);
        registerSetting(SettingEnum.EAST_OPEN, false);
        registerSetting(SettingEnum.EAST_COLLAPSIBLE, true);
        registerSetting(SettingEnum.EAST_SPLITTABLE, true);
        registerSetting(SettingEnum.EAST_WIDTH, "250px");
    }

    private void displayPanelForDesign(org.web4thejob.web.panel.Panel panel) {
        panel.setParent(tabbedRegion);
        if (panel instanceof DesignModeAware) {
            ((DesignModeAware) panel).setInDesignMode(true);
        }
        if (panel instanceof I18nAware) {
            ((I18nAware) panel).setL10nMode(getL10nMode());
        }
        panel.render();
        if (panel instanceof CommandAware && !panel.isPersisted()) {
            Command command = ((CommandAware) panel).getCommand(CommandEnum.CONFIGURE_SETTINGS);
            if (command != null) {
                command.process();
            }
        }
    }

// -------------------------- INNER CLASSES --------------------------

    private class SelectPanelResponse implements MessageListener {
        @Override
        public void processMessage(Message message) {
            if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId()) {
                org.web4thejob.web.panel.Panel panel = ContextUtil.getPanel(message.getArgs().get(MessageArgEnum
                        .ARG_ITEM)
                        .toString());
                displayPanelForDesign(panel);
            }
        }
    }

    @Override
    public void setInDesignMode(boolean designMode) {
        super.setInDesignMode(designMode);

        if (isInDesignMode()) {

        } else {

        }
    }
}
