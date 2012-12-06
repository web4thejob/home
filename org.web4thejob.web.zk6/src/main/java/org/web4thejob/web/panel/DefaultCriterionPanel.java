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
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.EntityFactory;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.annotation.ControllerHolder;
import org.web4thejob.orm.annotation.EntityTypeHolder;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.panel.base.zk.AbstractZkTargetTypeAwarePanel;
import org.web4thejob.web.util.ComboItemConverter;
import org.web4thejob.web.util.ListboxRenderer;
import org.web4thejob.web.util.ZkUtil;
import org.web4thejob.web.zbox.AbstractBox;
import org.web4thejob.web.zbox.PropertyBox;
import org.web4thejob.web.zbox.ValueListBox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.*;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultCriterionPanel extends AbstractZkTargetTypeAwarePanel implements CriterionPanel,
        EventListener<Event> {
    // ------------------------------ FIELDS ------------------------------

    public static final L10nString L10N_LIST_HEADER_FIXED = new L10nString(DefaultCriterionPanel.class,
            "list_header_fixed", "Fixed");
    public static final L10nString L10N_LIST_HEADER_PROPERTY = new L10nString(DefaultCriterionPanel.class,
            "list_header_property", "Property");
    public static final L10nString L10N_LIST_HEADER_CONDITION = new L10nString(DefaultCriterionPanel.class,
            "list_header_condition", "Condition");
    public static final L10nString L10N_LIST_HEADER_VALUE = new L10nString(DefaultCriterionPanel.class,
            "list_header_value", "Value");
    public static final L10nString L10N_BUTTON_CLEAR = new L10nString(DefaultCriterionPanel.class, "button_clear",
            "clear");
    public static final L10nString L10N_BUTTON_REMOVE = new L10nString(DefaultCriterionPanel.class, "button_remove",
            "remove");
    public static final String ATTRIB_HEADER_FIXED = "fixed";
    private static final String ATTRIB_CONDITION = "condition";
    private static final String ATTRIB_CLEAR = "clear";
    private static final String ATTRIB_REMOVE = "remove";
    private static final String ATTRIB_VALUEBOX = "valuebox";
    private static final String ATTRIB_CRITERION = "criterion";
    private static final String ATTRIB_BEANID = "beanid";
    private final Listbox listbox = new Listbox();
    //private DataBinder dataBinder;

    // --------------------------- CONSTRUCTORS ---------------------------

    public DefaultCriterionPanel() {
        ZkUtil.setParentOfChild((Component) base, listbox);
//        listbox.setWidth("100%");
        listbox.setVflex("true");
        listbox.setSpan(true);
        listbox.setModel(new ListModelList<Criterion>());
        listbox.addEventListener(Events.ON_SELECT, this);

        RenderElement renderElement;
        RenderScheme renderScheme = ContextUtil.getBean(EntityFactory.class).buildRenderScheme(Criterion.class);

        renderElement = renderScheme.addElement(ContextUtil.getMRS().getPropertyMetadata(Criterion.class,
                Criterion.FLD_FLAT_PROPERTY));
        renderElement.setFriendlyName(DefaultCriterionPanel.L10N_LIST_HEADER_PROPERTY.toString());

        renderElement = renderScheme.addElement(ContextUtil.getMRS().getPropertyMetadata(Criterion.class,
                Criterion.FLD_FLAT_CONDITION));
        renderElement.setFriendlyName(L10N_LIST_HEADER_CONDITION.toString());
        renderElement.setWidth("150px");

        renderElement = renderScheme.addElement(ContextUtil.getMRS().getPropertyMetadata(Criterion.class,
                Criterion.FLD_FLAT_VALUE));
        renderElement.setFriendlyName(L10N_LIST_HEADER_VALUE.toString());

        renderScheme.setPageSize(100);
        ContextUtil.getBean(ListboxRenderer.class).arrangeForRenderScheme(listbox, renderScheme);

        Listheader listheader = new Listheader();
        listheader.setParent(listbox.getListhead());
        listheader.setWidth("70px");

        listbox.setItemRenderer(new MutableListRenderer());
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface CriterionPanel ---------------------

    private void saveAll() {
        for (Listitem item : listbox.getItems()) {
            if (item.getAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DATA_BINDER) instanceof DataBinder) {
                ((DataBinder) item.getAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DATA_BINDER)).saveAll();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Criterion> getCriteria() {
        saveAll();
        return (List<Criterion>) listbox.getModel();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addCriterion(Criterion criterion) {
        ((ListModelList) listbox.getModel()).add(criterion);
    }

    @Override
    public void clearAll() {
        listbox.setModel(new ListModelList<Criterion>());
    }

    // --------------------- Interface EventListener ---------------------

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_SELECT.equals(event.getName())) {
            arrangeForState(PanelState.FOCUSED);
        }
    }

    // --------------------- Interface ListitemRenderer ---------------------

    @Override
    public void processMessage(Message message) {
        switch (message.getId()) {
            case PATH_SELECTED:
                if (hasTargetType() && message.getSender() instanceof TargetTypeAware) {
                    if (((TargetTypeAware) message.getSender()).hasTargetType()) {
                        if (((TargetTypeAware) message.getSender()).getTargetType().equals(getTargetType())) {
                            addCriterion(message.getArg(MessageArgEnum.ARG_ITEM, PathMetadata.class));
                        }
                    }
                }
                break;
            default:
                super.processMessage(message);
                break;
        }
    }

    // --------------------- Interface Panel ---------------------

    @Override
    public void render() {
        super.render();

        if (isInDesignMode() && getFixedHeader() == null) {
            appendFixedHeader();
        } else if (!isInDesignMode() && getFixedHeader() != null) {
            removeFixedHeader();
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addCriterion(PathMetadata pathMetadata) {
        Criterion criterion = ContextUtil.getMRS().newInstance(Criterion.class);
        criterion.setPropertyPath(pathMetadata);
        ((ListModelList) listbox.getModel()).add(criterion);
        Clients.scrollIntoView(listbox.getItemAtIndex(listbox.getItemCount() - 1));
        ((ListModelList) listbox.getModel()).addToSelection(criterion);
        arrangeForState(PanelState.FOCUSED);
        dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, this));
    }

    private void appendFixedHeader() {
        saveAll();
        ListModel<Criterion> listModel = listbox.getListModel();
        listbox.setModel((ListModelList<Criterion>) null);

        Listheader listheader = new Listheader(L10N_LIST_HEADER_FIXED.toString());
        listheader.setHflex("min");
        listheader.setAttribute(ATTRIB_HEADER_FIXED, true);
        listbox.getListhead().insertBefore(listheader, listbox.getListhead().getFirstChild());

        listbox.setModel(listModel);
    }

    @Override
    protected void arrangeForNullTargetType() {
        super.arrangeForNullTargetType();
        listbox.setModel(new ListModelList<Criterion>(0));
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

    private Listheader getFixedHeader() {
        for (Object item : listbox.getListhead().getChildren()) {
            if (((Listheader) item).hasAttribute(ATTRIB_HEADER_FIXED)) {
                return (Listheader) item;
            }
        }
        return null;
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

    @SuppressWarnings("unchecked")
    private void moveDown() {
        final int index = listbox.getSelectedIndex();
        if (index < 0 || index == listbox.getModel().getSize() - 1) {
            return;
        }

        final Criterion item = (Criterion) listbox.getModel().getElementAt(index);
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

        final Criterion item = (Criterion) listbox.getModel().getElementAt(index);
        if (item.isFixed()) return;
        ((ListModelList) listbox.getModel()).remove(index);
        ((ListModelList) listbox.getModel()).add(index - 1, item);
        listbox.setSelectedIndex(index - 1);
        dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, this));
    }

    @Override
    public void clear() {
        @SuppressWarnings({"rawtypes", "unchecked"}) ListModelList<Criterion> oldList = (ListModelList) listbox
                .getModel();
        ListModelList<Criterion> fixedOnlyList = new ListModelList<Criterion>();
        if (oldList != null) {
            saveAll();
            for (Criterion item : oldList) {
                if (item.isFixed()) {
                    fixedOnlyList.add(item);
                }
            }
        }
        listbox.setModel(fixedOnlyList);
        arrangeForState(PanelState.READY);
        dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, this));
    }

    private void removeFixedHeader() {
        saveAll();
        ListModel<Criterion> listModel = listbox.getListModel();
        listbox.setModel((ListModelList<Criterion>) null);

        getFixedHeader().detach();

        listbox.setModel(listModel);
    }

    private class MutableListRenderer implements ListitemRenderer<Criterion>, EventListener<Event> {
        final private static int tooltipLimit = 30;

        @Override
        @SuppressWarnings("unchecked")
        public void render(Listitem item, Criterion criterion, int index) throws Exception {
            final boolean enforceFixed = getFixedHeader() == null;

            Listcell listcell;
            DataBinder dataBinder = new DataBinder();
            item.setAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DATA_BINDER, dataBinder);

            if (enforceFixed && criterion.isFixed()) {
                listcell = new Listcell();
                listcell.setParent(item);
                PropertyBox propertyBox = new PropertyBox();
                propertyBox.setParent(listcell);
                propertyBox.setTooltipLimit(tooltipLimit);
                propertyBox.setValue(criterion.getPropertyPath().getFriendlyName());

                listcell = new Listcell();
                listcell.setParent(item);
                if (criterion.getCondition() != null) {
                    listcell.setLabel(criterion.getCondition().toString());
                }

                listcell = new Listcell();
                listcell.setParent(item);
                propertyBox = new PropertyBox();
                propertyBox.setParent(listcell);
                propertyBox.setTooltipLimit(tooltipLimit);
                propertyBox.setValue(criterion.getValue());

                listcell = new Listcell();
                listcell.setParent(item);
            } else {
                final String beanid = "criterion_" + System.identityHashCode(criterion);
                dataBinder.bindBean(beanid, criterion);

                if (!enforceFixed) {
                    Checkbox checkbox = new Checkbox();
                    listcell = new Listcell();
                    listcell.setParent(item);
                    checkbox.setParent(listcell);
                    ZkUtil.addBinding(dataBinder, checkbox, beanid, Criterion.FLD_FIXED);
                }

                listcell = new Listcell();
                listcell.setParent(item);
                PropertyBox propertyBox = new PropertyBox();
                propertyBox.setParent(listcell);
                propertyBox.setTooltipLimit(tooltipLimit);
                propertyBox.setValue(criterion.getPropertyPath().getFriendlyName());

                listcell = new Listcell();
                listcell.setParent(item);
                Combobox conditionCombobox = new Combobox();
                buildApplicableConditions(conditionCombobox, criterion.getPropertyPath().getLastStep());
                conditionCombobox.setHflex("true");
                conditionCombobox.setParent(listcell);
                conditionCombobox.setAttribute(ATTRIB_CONDITION, true);
                ZkUtil.addBinding(dataBinder, conditionCombobox, beanid, Criterion.FLD_CONDITION,
                        ComboItemConverter.class);
                conditionCombobox.addEventListener(Events.ON_CHANGE, this);
                if (conditionCombobox.getItemCount() > 0 && criterion.getCondition() == null) {
                    criterion.setCondition(getDefaultForType(criterion.getPropertyPath().getLastStep()));
                }
                //required in  arrangeValueBoxForCondition()
                conditionCombobox.setAttribute(ATTRIB_VALUEBOX, null);
                conditionCombobox.setAttribute(ATTRIB_CRITERION, criterion);
                conditionCombobox.setAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DATA_BINDER, dataBinder);
                conditionCombobox.setAttribute(ATTRIB_BEANID, beanid);


                listcell = new Listcell();
                listcell.setParent(item);
                arrangeValueBoxForCondition(listcell, conditionCombobox);


                listcell = new Listcell();
                listcell.setParent(item);
                Hlayout hlayout = new Hlayout();
                hlayout.setParent(listcell);
                hlayout.setSpacing("5px");
                Toolbarbutton clear = new Toolbarbutton();

                String image = CoreUtil.getCommandImage(CommandEnum.CLEAR, null);
                if (image != null) {
                    clear.setImage(image);
                } else {
                    clear.setLabel(L10N_BUTTON_CLEAR.toString());
                }
                clear.setTooltiptext(L10N_BUTTON_CLEAR.toString());
                clear.setParent(hlayout);
                clear.setAttribute(ATTRIB_CLEAR, item);
                clear.addEventListener(Events.ON_CLICK, this);

                Toolbarbutton remove = new Toolbarbutton();
                image = CoreUtil.getCommandImage(CommandEnum.REMOVE, null);
                if (image != null) {
                    remove.setImage(image);
                } else {
                    remove.setLabel(L10N_BUTTON_REMOVE.toString());
                }
                remove.setTooltiptext(L10N_BUTTON_REMOVE.toString());
                remove.setParent(hlayout);
                remove.setAttribute(ATTRIB_REMOVE, item);
                remove.addEventListener(Events.ON_CLICK, this);

                dataBinder.loadAll();
            }

        }

        private Condition getDefaultForType(PropertyMetadata propertyMetadata) {
            if (propertyMetadata.isAnnotatedWith(ControllerHolder.class) || propertyMetadata.isAnnotatedWith
                    (EntityTypeHolder.class)) {
                return Condition.EQ;
            } else if (String.class.isAssignableFrom(propertyMetadata.getJavaType())) {
                return Condition.SW;
            } else if (Collection.class.isAssignableFrom(propertyMetadata.getJavaType())) {
                return Condition.EX;
            } else {
                return Condition.EQ;
            }
        }

        private void arrangeValueBoxForCondition(Component parent, Combobox combobox) {
            HtmlBasedComponent valuebox = (HtmlBasedComponent) combobox.getAttribute(ATTRIB_VALUEBOX);
            Criterion criterion = (Criterion) combobox.getAttribute(ATTRIB_CRITERION);
            DataBinder dataBinder = (DataBinder) combobox.getAttribute(org.web4thejob.web.panel.Attributes
                    .ATTRIB_DATA_BINDER);
            String beanid = (String) combobox.getAttribute(ATTRIB_BEANID);
            if (parent == null) {
                parent = valuebox.getParent();
            }
            Condition condition;

            if (combobox.getSelectedIndex() >= 0) {
                condition = (Condition) combobox.getSelectedItem().getValue();
            } else {
                condition = criterion.getCondition();
            }


            if (!ValueListBox.class.isInstance(valuebox) && (Condition.IN.equals(condition) || Condition.NIN.equals
                    (condition))) {

                if (valuebox != null) {
                    valuebox.detach();
                }
                valuebox = new ValueListBox(criterion.getPropertyPath());
            } else if (ValueListBox.class.isInstance(valuebox) && (!(Condition.IN.equals(condition) || Condition.NIN
                    .equals(condition)))) {

                if (valuebox != null) {
                    valuebox.detach();
                }
                valuebox = (HtmlBasedComponent) ZkUtil.getEditableComponentForPropertyType(criterion.getPropertyPath());
            } else if (condition != null && valuebox == null) {
                if (!Condition.IN.equals(criterion.getCondition()) && !Condition.NIN.equals(criterion.getCondition())) {
                    valuebox = (HtmlBasedComponent) ZkUtil.getEditableComponentForPropertyType(criterion
                            .getPropertyPath());
                } else {
                    valuebox = new ValueListBox(criterion.getPropertyPath());
                }
            }

            if (valuebox != null) {
                valuebox.setParent(parent);
                valuebox.setHflex("true");
                valuebox.setVisible(condition != null && condition.getOperandsNo() > 0);
                valuebox.addEventListener(Events.ON_CHANGE, this);
                valuebox.addEventListener(Events.ON_CHANGING, this);
                valuebox.addEventListener(Events.ON_CHECK, this);
                ZkUtil.addBinding(dataBinder, valuebox, beanid, Criterion.FLD_VALUE);

                if (valuebox instanceof AbstractBox) {
                    ((AbstractBox) valuebox).setTooltipLimit(tooltipLimit);
                    ((AbstractBox) valuebox).setHideClearLink(true);
                }

            }

            combobox.setAttribute(ATTRIB_VALUEBOX, valuebox);

        }

        // --------------------- Interface MessageListener ---------------------

        private void buildApplicableConditions(Combobox combobox, PropertyMetadata propertyMetadata) {
            combobox.getItems().clear();
            combobox.setReadonly(true);
            for (Condition condition : Condition.getApplicableToType(propertyMetadata)) {
                Comboitem item = new Comboitem(condition.toString());
                item.setParent(combobox);
                item.setValue(condition);
            }

        }

        @Override
        public void onEvent(Event event) throws Exception {
            if (Events.ON_CHANGE.equals(event.getName()) && event.getTarget().hasAttribute(ATTRIB_CONDITION)) {
                arrangeValueBoxForCondition(null, (Combobox) event.getTarget());
                //it is not a good idea to treat value changes as query change
                //dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, DefaultCriterionPanel.this));
            } else if (Events.ON_CLICK.equals(event.getName())) {
                if (event.getTarget().hasAttribute(ATTRIB_CLEAR)) {
                    Listitem item = (Listitem) event.getTarget().getAttribute(ATTRIB_CLEAR);
                    Criterion criterion = (Criterion) listbox.getModel().getElementAt(item.getIndex());
                    criterion.setValue(null);
                    ((DataBinder) item.getAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DATA_BINDER)).loadAll();
                    dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, DefaultCriterionPanel.this));
                } else if (event.getTarget().hasAttribute(ATTRIB_REMOVE)) {
                    Listitem item = (Listitem) event.getTarget().getAttribute(ATTRIB_REMOVE);
                    ((ListModelList) listbox.getModel()).remove(item.getIndex());
                    dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, DefaultCriterionPanel.this));
                    if (listbox.getModel().getSize() == 0) {
                        arrangeForState(PanelState.READY);
                    }
                }
            } else if (Events.ON_CHANGING.equals(event.getName()) || Events.ON_CHANGE.equals(event.getName()) ||
                    Events.ON_CHECK.equals(event.getName())) {
                //it is not a good idea to treat value changes as query change
                //dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, DefaultCriterionPanel.this));
            }
        }
    }
}


