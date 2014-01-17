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
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.EntityFactory;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.OrderBy;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.panel.base.zk.AbstractZkTargetTypeAwarePanel;
import org.web4thejob.web.util.ListboxRenderer;
import org.web4thejob.web.util.ZkUtil;
import org.web4thejob.web.zbox.PropertyBox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultOrderByPanel extends AbstractZkTargetTypeAwarePanel implements OrderByPanel, EventListener<Event> {
    public static final L10nString L10N_BUTTON_REMOVE = new L10nString(DefaultCriterionPanel.class, "button_remove",
            "remove");
    public static final L10nString L10N_LIST_HEADER_DESCENDING = new L10nString(DefaultOrderByPanel.class,
            "list_header_descending", "Descending");
    private static final String ATTRIB_REMOVE = "remove";


    private final Listbox listbox = new Listbox();

    public DefaultOrderByPanel() {
        ZkUtil.setParentOfChild((Component) base, listbox);
//        listbox.setWidth("100%");
        listbox.setVflex("true");
        listbox.setSpan(true);
        listbox.setModel(new ListModelList<Criterion>());
        listbox.addEventListener(Events.ON_SELECT, this);

        RenderElement renderElement;
        RenderScheme renderScheme = ContextUtil.getBean(EntityFactory.class).buildRenderScheme(OrderBy.class);

        renderElement = renderScheme.addElement(ContextUtil.getMRS().getPropertyMetadata(OrderBy.class,
                OrderBy.FLD_PROPERTY));
        renderElement.setFriendlyName(DefaultCriterionPanel.L10N_LIST_HEADER_PROPERTY.toString());

        renderElement = renderScheme.addElement(ContextUtil.getMRS().getPropertyMetadata(OrderBy.class,
                OrderBy.FLD_DESCENDING));
        renderElement.setFriendlyName(L10N_LIST_HEADER_DESCENDING.toString());
        renderElement.setWidth("100px");

        renderScheme.setPageSize(100);
        ContextUtil.getBean(ListboxRenderer.class).arrangeForRenderScheme(listbox, renderScheme);

        Listheader listheader = new Listheader();
        listheader.setParent(listbox.getListhead());
        listheader.setWidth("50px");

        listbox.setItemRenderer(new MutableListRenderer());


    }

    private void appendFixedHeader() {
        saveAll();
        ListModel<Criterion> listModel = listbox.getListModel();
        listbox.setModel((ListModelList<Criterion>) null);

        Listheader listheader = new Listheader(DefaultCriterionPanel.L10N_LIST_HEADER_FIXED.toString());
        listheader.setHflex("min");
        listheader.setAttribute(DefaultCriterionPanel.ATTRIB_HEADER_FIXED, true);
        listbox.getListhead().insertBefore(listheader, listbox.getListhead().getFirstChild());

        listbox.setModel(listModel);
    }

    private void saveAll() {
        for (Listitem item : listbox.getItems()) {
            if (item.getAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DATA_BINDER) instanceof DataBinder) {
                ((DataBinder) item.getAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DATA_BINDER)).saveAll();
            }
        }
    }

    @Override
    protected void arrangeForNullTargetType() {
        super.arrangeForNullTargetType();
        listbox.setModel(new ListModelList<OrderBy>(0));
        arrangeForState(PanelState.UNDEFINED);
    }

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.CLEAR);
        supported.add(CommandEnum.MOVE_UP);
        supported.add(CommandEnum.MOVE_DOWN);
        return Collections.unmodifiableSet(supported);
    }

    @Override
    protected void arrangeForTargetType() {
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.CLEAR, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_UP, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_DOWN, this));
        arrangeForState(PanelState.READY);
    }

    @Override
    public void render() {
        super.render();

        if (isInDesignMode() && getFixedHeader() == null) {
            appendFixedHeader();
        } else if (!isInDesignMode() && getFixedHeader() != null) {
            removeFixedHeader();
        }
    }

    @Override
    public void processMessage(Message message) {
        switch (message.getId()) {
            case PATH_SELECTED:
                if (hasTargetType() && message.getSender() instanceof TargetTypeAware) {
                    if (((TargetTypeAware) message.getSender()).hasTargetType()) {
                        if (((TargetTypeAware) message.getSender()).getTargetType().equals(getTargetType())) {
                            if (!message.getArg(MessageArgEnum.ARG_ITEM, PathMetadata.class).getLastStep()
                                    .isOneToManyType()) {
                                addOrderBy(message.getArg(MessageArgEnum.ARG_ITEM, PathMetadata.class));
                            }
                        }
                    }
                }
                break;
            default:
                super.processMessage(message);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void moveDown() {
        final int index = listbox.getSelectedIndex();
        if (index < 0 || index == listbox.getModel().getSize() - 1) {
            return;
        }

        final OrderBy item = (OrderBy) listbox.getModel().getElementAt(index);
        if (item.isFixed()) return;
        ((ListModelList) listbox.getModel()).remove(index);
        ((ListModelList) listbox.getModel()).add(index + 1, item);
        listbox.setSelectedIndex(index + 1);
        dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, this));
    }

    @SuppressWarnings("unchecked")
    private void moveUp() {
        final int index = listbox.getSelectedIndex();
        if (index <= 0 || listbox.getModel().getSize() == 1) {
            return;
        }

        final OrderBy item = (OrderBy) listbox.getModel().getElementAt(index);
        if (item.isFixed()) return;
        ((ListModelList) listbox.getModel()).remove(index);
        ((ListModelList) listbox.getModel()).add(index - 1, item);
        listbox.setSelectedIndex(index - 1);
        dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, this));
    }

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.CLEAR.equals(command.getId())) {
            clear();
        } else if (CommandEnum.MOVE_UP.equals(command.getId())) {
            moveUp();
        } else if (CommandEnum.MOVE_DOWN.equals(command.getId())) {
            moveDown();
        } else {
            super.processValidCommand(command);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addOrderBy(PathMetadata pathMetadata) {
        OrderBy orderBy = ContextUtil.getMRS().newInstance(OrderBy.class);
        orderBy.setProperty(pathMetadata.getPath());
        ((ListModelList) listbox.getModel()).add(orderBy);
        Clients.scrollIntoView(listbox.getItemAtIndex(listbox.getItemCount() - 1));
        ((ListModelList) listbox.getModel()).addToSelection(orderBy);
        arrangeForState(PanelState.FOCUSED);
        dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, this));
    }


    private void removeFixedHeader() {
        saveAll();
        ListModel<Criterion> listModel = listbox.getListModel();
        listbox.setModel((ListModelList<Criterion>) null);

        getFixedHeader().detach();

        listbox.setModel(listModel);
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_SELECT.equals(event.getName())) {
            arrangeForState(PanelState.FOCUSED);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<OrderBy> getOrderings() {
        saveAll();
        return (List<OrderBy>) listbox.getModel();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addOrderBy(OrderBy orderBy) {
        ((ListModelList) listbox.getModel()).add(orderBy);
    }

    @Override
    public void clear() {
        @SuppressWarnings({"rawtypes", "unchecked"}) ListModelList<OrderBy> oldList = (ListModelList) listbox
                .getModel();
        ListModelList<OrderBy> fixedOnlyList = new ListModelList<OrderBy>();
        if (oldList != null) {
            saveAll();
            for (OrderBy item : oldList) {
                if (item.isFixed()) {
                    fixedOnlyList.add(item);
                }
            }
        }
        listbox.setModel(fixedOnlyList);
        arrangeForState(PanelState.READY);
        dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, this));
    }

    @Override
    public void clearAll() {
        listbox.setModel(new ListModelList<OrderBy>());
    }

    private Listheader getFixedHeader() {
        for (Object item : listbox.getListhead().getChildren()) {
            if (((Listheader) item).hasAttribute(DefaultCriterionPanel.ATTRIB_HEADER_FIXED)) {
                return (Listheader) item;
            }
        }
        return null;
    }

    private class MutableListRenderer implements ListitemRenderer<OrderBy>, EventListener<Event> {

        @Override
        public void render(Listitem item, OrderBy orderBy, int index) throws Exception {
            final boolean enforceFixed = getFixedHeader() == null;
            Listcell listcell;
            String beanId = "orderby_" + System.identityHashCode(orderBy);
            DataBinder dataBinder = new DataBinder();
            item.setAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DATA_BINDER, dataBinder);

            if (enforceFixed && orderBy.isFixed()) {
                listcell = new Listcell(ContextUtil.getMRS().getPropertyPath(getTargetType(),
                        new Path(orderBy.getProperty())).getFriendlyName());
                listcell.setParent(item);

                listcell = new Listcell();
                listcell.setParent(item);
                PropertyBox comp = new PropertyBox(ContextUtil.getMRS().getPropertyPath(OrderBy.class,
                        new Path(OrderBy.FLD_DESCENDING)));
                comp.setParent(listcell);
                ZkUtil.addBinding(dataBinder, comp, beanId, OrderBy.FLD_DESCENDING);
            } else {
                if (!enforceFixed) {
                    Checkbox checkbox = new Checkbox();
                    listcell = new Listcell();
                    listcell.setParent(item);
                    checkbox.setParent(listcell);
                    checkbox.addEventListener(Events.ON_CHECK, this);
                    ZkUtil.addBinding(dataBinder, checkbox, beanId, OrderBy.FLD_FIXED);
                }

                listcell = new Listcell(ContextUtil.getMRS().getPropertyPath(getTargetType(),
                        new Path(orderBy.getProperty())).getFriendlyName());
                listcell.setParent(item);

                listcell = new Listcell();
                listcell.setParent(item);
                listcell.setStyle("text-align:center;");
                Checkbox checkbox = new Checkbox();
                checkbox.setParent(listcell);
                checkbox.addEventListener(Events.ON_CHECK, this);
                ZkUtil.addBinding(dataBinder, checkbox, beanId, OrderBy.FLD_DESCENDING);

                listcell = new Listcell();
                listcell.setParent(item);
                Toolbarbutton remove = new Toolbarbutton();
                String image = CoreUtil.getCommandImage(CommandEnum.CLEAR, null);
                if (image != null) {
                    remove.setImage(image);
                } else {
                    remove.setLabel(L10N_BUTTON_REMOVE.toString());
                }
                remove.setTooltiptext(L10N_BUTTON_REMOVE.toString());
                remove.setParent(listcell);
                remove.setAttribute(ATTRIB_REMOVE, item);
                remove.addEventListener(Events.ON_CLICK, this);

            }

            dataBinder.bindBean(beanId, orderBy);
            dataBinder.loadAll();
        }

        @Override
        public void onEvent(Event event) throws Exception {
            if (event.getTarget().hasAttribute(ATTRIB_REMOVE)) {
                Listitem item = (Listitem) event.getTarget().getAttribute(ATTRIB_REMOVE);
                ((ListModelList) listbox.getModel()).remove(item.getIndex());
                dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, DefaultOrderByPanel.this));
                if (listbox.getModel().getSize() == 0) {
                    arrangeForState(PanelState.READY);
                }
            } else if (Events.ON_CHECK.equals(event.getName())) {
                //it is not a good idea to treat asc/desc changes as query change
                //dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, DefaultOrderByPanel.this));
            }
        }
    }

}
