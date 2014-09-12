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

import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.util.StringUtils;
import org.web4thejob.SystemProtectedEntryException;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.orm.*;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.dialog.EntityPersisterDialog;
import org.web4thejob.web.panel.base.zk.AbstractZkBindablePanel;
import org.web4thejob.web.panel.base.zk.AbstractZkTargetTypeAwarePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 3.5.2
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@SuppressWarnings({"unsafe", "unchecked"})
public class DefaultEntityHierarchyPanel extends AbstractZkTargetTypeAwarePanel implements EntityHierarchyPanel,
        EventListener<Event> {
    public DefaultEntityHierarchyPanel() {
        this(false, true);
    }

    public DefaultEntityHierarchyPanel(boolean readOnly, boolean showChildren) {
        this.readOnly = readOnly;
        this.showChildren = showChildren;
        ZkUtil.setParentOfChild((Component) base, tree);
        tree.setVflex("true");
        tree.setWidth("100%");
        tree.addEventListener(Events.ON_SELECT, this);
        new Treechildren().setParent(tree);
    }
    private static final String ATTRIB_HIERARCHY = "ATTRIB_HIERARCHY";
    private static final String ATTRIB_ITEM = "ATTRIB_ITEM";
    private static final String ON_OPEN_ECHO = Events.ON_OPEN + "Echo";
    private static final String ON_DOUBLE_CLICK_ECHO = Events.ON_DOUBLE_CLICK + "Echo";
    public static final L10nString L10N_MSG_CANNOT_DELETE_PARENT = new L10nString(EntityHierarchyPanel.class,
            "message_cannot_delete_parent", "This item cannot be deleted because it has child items. Remove child " +
            "items and retry.");
    private final Tree tree = new Tree();
    private final boolean showChildren;
    private final boolean readOnly;
    private EntityHierarchy HIERARCHY_INSTANCE;

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.REFRESH);
        supported.add(CommandEnum.ADDNEW);
        supported.add(CommandEnum.UPDATE);
        supported.add(CommandEnum.DELETE);
        supported.add(CommandEnum.MOVE_UP);
        supported.add(CommandEnum.MOVE_DOWN);
        return Collections.unmodifiableSet(supported);
    }

    @Override
    protected void arrangeForTargetType() {

        HIERARCHY_INSTANCE = getEntityHierarchyInstance();
        if (!readOnly) {
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.REFRESH, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.ADDNEW, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.UPDATE, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.DELETE, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_UP, this));
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_DOWN, this));
        }
        renderRootItem();
    }

    @Override
    protected void arrangeForNullTargetType() {
        super.arrangeForNullTargetType();
        HIERARCHY_INSTANCE = null;
    }

    private void renderRootItem() {
        tree.getTreechildren().getChildren().clear();
        if (hasTargetType() && HIERARCHY_INSTANCE != null) {
            List<EntityHierarchyItem> roots = ContextUtil.getDRS().findByQuery(HIERARCHY_INSTANCE.getRootItems());
            for (EntityHierarchyItem root : roots) {
                Treeitem rootItem = getNewTreeitem(root);
                rootItem.setParent(tree.getTreechildren());
                if (root instanceof EntityHierarchyParent) {
                    renderChildren(rootItem, (EntityHierarchyParent) root);
                }
            }
            arrangeForState(PanelState.READY);
        } else {
            arrangeForState(PanelState.UNDEFINED);
        }
    }

    private void renderChildren(final Treeitem parentNode, final EntityHierarchyParent parentItem) {

        ContextUtil.getTransactionWrapper().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Set<EntityHierarchy<EntityHierarchyParent, EntityHierarchyItem>> children = ((EntityHierarchyParent)
                        ContextUtil.getDRS().get(parentItem.getEntityType(), parentItem.getIdentifierValue()))
                        .getChildren();
                if (!children.isEmpty()) {
                    if (parentNode.getTreechildren() == null) {
                        new Treechildren().setParent(parentNode);
                    }
                    parentNode.getTreechildren().getChildren().clear();
                    for (EntityHierarchy hierarchyItem : children) {
                        EntityHierarchyItem childItem = hierarchyItem.getChild();

                        if (!showChildren && !(childItem instanceof EntityHierarchyParent)) continue;

                        Treeitem newNode = getNewTreeitem(childItem);
                        newNode.setParent(parentNode.getTreechildren());
                        newNode.setAttribute(ATTRIB_HIERARCHY, hierarchyItem);

                        if (childItem instanceof EntityHierarchyParent) {
                            if (!((EntityHierarchyParent) childItem).getChildren().isEmpty()) {
                                new Treechildren().setParent(newNode);
                                newNode.setOpen(false);
                                newNode.addEventListener(Events.ON_OPEN, DefaultEntityHierarchyPanel.this);
                                newNode.addEventListener(ON_OPEN_ECHO, DefaultEntityHierarchyPanel.this);
                            }
                        }
                    }
                    if (parentNode.getTreechildren().getItemCount() == 0) {
                        parentNode.getTreechildren().detach();
                    }
                }
            }
        });

    }

    @Override
    protected void arrangeForState(PanelState newState) {
        super.arrangeForState(newState);
        if (newState == PanelState.FOCUSED && !readOnly) {
            boolean selected = tree.getSelectedItem() != null;
            if (hasCommand(CommandEnum.MOVE_UP)) {
                getCommand(CommandEnum.MOVE_UP).setActivated(selected);
            }
            if (hasCommand(CommandEnum.MOVE_DOWN)) {
                getCommand(CommandEnum.MOVE_DOWN).setActivated(selected);
            }
            if (hasCommand(CommandEnum.UPDATE)) {
                getCommand(CommandEnum.UPDATE).setActivated(selected);
            }
            if (hasCommand(CommandEnum.DELETE)) {
                getCommand(CommandEnum.DELETE).setActivated(selected);
            }
        }
        if (hasCommand(CommandEnum.REFRESH)) {
            getCommand(CommandEnum.REFRESH).setActivated(hasTargetType());
        }
        if (hasCommand(CommandEnum.ADDNEW)) {
            getCommand(CommandEnum.ADDNEW).setActivated(hasTargetType());
        }
    }

    @Override
    public void processMessage(Message message) {
        if (message.getId() == MessageEnum.ENTITY_UPDATED) {
            if (canBind(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class))) {
                final EntityHierarchyItem ehi = message.getArg(MessageArgEnum.ARG_ITEM, EntityHierarchyItem.class);
                for (Treeitem item : getMatchingItems(ehi)) {
                    updateTreeitemLabel(item, ehi.toString());
                }
            }
        } else if (message.getId() == MessageEnum.ENTITY_DELETED) {
            if (canBind(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class))) {
                final EntityHierarchyItem ehi = message.getArg(MessageArgEnum.ARG_ITEM, EntityHierarchyItem.class);
                for (Treeitem item : getMatchingItems(ehi)) {
                    if (item.getParent() != null && item.getParent().getChildren().size() == 1) {
                        item.getParent().detach();
                    } else {
                        item.detach();
                    }
                }
            }
        } else {
            super.processMessage(message);
        }
    }

    protected boolean canBind(Entity entity) {
        if (entity == null) {
            return false;
        } else if (hasTargetType() && HIERARCHY_INSTANCE != null) {
            return HIERARCHY_INSTANCE.getParentType().isInstance(entity) || HIERARCHY_INSTANCE.getChildType()
                    .isInstance(entity);
        }

        return false;
    }

    protected List<Treeitem> getMatchingItems(EntityHierarchyItem ehi) {
        List<Treeitem> matched = new ArrayList<Treeitem>();

        for (Treeitem root : tree.getTreechildren().getItems()) {
            if (ehi.equals(root.getAttribute(ATTRIB_ITEM))) {
                matched.add(root);
            }

            if (root.getTreechildren() != null) {
                for (Treeitem childItem : root.getTreechildren().getItems()) {
                    getMatchingItemsRecursive(childItem, ehi, matched);
                }
            }
        }


        return matched;
    }

    protected List<Treeitem> getMatchingItemsRecursive(Treeitem parentItem, final EntityHierarchyItem ehi,
                                                       List<Treeitem> matched) {

        if (ehi.equals(parentItem.getAttribute(ATTRIB_ITEM))) {
            matched.add(parentItem);
        }

        if (parentItem.getTreechildren() != null) {
            for (Treeitem childItem : parentItem.getTreechildren().getItems()) {
                getMatchingItemsRecursive(childItem, ehi, matched);
            }
        }

        return matched;
    }

    public void onEvent(Event event) throws Exception {
        if (Events.ON_SELECT.equals(event.getName())) {
            arrangeForState(PanelState.FOCUSED);
            EntityHierarchyItem ehi = (EntityHierarchyItem) tree.getSelectedItem().getAttribute(ATTRIB_ITEM);
            Message message = ContextUtil.getMessage(MessageEnum.ENTITY_SELECTED, this, MessageArgEnum.ARG_ITEM, ehi);
            dispatchMessage(message);
        } else if (Events.ON_OPEN.equals(event.getName())) {
            showBusy();
            event.getTarget().removeEventListener(Events.ON_OPEN, this);
            Events.echoEvent(new OpenEvent(ON_OPEN_ECHO, event.getTarget(), ((OpenEvent) event).isOpen()));
        } else if (ON_OPEN_ECHO.equals(event.getName())) {
            clearBusy();
            event.getTarget().removeEventListener(ON_OPEN_ECHO, this);
            EntityHierarchyItem hierarchyItem = ((EntityHierarchy) event.getTarget().getAttribute
                    (ATTRIB_HIERARCHY)).getChild();
            if (hierarchyItem instanceof EntityHierarchyParent) {
                renderChildren((Treeitem) event.getTarget(), (EntityHierarchyParent) hierarchyItem);
            }
        } else if (Events.ON_DOUBLE_CLICK.equals(event.getName())) {
            Treeitem target = ((Treeitem) event.getTarget());
            if (target.getAttribute(ATTRIB_ITEM) instanceof EntityHierarchyItem) {
                if (ZkUtil.isDialogContained(target)) {
                    Message message = ContextUtil.getMessage(MessageEnum.ENTITY_ACCEPTED, this, MessageArgEnum.ARG_ITEM,
                            target.getAttribute(ATTRIB_ITEM));
                    dispatchMessage(message);
                } else {
                    Clients.showBusy(null);
                    Events.echoEvent(ON_DOUBLE_CLICK_ECHO, target, null);
                }
            }
        } else if (ON_DOUBLE_CLICK_ECHO.equals(event.getName())) {
            Clients.clearBusy();
            EntityHierarchyItem ehi = (EntityHierarchyItem) event.getTarget().getAttribute(ATTRIB_ITEM);
            Panel entityPanel = CoreUtil.getEntityViewPanel(ehi);
            if (entityPanel != null) {
                dispatchMessage(ContextUtil.getMessage(MessageEnum.ADOPT_ME, entityPanel));
            }
        } else if (Events.ON_DROP.equals(event.getName())) {
            DropEvent dropEvent = (DropEvent) event;
            Treeitem target = ((Treeitem) dropEvent.getTarget());
            EntityHierarchyParent parent = (EntityHierarchyParent) target.getAttribute(ATTRIB_ITEM);
            EntityHierarchyItem child = null;
            Treeitem dragged = null;
            Treechildren dragged_source = null;

            if (dropEvent.getDragged() instanceof Treeitem) {
                dragged = ((Treeitem) dropEvent.getDragged());
                dragged_source = (Treechildren) dragged.getParent();

                if (Components.isAncestor(dragged, target) ||
                        (dragged.getParent() != null && dragged.getParent().equals(target.getTreechildren()))) {
                    return;
                }

                child = (EntityHierarchyItem) dragged.getAttribute(ATTRIB_ITEM);

            } else if (dropEvent.getDragged() instanceof Listitem) {
                Listitem draggedItem = (Listitem) dropEvent.getDragged();
                ListModelList model = (ListModelList) draggedItem.getListbox().getModel();
                Entity draggedEntity = (Entity) model.getElementAt(draggedItem.getIndex());

                if (!(HIERARCHY_INSTANCE.getParentType().isInstance(draggedEntity) ||
                        HIERARCHY_INSTANCE.getChildType().isInstance(draggedEntity))) {
                    return;
                }

                child = (EntityHierarchyItem) draggedEntity;
                dragged = getNewTreeitem(child);

            }

            if (parent != null && child != null) {
                if (target.getTreechildren() == null) {
                    new Treechildren().setParent(dropEvent.getTarget());
                }

                for (Component item : target.getTreechildren().getChildren()) {
                    if (child.equals(item.getAttribute(ATTRIB_ITEM))) {
                        tree.clearSelection();
                        tree.setSelectedItem((Treeitem) item);
                        return;
                    }
                }

                EntityHierarchy eh = (EntityHierarchy) dragged.getAttribute(ATTRIB_HIERARCHY);
                if (eh == null) {
                    eh = getEntityHierarchyInstance();
                }

                eh.setParent(parent);
                eh.setChild(child);
                eh.setSorting(target.getTreechildren().getItemCount() + 1);

                Query query = ContextUtil.getEntityFactory().buildQuery(getTargetType());
                query.addCriterion(new Path(HIERARCHY_INSTANCE.getParentPropertyName()), Condition.EQ, parent);
                query.addCriterion(new Path(HIERARCHY_INSTANCE.getChildPropertyName()), Condition.EQ, child);
                if (ContextUtil.getDRS().findFirstByQuery(query) == null) {
                    ContextUtil.getDWS().save(eh);

                    dragged.setParent(null);
                    dragged.setParent(target.getTreechildren());
                    dragged.setAttribute(ATTRIB_HIERARCHY, eh);
                    dragged.setSelected(true);
                }

                if (dragged_source != null && dragged_source.getChildren() != null && dragged_source.getChildren()
                        .isEmpty()) {
                    dragged_source.setParent(null);
                }
            }

        }

    }

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.REFRESH.equals(command.getId())) {
            if (hasTargetType()) {
                renderRootItem();
            }
        } else if (CommandEnum.MOVE_UP.equals(command.getId())) {
            if (tree.getSelectedItem() != null) {
                Treeitem item = tree.getSelectedItem();
                Treeitem sibling = (Treeitem) item.getPreviousSibling();
                swap(sibling, item);
                tree.clearSelection();
                tree.setSelectedItem(item);
            }
        } else if (CommandEnum.MOVE_DOWN.equals(command.getId())) {
            if (tree.getSelectedItem() != null) {
                Treeitem item = tree.getSelectedItem();
                Treeitem sibling = (Treeitem) item.getNextSibling();
                swap(item, sibling);
                tree.clearSelection();
                tree.setSelectedItem(item);
            }
        } else if (CommandEnum.DELETE.equals(command.getId())) {
            if (tree.getSelectedItem() != null) {
                final Treeitem item = tree.getSelectedItem();
                final EntityHierarchyItem ehi = ((EntityHierarchyItem) item.getAttribute(ATTRIB_ITEM));
                final EntityHierarchy eh = ((EntityHierarchy) item.getAttribute(ATTRIB_HIERARCHY));
                if (eh != null) {
                    ContextUtil.getDWS().delete(eh);
                    if (item.getParent().getChildren().size() == 1) {
                        item.getParent().detach();
                    } else {
                        item.detach();
                    }

                    if (ehi instanceof EntityHierarchyParent) {
                        item.setParent(tree.getTreechildren());
                        item.removeAttribute(ATTRIB_HIERARCHY);
                        tree.clearSelection();
                        tree.setSelectedItem(item);
                    }
                } else if (ehi instanceof EntityHierarchyParent) {
                    if (item.getTreechildren() != null && item.getTreechildren().getItemCount() > 0) {
                        ZkUtil.displayMessage(L10N_MSG_CANNOT_DELETE_PARENT.toString(), false, item);
                        return;
                    }

                    Messagebox.show(AbstractZkBindablePanel.L10N_MSG_DELETE_CONFIRMATION.toString(),
                            L10nMessages.L10N_MSGBOX_TITLE_QUESTION
                                    .toString(), new Messagebox.Button[]{Messagebox.Button.OK,
                            Messagebox.Button.CANCEL},
                            null, Messagebox.QUESTION, Messagebox.Button.CANCEL,
                            new EventListener<Messagebox.ClickEvent>() {
                                public void onEvent(Messagebox.ClickEvent event) throws Exception {
                                    if (Messagebox.Button.OK == event.getButton()) {
                                        Message message = ContextUtil.getMessage(MessageEnum.ENTITY_DELETED,
                                                this,
                                                MessageArgEnum.ARG_ITEM, ehi);
                                        try {
                                            ContextUtil.getDWS().delete(ehi);
                                            item.detach();
                                            dispatchMessage(message);
                                        } catch (HibernateOptimisticLockingFailureException e) {
                                            displayMessage(AbstractZkBindablePanel
                                                    .L10N_MSG_ENTITY_MODIFIED_BY_OTHERS.toString(), true);
                                        } catch (SystemProtectedEntryException e) {
                                            displayMessage(e.getMessage(), true);
                                        } catch (Exception e) {
                                            displayMessage(AbstractZkBindablePanel.L10N_MSG_DELETION_FAILED
                                                    .toString(), true);
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                }
            }
        } else if (CommandEnum.ADDNEW.equals(command.getId())) {
            EntityHierarchyItem templEntity;
            try {
                templEntity = (EntityHierarchyItem) HIERARCHY_INSTANCE.getParentType().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            Set<Setting<?>> settings = new HashSet<Setting<?>>(1);
            settings.add(ContextUtil.getSetting(SettingEnum.TARGET_TYPE, templEntity.getEntityType()));
            EntityPersisterDialog dialog = ContextUtil.getDefaultDialog(EntityPersisterDialog.class,
                    templEntity, settings, MutableMode.INSERT, true);
            dialog.setL10nMode(getL10nMode());
            dialog.show(new MessageListener() {
                public void processMessage(Message message) {
                    if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId()) {
                        List<Entity> toSave = new ArrayList<Entity>(2);
                        EntityHierarchyItem child = message.getArg(MessageArgEnum.ARG_ITEM, EntityHierarchyItem.class);
                        toSave.add(child);

                        Treeitem treeNode = tree.getSelectedItem();
                        EntityHierarchy hierarchy = null;
                        if (treeNode != null) {
                            EntityHierarchyItem ehi = (EntityHierarchyItem) treeNode.getAttribute(ATTRIB_ITEM);
                            if (!(ehi instanceof EntityHierarchyParent)) {
                                //try to find a parent
                                Component comp = treeNode.getParent();
                                treeNode = null;
                                while (comp != null) {
                                    if (comp.getAttribute(ATTRIB_ITEM) instanceof EntityHierarchyParent) {
                                        treeNode = (Treeitem) comp;
                                        ehi = (EntityHierarchyParent) comp.getAttribute(ATTRIB_ITEM);
                                        break;
                                    }
                                    comp = comp.getParent();
                                }
                            }

                            if (treeNode == null) return; //error

                            if (treeNode.getTreechildren() == null) {
                                new Treechildren().setParent(treeNode);
                            }
                            hierarchy = getEntityHierarchyInstance();
                            EntityHierarchyParent parentItem = (EntityHierarchyParent) ehi;
                            hierarchy.setParent(parentItem);
                            hierarchy.setChild(child);
                            hierarchy.setSorting(treeNode.getTreechildren().getItemCount() + 1);
                            toSave.add(hierarchy);
                        }

                        ContextUtil.getDWS().save(toSave);

                        Treeitem newItem = getNewTreeitem(child);

                        if (hierarchy != null) {
                            newItem.setParent(treeNode.getTreechildren());
                            newItem.setAttribute(ATTRIB_HIERARCHY, hierarchy);
                        } else {
                            newItem.setParent(tree.getTreechildren());
                        }
                    }
                }
            });
        } else if (CommandEnum.UPDATE.equals(command.getId())) {
            if (tree.getSelectedItem() != null) {
                final Treeitem item = tree.getSelectedItem();
                final EntityHierarchyItem ehi = ((EntityHierarchyItem) item.getAttribute(ATTRIB_ITEM));
                if (ehi != null) {
                    Set<Setting<?>> settings = new HashSet<Setting<?>>(1);
                    settings.add(ContextUtil.getSetting(SettingEnum.TARGET_TYPE,
                            ehi.getEntityType()));
                    EntityPersisterDialog dialog = ContextUtil.getDefaultDialog(EntityPersisterDialog.class,
                            ehi, settings, MutableMode.UPDATE);
                    dialog.setL10nMode(getL10nMode());
                    dialog.show(new MessageListener() {
                        public void processMessage(Message message) {
                            if (MessageEnum.ENTITY_UPDATED == message.getId()) {
                                EntityHierarchyItem ehi2 = message.getArg(MessageArgEnum.ARG_ITEM,
                                        EntityHierarchyItem.class);
                                item.setAttribute(ATTRIB_ITEM, ehi2);
                                updateTreeitemLabel(item, ehi2.toString());
                            }
                        }
                    });
                }
            }
        }
        super.processValidCommand(command);
    }

    private void swap(Treeitem item1, Treeitem item2) {
        if (item1 != null && item2 != null) {

            EntityHierarchy eh1 = ((EntityHierarchy) item1.getAttribute(ATTRIB_HIERARCHY));
            EntityHierarchy eh2 = ((EntityHierarchy) item2.getAttribute(ATTRIB_HIERARCHY));

            if (eh1 == null || eh2 == null) return;

            long tmp = eh1.getSorting();
            eh1.setSorting(eh2.getSorting());
            eh2.setSorting(tmp);
            List<EntityHierarchy> toSave = new ArrayList<EntityHierarchy>();
            Collections.addAll(toSave, eh1, eh2);
            ContextUtil.getDWS().save(toSave);

            item2.detach();
            item1.getParentItem().getTreechildren().insertBefore(item2, item1);
        }
    }

    private void updateTreeitemLabel(Treeitem item, String label) {
        //Treeitem -> Treerow    -> Treecell      -> Html
        ((Html) item.getTreerow().getFirstChild().getFirstChild()).setContent(label);
    }

    private Treeitem getNewTreeitem(EntityHierarchyItem item) {
        Treeitem newItem = new Treeitem();

        new Treerow().setParent(newItem);
        Treecell cell = new Treecell();
        cell.setParent(newItem.getTreerow());
        cell.setStyle("white-space:nowrap;");
        cell.setImage("img/" + (item instanceof EntityHierarchyParent ? "FOLDER" : "FILE") + ".png");
        Html html = new Html(item.toString());
        html.setStyle("margin-left: 5px;");
        html.setParent(cell);

        newItem.setAttribute(ATTRIB_ITEM, item);
        newItem.addEventListener(Events.ON_DOUBLE_CLICK, this);
        newItem.addEventListener(ON_DOUBLE_CLICK_ECHO, this);

        if (!readOnly) {
            if (item instanceof EntityHierarchyParent) {
                Set<String> subs = CoreUtil.getSubClasses(HIERARCHY_INSTANCE.getParentType());
                subs.addAll(CoreUtil.getSubClasses(HIERARCHY_INSTANCE.getChildType()));
                newItem.setDroppable(StringUtils.collectionToCommaDelimitedString(subs));
                newItem.addEventListener(Events.ON_DROP, DefaultEntityHierarchyPanel.this);
            }
            newItem.setDraggable(item.getEntityType().getCanonicalName());
        }
        return newItem;
    }

    private EntityHierarchy getEntityHierarchyInstance() {
        try {
            return (EntityHierarchy) getTargetType().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EntityHierarchyItem getSelectedItem() {
        if (tree.getSelectedItem() != null) {
            return (EntityHierarchyItem) tree.getSelectedItem().getAttribute(ATTRIB_ITEM);
        }
        return null;
    }

    public int getItemCount() {
        return tree.getItemCount();
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}

