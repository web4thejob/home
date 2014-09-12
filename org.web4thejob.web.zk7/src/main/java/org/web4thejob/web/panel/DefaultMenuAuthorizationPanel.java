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
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.dialog.DefaultValueInputDialog;
import org.web4thejob.web.dialog.ValueInputDialog;
import org.web4thejob.web.panel.base.zk.AbstractZkContentPanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultMenuAuthorizationPanel extends AbstractZkContentPanel implements MenuAuthorizationPanel<Treeitem>,
        EventListener<Event> {
    // ------------------------------ FIELDS ------------------------------

    public DefaultMenuAuthorizationPanel() {
        this(true);
    }

    public DefaultMenuAuthorizationPanel(boolean readOnly) {
        Borderlayout regions = new Borderlayout();
        ZkUtil.setParentOfChild((Component) base, regions);
        regions.setWidth("100%");
        regions.setVflex("true");
        this.readOnly = readOnly;
        arrangeForReadOnly();

        final West west = new West();
        west.setParent(regions);
        //west.setFlex(true);
        west.setBorder("none");
        if (!readOnly) {
            west.setWidth("50%");
        } else {
            west.setWidth("100%");
        }
        west.setSplittable(true);
        west.setCollapsible(false);
        west.setOpen(true);
        tree = new Tree();
        tree.setParent(west);
        tree.setVflex("true");
        tree.setHflex("true");
        tree.setSpan(true);
        tree.addEventListener(Events.ON_SELECT, this);

        Treecols treecols = new Treecols();
        treecols.setSizable(true);
        treecols.setParent(tree);
        new Treecol(L10N_COLUMN_ITEMS.toString()).setParent(treecols);

        new Treechildren().setParent(tree);
        rootItem = getNewTreeitem(L10N_TREE_ITEM_ROOT.toString(), null);

        rootItem.setParent(tree.getTreechildren());
        rootItem.setDroppable(ELEMENT_PANEL);
        rootItem.addEventListener(Events.ON_DROP, this);
        rootItem.setAttribute(ITEM_ROOT, true);
        rootItem.setAttribute(ELEMENT_MENU, true);

        if (!readOnly) {
            final Center center = new Center();
            center.setParent(regions);
            center.setBorder("none");
            listbox = new Listbox();
            listbox.setParent(center);
            listbox.setVflex("true");
            listbox.setHflex("true");
            listbox.setSpan(true);
            listbox.addEventListener(Events.ON_SELECT, this);
            final Listhead listhead = new Listhead();
            listhead.setParent(listbox);
            final Listheader header = new Listheader(L10N_HEADER_PANELS.toString());
            header.setParent(listhead);
        } else {
            listbox = null;
        }

        arrangeForState(PanelState.READY);
    }

    private static final String ITEM_ROOT = "root";
    public static final L10nString L10N_HEADER_PANELS = new L10nString(DefaultMenuAuthorizationPanel.class,
            "list_header_panels", "Panels");
    public static final L10nString L10N_TREE_ITEM_ROOT = new L10nString(DefaultMenuAuthorizationPanel.class,
            "tree_item_root", "Start Menu");
    public static final L10nString L10N_COLUMN_ITEMS = new L10nString(DefaultMenuAuthorizationPanel.class,
            "tree_col_menu_items", "Menu items");
    private final Treeitem rootItem;
    private final Tree tree;

    // --------------------------- CONSTRUCTORS ---------------------------
    private final Listbox listbox;
    private final boolean readOnly;

    protected void arrangeForReadOnly() {
        if (!readOnly) {
            super.registerCommands();
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.ADD, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.EDIT, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.REMOVE, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_LEFT, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_UP, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_DOWN, this));
        }
    }

    @Override
    protected void arrangeForState(PanelState newState) {
        if (!readOnly) {
            super.arrangeForState(newState);
            if (newState == PanelState.FOCUSED) {
                boolean selected = tree.getSelectedItem() != null;
                getCommand(CommandEnum.ADD).setActivated(canAddMenu());
                getCommand(CommandEnum.EDIT).setActivated(canAddMenu());
                getCommand(CommandEnum.REMOVE).setActivated(selected && (tree.getSelectedItem().hasAttribute
                        (ELEMENT_MENU) || tree.getSelectedItem().hasAttribute(ELEMENT_PANEL)));
                getCommand(CommandEnum.MOVE_LEFT).setActivated(canAddMenu() && listbox.getSelectedItem() != null);
                getCommand(CommandEnum.MOVE_UP).setActivated(selected);
                getCommand(CommandEnum.MOVE_DOWN).setActivated(selected);
            }
        }
    }

    private boolean canAddMenu() {
        if (tree.getSelectedItem() != null) {
            if (tree.getSelectedItem().hasAttribute(ELEMENT_MENU)) {
                return true;
            }
        }
        return false;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public boolean isReadOnly() {
        return readOnly;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface CommandAware ---------------------

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.ADD);
        supported.add(CommandEnum.EDIT);
        supported.add(CommandEnum.REMOVE);
        supported.add(CommandEnum.MOVE_LEFT);
        supported.add(CommandEnum.MOVE_UP);
        supported.add(CommandEnum.MOVE_DOWN);
        return Collections.unmodifiableSet(supported);
    }

    // --------------------- Interface EventListener ---------------------

    public void onEvent(Event event) throws Exception {
        if (Events.ON_SELECT.equals(event.getName())) {
            arrangeForState(PanelState.FOCUSED);
        } else if (Events.ON_DROP.equals(event.getName())) {
            Treeitem target = (Treeitem) event.getTarget();
            Listitem source = (Listitem) ((DropEvent) event).getDragged();
            renderAddedPanel(target, (org.web4thejob.web.panel.Panel) listbox.getModel().getElementAt(source.getIndex
                    ()));
        }
    }

    // --------------------- Interface MessageListener ---------------------

    @Override
    public void processMessage(Message message) {
        if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId() && ValueInputDialog.class.isInstance(message
                .getSender())) {
            if (message.getArgs().containsKey(MessageArgEnum.ARG_NEW_ITEM)) {
                updateTreeitemLabel(tree.getSelectedItem(), message.getArgs().get(MessageArgEnum.ARG_NEW_ITEM)
                        .toString());
            } else {
                renderAddedMenu(tree.getSelectedItem(), message.getArgs().get(MessageArgEnum.ARG_ITEM).toString());
            }
        } else {
            super.processMessage(message);
        }
    }

    // --------------------- Interface Panel ---------------------

    @Override
    public void render() {
        super.render();

        if (listbox != null && listbox.getModel() == null) {
            ListModelList<org.web4thejob.web.panel.Panel> panels = new ListModelList<org.web4thejob.web.panel.Panel>();
            for (org.web4thejob.web.panel.Panel panel : ContextUtil.getSessionContext().getPanels(org.web4thejob.web
                    .panel.Panel
                    .class)) {
                if (panel.isPersisted() && !DesktopLayoutPanel.class.isInstance(panel)) {
                    panels.add(panel);
                }
            }
            listbox.setModel(panels);
            listbox.setItemRenderer(new ListitemRenderer<org.web4thejob.web.panel.Panel>() {
                public void render(Listitem item, org.web4thejob.web.panel.Panel data, int index) throws Exception {
                    Listcell cell = new Listcell();
                    cell.setStyle("white-space:nowrap;");
                    cell.setImage(data.getImage());
                    cell.setParent(item);
                    Html html = new Html(data.toString());
                    html.setParent(cell);
                    html.setStyle("margin-left: 5px;");

                    item.setValue(data);
                    item.setDraggable(ELEMENT_PANEL);
                }
            });
        }
    }


    public String getDefinition() {
        final Element userMenu = new Element(ROOT_ELEMENT);
        appendXMLNode(userMenu, rootItem);
        String xml = userMenu.toXML();
        return xml;
    }

    public void setDefinition(String xml) {
        if (rootItem.getTreechildren() != null) {
            rootItem.getTreechildren().detach();
            new Treechildren().setParent(rootItem);
        }
        if (!StringUtils.hasText(xml)) return;

        Element rootElement = XMLUtil.getRootElement(xml);
        if (rootElement == null || !rootElement.getLocalName().equals(ROOT_ELEMENT)) {
            throw new IllegalArgumentException();
        } else if (rootElement.getChildElements().size() != 1) {
            throw new IllegalArgumentException();
        }

        Element startMenu = rootElement.getChildElements().get(0);
        for (int i = 0; i < startMenu.getChildElements().size(); i++) {
            appendTreeNode(startMenu.getChildElements().get(i), rootItem);
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    private void appendTreeNode(Element node, Treeitem parentItem) {
        Treeitem item = null;
        if (ELEMENT_MENU.equals(node.getLocalName())) {
            item = renderAddedMenu(parentItem, XMLUtil.getTextualValue(node));
        } else if (ELEMENT_PANEL.equals(node.getLocalName())) {
            org.web4thejob.web.panel.Panel panel = getPanelSafe(XMLUtil.getTextualValue(node));
            if (panel != null) {
                item = renderAddedPanel(parentItem, panel);
            }
        } else {
            return;
        }

        if (item != null) {
            for (int i = 0; i < node.getChildElements().size(); i++) {
                appendTreeNode(node.getChildElements().get(i), item);
            }
        }
    }

    public Treeitem renderAddedMenu(Treeitem parent, String name) {
        Treeitem item = getNewTreeitem(name, "img/FOLDER.png");
        if (parent.getTreechildren() == null) {
            new Treechildren().setParent(parent);
        }
        item.setParent(parent.getTreechildren());
        item.addEventListener(Events.ON_DROP, this);
        item.setAttribute(ELEMENT_MENU, true);
        item.setDroppable(ELEMENT_PANEL);

        return item;
    }

    private org.web4thejob.web.panel.Panel getPanelSafe(String beanid) {
        org.web4thejob.web.panel.Panel panel = null;
        try {
            panel = ContextUtil.getPanel(beanid);
        } catch (Exception ignore) {
        }
        return panel;
    }

    public Treeitem renderAddedPanel(Treeitem parent, org.web4thejob.web.panel.Panel panel) {
        Treeitem item = getNewTreeitem(panel.toString(), panel.getImage());
        if (parent.getTreechildren() == null) {
            new Treechildren().setParent(parent);
        }
        parent.setOpen(true);
        item.setParent(parent.getTreechildren());
        item.setDroppable(null);
        item.setAttribute(ELEMENT_PANEL, panel.getBeanName());

        return item;
    }

    private void appendXMLNode(Element parentNode, Treeitem item) {
        Element childElement;
        if (item.hasAttribute(ELEMENT_MENU)) {
            childElement = new Element(ELEMENT_MENU);
            childElement.appendChild(getTreeitemLabel(item));
        } else if (item.hasAttribute(ELEMENT_PANEL)) {
            childElement = new Element(ELEMENT_PANEL);
            childElement.appendChild(item.getAttribute(ELEMENT_PANEL).toString());
        } else {
            throw new IllegalStateException();
        }

        parentNode.appendChild(childElement);

        if (item.getTreechildren() != null) {
            for (Treeitem childItem : item.getTreechildren().getItems()) {
                if (childItem.getParentItem().equals(item)) {
                    appendXMLNode(childElement, childItem);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.ADD.equals(command.getId())) {
            if (canAddMenu()) {
                final ValueInputDialog dialog = ContextUtil.getDialog(DefaultValueInputDialog.class);
                dialog.setL10nMode(getL10nMode());
                dialog.show(this);
            }
        } else if (CommandEnum.EDIT.equals(command.getId())) {
            if (canAddMenu()) {
                String name = getTreeitemLabel(tree.getSelectedItem());
                final ValueInputDialog dialog = ContextUtil.getDialog(DefaultValueInputDialog.class, name);
                dialog.setL10nMode(getL10nMode());
                dialog.show(this);
            }
        } else if (CommandEnum.REMOVE.equals(command.getId())) {
            if (tree.getSelectedItem() != null) {
                if (!tree.getSelectedItem().hasAttribute(ITEM_ROOT) && (tree.getSelectedItem().hasAttribute
                        (ELEMENT_MENU) || tree.getSelectedItem().hasAttribute(ELEMENT_PANEL))) {
                    tree.getSelectedItem().detach();
                    arrangeForState(PanelState.READY);
                }
            }
        } else if (CommandEnum.MOVE_LEFT.equals(command.getId())) {
            if (tree.getSelectedItem() != null && listbox.getSelectedItem() != null) {
                if (tree.getSelectedItem().hasAttribute(ELEMENT_MENU)) {
                    renderAddedPanel(tree.getSelectedItem(), (org.web4thejob.web.panel.Panel) ((ListModelList) listbox
                            .getModel())
                            .getSelection().iterator().next());
                }
            }
        } else if (CommandEnum.MOVE_UP.equals(command.getId())) {
            if (tree.getSelectedItem() != null) {
                Treeitem item = tree.getSelectedItem();
                Treeitem sibling = (Treeitem) item.getPreviousSibling();
                swap(sibling, item);
                tree.setSelectedItem(item);
            }
        } else if (CommandEnum.MOVE_DOWN.equals(command.getId())) {
            if (tree.getSelectedItem() != null) {
                Treeitem item = tree.getSelectedItem();
                Treeitem sibling = (Treeitem) item.getNextSibling();
                swap(item, sibling);
                tree.setSelectedItem(item);
            }
        } else {
            super.processValidCommand(command);
        }
    }

    private void swap(Treeitem item1, Treeitem item2) {
        if (item1 != null && item2 != null) {
            item2.detach();
            item1.getParentItem().getTreechildren().insertBefore(item2, item1);
        }
    }

    public Treeitem getRootItem() {
        return rootItem;
    }

    protected Treeitem getNewTreeitem(String label, String img) {
        Treeitem item = new Treeitem();
        new Treerow().setParent(item);

        Treecell cell = new Treecell();
        cell.setStyle("white-space: nowrap;");
        cell.setParent(item.getTreerow());
        cell.setImage(img);

        Html html = new Html(label);
        html.setParent(cell);
        html.setStyle("margin-left: 5px;");

        return item;
    }

    protected String getTreeitemLabel(Treeitem item) {
        return ((Html) item.getTreerow().getFirstChild().getFirstChild()).getContent();
    }

    protected void updateTreeitemLabel(Treeitem item, String label) {
        ((Html) item.getTreerow().getFirstChild().getFirstChild()).setContent(label);
    }

}
